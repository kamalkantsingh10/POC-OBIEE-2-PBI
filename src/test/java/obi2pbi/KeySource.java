package obi2pbi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import obi2pbi.KbCompare.SourceElement;

/**
 * What an estate's repository files say about keys, column scale and place, time dimensions, the tables
 * and joins of a table source, the order of levels, parent-child tables and a few smaller attributes: the
 * facts behind deliberate differences 28 to 37. Read here with the JDK's streaming parser, a second time and with
 * different code from the jar's, so that {@link KbCompare} can check what the jar wrote instead of trusting
 * it.
 *
 * <p>From the files alone this works out
 * <ul>
 * <li>every row the three key tables must hold, cell for cell ({@code physical_keys}, {@code logical_keys},
 * {@code logical_level_keys});</li>
 * <li>the scale and the place of every physical column, and the time flag of every dimension;</li>
 * <li>for each kind of content the reference listed as not read and the jar now reads, how many occurrences
 * the jar must <b>still</b> list as not read ({@link Facts#remaining}), and which difference reads the rest;</li>
 * <li>how many keys the jar must count as read in {@code coverage}.</li>
 * </ul>
 * An identifier is built from the names in the files exactly as the port guide defines it. A name that
 * cannot be part of an identifier (blank, or containing {@code ::}) gives none, as in the jar.
 */
final class KeySource {

    static final String PHYSICAL_TABLE = "PhysicalTable";
    static final String LOGICAL_TABLE = "LogicalTable";
    static final String DIMENSION = "Dimension";
    static final String SOURCE = "LogicalTableSource";

    /** The facts of one estate. */
    static final class Facts {
        /** Row identifier to the row's cells in the table's column order; null where two keys share an identifier. */
        final Map<String, String[]> physicalKeys = new TreeMap<>();
        final Map<String, String[]> logicalKeys = new TreeMap<>();
        final Map<String, String[]> levelKeys = new TreeMap<>();
        /** The rows of lts_tables and lts_joins (difference 34), in the same form. */
        final Map<String, String[]> sourceTables = new TreeMap<>();
        final Map<String, String[]> sourceJoins = new TreeMap<>();
        /**
         * Table name, then Row identifier, to the cells of the columns the jar adds to that table, in
         * {@link KbCompare#ADDED_COLUMNS} order; null where two rows share an identifier.
         */
        final Map<String, Map<String, String[]>> added = new HashMap<>();
        /**
         * {@code <object type>|<kind>|<detail>} to how many occurrences the jar must still list as not read.
         * Only the kinds of content differences 28 to 32 read are in the map.
         */
        final Map<String, Integer> remaining = new HashMap<>();
        /** The same key to the difference that reads the rest. */
        final Map<String, Integer> difference = new HashMap<>();
        /** How many keys the jar must count as read: of physical tables, of logical tables, of levels. */
        int physicalKeyCount;
        int logicalKeyCount;
        int levelKeyCount;
        /** How many source tables and source joins the jar must count as read (difference 34). */
        int linkCount;
        int joinCount;

        Map<String, String[]> added(String table) {
            return added.computeIfAbsent(table, k -> new HashMap<>());
        }
    }

    private static final Map<String, Facts> CACHE = new HashMap<>();

    private KeySource() {
    }

    /** One open element while a file is read. */
    private static final class Open {
        final String tag;
        /** The Row identifier this element was given, or null. */
        String id;
        /** True when the element is a key the jar reads (its owner has an identifier). */
        boolean readKey;
        /** True for the first RefColumns child of a physical key or foreign key the jar reads. */
        boolean readList;
        int columns;
        int keys;
        boolean sawList;
        /** For a key being read: its cells so far. */
        String name = "";
        String primary = "";
        String chronological = "";
        String drillDown = "";
        final List<String> keyColumns = new ArrayList<>();
        /** Wrapper elements named RefColumns inside a logical key, each with whether a column link was found below it. */
        boolean hasColumnBelow;
        /** For a physical table: the text of its first description, and whether one was met. */
        StringBuilder description;
        boolean collecting;
        boolean sawDescription;
        String[] cells;
        /** For a level: the children it lists. For a dimension: its levels in order, and each level's children. */
        final List<String> children = new ArrayList<>();
        final List<Open> levels = new ArrayList<>();
        /** For a source table (Link): its row identifier. */
        int joins;

        Open(String tag) {
            this.tag = tag;
        }
    }

    /** Null when the estate folder has no repository files. */
    static Facts of(Path estateFolder, String estate) throws IOException {
        if (estateFolder == null) {
            return null;
        }
        Map<String, SourceElement> source = KbCompare.sourceElements(estateFolder);
        if (source.isEmpty()) {
            return null;
        }
        Path base = estateFolder.resolve("rpd").resolve("oracle").resolve("bi").resolve("server").resolve("base")
                .toAbsolutePath();
        String cacheKey = base + "|" + estate;
        Facts facts = CACHE.get(cacheKey);
        if (facts != null) {
            return facts;
        }
        facts = new Facts();
        // Which difference reads what. Everything named here was listed as not read by the reference.
        for (String detail : List.of("unread_element|PhysicalKey", "unread_attribute|PhysicalKey@name",
                "unread_element|RefColumns", "unread_element|RefColumn", "unread_attribute|RefColumn@columnRef")) {
            facts.difference.put(PHYSICAL_TABLE + "|" + detail, 28);
        }
        facts.difference.put(PHYSICAL_TABLE + "|unread_attribute|PhysicalColumn@scale", 31);
        for (String detail : List.of("unread_element|LogicalKey", "unread_attribute|LogicalKey@name",
                "unread_attribute|LogicalKey@isPrimary", "unread_element|RefColumns", "unread_element|RefLogicalColumn",
                "unread_attribute|RefLogicalColumn@logicalColumnRef")) {
            facts.difference.put(LOGICAL_TABLE + "|" + detail, 29);
            facts.difference.put(DIMENSION + "|" + detail, 30);
        }
        facts.difference.put(DIMENSION + "|unread_attribute|LogicalKey@isChronKey", 30);
        facts.difference.put(DIMENSION + "|unread_attribute|Dimension@isTimeDim", 32);
        for (String detail : List.of("unread_element|Link", "unread_attribute|Link@startNodeTableRef", "unread_element|Join",
                "unread_attribute|Join@type", "unread_attribute|Join@cardinality", "unread_attribute|Join@physicalTable1Ref",
                "unread_attribute|Join@physicalTable2Ref", "unread_attribute|Join@physicalJoinRef")) {
            facts.difference.put(SOURCE + "|" + detail, 34);
        }
        for (String detail : List.of("unread_element|RefChildLevels", "unread_element|RefLogicalLevel",
                "unread_attribute|RefLogicalLevel@logicalLevelRef")) {
            facts.difference.put(DIMENSION + "|" + detail, 35);
        }
        facts.difference.put(SOURCE + "|unread_attribute|LogicalTableSource@closureTableRef", 36);
        for (String attribute : List.of("memberKeyRef", "parentKeyRef", "distanceColumnRef", "leafColumnRef")) {
            facts.difference.put(PHYSICAL_TABLE + "|unread_attribute|PhysicalTable@" + attribute, 36);
        }
        facts.difference.put(PHYSICAL_TABLE + "|unread_element|Description", 37);
        facts.difference.put(PHYSICAL_TABLE + "|unread_text|Description#text", 37);
        facts.difference.put(PHYSICAL_TABLE + "|unread_attribute|PhysicalColumn@sourceColumnRef", 37);
        facts.difference.put(LOGICAL_TABLE + "|unread_attribute|LogicalTable@isLookupTable", 37);
        facts.difference.put(LOGICAL_TABLE + "|unread_attribute|LogicalColumn@descriptorIDRef", 37);
        for (String key : facts.difference.keySet()) {
            facts.remaining.put(key, 0);
        }

        // Logical column id (the source's own) to the Row identifier it must have: needed to state a key's columns.
        Map<String, String> logicalColumns = new HashMap<>();
        for (Map.Entry<String, SourceElement> entry : source.entrySet()) {
            SourceElement column = entry.getValue();
            if (!column.tag.equals("LogicalColumn") || !usable(column.name)) {
                continue;
            }
            String table = logicalTableIdentifier(estate, source.get(column.parent), column.parent, estateFolder, source);
            if (table != null) {
                logicalColumns.put(entry.getKey(), table + "." + column.name);
            }
        }
        // The same for levels (a level lists its children by id) and for logical tables (a source names its own).
        Map<String, String> levels = new HashMap<>();
        Map<String, String> logicalTables = new HashMap<>();
        for (Map.Entry<String, SourceElement> entry : source.entrySet()) {
            SourceElement element = entry.getValue();
            if (element.tag.equals("LogicalLevel") && usable(element.name)) {
                SourceElement dimension = source.get(element.parent);
                String model = dimension == null || !dimension.tag.equals(DIMENSION) || !usable(dimension.name) ? null
                        : modelName(estateFolder, DIMENSION, element.parent, source);
                if (model != null) {
                    levels.put(entry.getKey(), estate + "::bmm::" + model + "." + dimension.name + "." + element.name);
                }
            } else if (element.tag.equals(LOGICAL_TABLE)) {
                String id = logicalTableIdentifier(estate, element, entry.getKey(), estateFolder, source);
                if (id != null) {
                    logicalTables.put(entry.getKey(), id);
                }
            }
        }
        Resolver resolver = new Resolver(estate, source, logicalColumns, levels, logicalTables);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        for (String type : List.of(PHYSICAL_TABLE, LOGICAL_TABLE, DIMENSION, SOURCE)) {
            Path folder = base.resolve(type);
            if (!Files.isDirectory(folder)) {
                continue;
            }
            List<Path> files;
            try (Stream<Path> list = Files.list(folder)) {
                files = list.filter(p -> p.toString().endsWith(".xml") && Files.isRegularFile(p)).sorted()
                        .collect(Collectors.toList());
            }
            for (Path file : files) {
                try (InputStream in = Files.newInputStream(file)) {
                    read(factory.createXMLStreamReader(in), type, estate, source, logicalColumns, facts, resolver);
                } catch (XMLStreamException e) {
                    // A file that cannot be parsed gives no facts; what needs it stays unexplained.
                }
            }
        }
        CACHE.put(cacheKey, facts);
        return facts;
    }

    /** Turns a reference of the files into the Row identifier its target must have, from names alone. */
    private static final class Resolver {
        final String estate;
        final Map<String, SourceElement> source;
        final Map<String, String> logicalColumns;
        final Map<String, String> levels;
        final Map<String, String> logicalTables;

        Resolver(String estate, Map<String, SourceElement> source, Map<String, String> logicalColumns,
                Map<String, String> levels, Map<String, String> logicalTables) {
            this.estate = estate;
            this.source = source;
            this.logicalColumns = logicalColumns;
            this.levels = levels;
            this.logicalTables = logicalTables;
        }

        String physicalColumn(String reference) {
            SourceElement column = source.get(idOf(reference));
            String id = column == null ? null : KbCompare.physicalColumnIdentifier(estate, column, source);
            return id == null ? "" : id;
        }

        String physicalTable(String reference) {
            SourceElement table = source.get(idOf(reference));
            String id = table == null ? null : KbCompare.physicalTableIdentifier(estate, table, source);
            return id == null ? "" : id;
        }

        /** A foreign key of a physical table, or a complex join. */
        String physicalJoin(String reference) {
            SourceElement join = source.get(idOf(reference));
            if (join == null) {
                return "";
            }
            if (join.tag.equals("PhysicalForeignKey")) {
                SourceElement table = source.get(join.parent);
                String tableId = table == null ? null : KbCompare.physicalTableIdentifier(estate, table, source);
                return tableId == null ? "" : tableId + "." + (join.name.isEmpty() ? "fk" : join.name);
            }
            return join.tag.equals("ComplexJoin") && usable(join.name) ? estate + "::phys::joins." + join.name : "";
        }
    }

    private static void read(XMLStreamReader xml, String type, String estate, Map<String, SourceElement> source,
            Map<String, String> logicalColumns, Facts facts, Resolver resolver) throws XMLStreamException {
        List<Open> open = new ArrayList<>();
        while (xml.hasNext()) {
            int event = xml.next();
            if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA
                    || event == XMLStreamConstants.SPACE) {
                Open top = open.isEmpty() ? null : open.get(open.size() - 1);
                if (top != null && top.collecting) {
                    top.description.append(xml.getText());
                }
                continue;
            }
            if (event == XMLStreamConstants.END_ELEMENT) {
                Open closed = open.remove(open.size() - 1);
                Open parent = open.isEmpty() ? null : open.get(open.size() - 1);
                close(closed, parent, type, facts);
                continue;
            }
            if (event != XMLStreamConstants.START_ELEMENT) {
                continue;
            }
            String tag = xml.getLocalName();
            Open here = new Open(tag);
            Open parent = open.isEmpty() ? null : open.get(open.size() - 1);
            Open grand = open.size() < 2 ? null : open.get(open.size() - 2);
            String mdsid = attribute(xml, "mdsid");
            String name = attribute(xml, "name");
            if (parent == null) {
                // The file's root: the object itself.
                SourceElement element = source.get(mdsid);
                if (type.equals(PHYSICAL_TABLE) && tag.equals(PHYSICAL_TABLE) && element != null) {
                    here.id = KbCompare.physicalTableIdentifier(estate, element, source);
                    here.id = here.id != null && usable(name) ? here.id : null;
                    if (here.id != null) {
                        here.cells = new String[] {"", resolver.physicalColumn(attribute(xml, "memberKeyRef")),
                                resolver.physicalColumn(attribute(xml, "parentKeyRef")),
                                resolver.physicalColumn(attribute(xml, "distanceColumnRef")),
                                resolver.physicalColumn(attribute(xml, "leafColumnRef"))};
                    } else {
                        for (String attribute : List.of("memberKeyRef", "parentKeyRef", "distanceColumnRef", "leafColumnRef")) {
                            unread(facts, xml, attribute, PHYSICAL_TABLE + "|unread_attribute|PhysicalTable@" + attribute);
                        }
                    }
                } else if (type.equals(LOGICAL_TABLE) && tag.equals(LOGICAL_TABLE)) {
                    here.id = modelObject(estate, xml, source);
                    if (here.id != null) {
                        putCells(facts.added("logical_tables"), here.id, new String[] {attribute(xml, "isLookupTable")});
                    } else {
                        unread(facts, xml, "isLookupTable", LOGICAL_TABLE + "|unread_attribute|LogicalTable@isLookupTable");
                    }
                } else if (type.equals(DIMENSION) && tag.equals(DIMENSION)) {
                    here.id = modelObject(estate, xml, source);
                    if (here.id != null) {
                        putCells(facts.added("logical_dimensions"), here.id, new String[] {attribute(xml, "isTimeDim")});
                    } else {
                        unread(facts, xml, "isTimeDim", DIMENSION + "|unread_attribute|Dimension@isTimeDim");
                    }
                } else if (type.equals(SOURCE) && tag.equals(SOURCE)) {
                    String table = resolver.logicalTables.get(idOf(attribute(xml, "logicalTableRef")));
                    here.id = table != null && usable(name) ? table + ".src." + name : null;
                    if (here.id != null) {
                        putCells(facts.added("logical_table_sources"), here.id,
                                new String[] {resolver.physicalTable(attribute(xml, "closureTableRef"))});
                    } else {
                        unread(facts, xml, "closureTableRef", SOURCE + "|unread_attribute|LogicalTableSource@closureTableRef");
                    }
                }
            } else if (type.equals(SOURCE) && tag.equals("Link")) {
                boolean read = open.size() == 1 && parent.id != null;
                if (read) {
                    here.id = parent.id + "#table" + parent.keys;
                    putCells(facts.sourceTables, here.id, new String[] {here.id, "", parent.id, Integer.toString(parent.keys),
                            resolver.physicalTable(attribute(xml, "startNodeTableRef"))});
                    parent.keys++;
                    facts.linkCount++;
                } else {
                    count(facts, SOURCE + "|unread_element|Link");
                    unread(facts, xml, "startNodeTableRef", SOURCE + "|unread_attribute|Link@startNodeTableRef");
                }
            } else if (type.equals(SOURCE) && tag.equals("Join")) {
                boolean read = open.size() == 2 && parent.tag.equals("Link") && parent.id != null;
                if (read) {
                    String id = parent.id + ".join" + parent.joins;
                    putCells(facts.sourceJoins, id, new String[] {id, "", grand.id, parent.id, Integer.toString(parent.joins),
                            attribute(xml, "type"), attribute(xml, "cardinality"),
                            resolver.physicalTable(attribute(xml, "physicalTable1Ref")),
                            resolver.physicalTable(attribute(xml, "physicalTable2Ref")),
                            resolver.physicalJoin(attribute(xml, "physicalJoinRef"))});
                    parent.joins++;
                    facts.joinCount++;
                } else {
                    count(facts, SOURCE + "|unread_element|Join");
                    for (String attribute : List.of("type", "cardinality", "physicalTable1Ref", "physicalTable2Ref", "physicalJoinRef")) {
                        unread(facts, xml, attribute, SOURCE + "|unread_attribute|Join@" + attribute);
                    }
                }
            } else if (type.equals(PHYSICAL_TABLE) && tag.equals("Description")) {
                boolean read = open.size() == 1 && parent.id != null && !parent.sawDescription;
                if (open.size() == 1) {
                    parent.sawDescription = true;
                }
                if (read) {
                    here.collecting = true;
                    here.description = new StringBuilder();
                } else {
                    count(facts, PHYSICAL_TABLE + "|unread_element|Description");
                    here.collecting = true;
                    here.description = new StringBuilder();
                    here.readList = true;
                }
            } else if (type.equals(LOGICAL_TABLE) && open.size() == 1 && tag.equals("LogicalColumn")) {
                if (parent.id != null && usable(name)) {
                    putCells(facts.added("logical_columns"), parent.id + "." + name,
                            new String[] {logicalColumns.getOrDefault(idOf(attribute(xml, "descriptorIDRef")), "")});
                } else {
                    unread(facts, xml, "descriptorIDRef", LOGICAL_TABLE + "|unread_attribute|LogicalColumn@descriptorIDRef");
                }
            } else if (type.equals(DIMENSION) && tag.equals("RefChildLevels")) {
                here.readList = open.size() == 2 && parent.tag.equals("LogicalLevel") && parent.id != null && !parent.sawList;
                if (open.size() == 2 && parent.tag.equals("LogicalLevel")) {
                    parent.sawList = true;
                }
                if (!here.readList) {
                    count(facts, DIMENSION + "|unread_element|RefChildLevels");
                }
            } else if (type.equals(DIMENSION) && tag.equals("RefLogicalLevel")) {
                if (parent.tag.equals("RefChildLevels") && parent.readList) {
                    grand.children.add(resolver.levels.getOrDefault(idOf(attribute(xml, "logicalLevelRef")), ""));
                } else {
                    count(facts, DIMENSION + "|unread_element|RefLogicalLevel");
                    unread(facts, xml, "logicalLevelRef", DIMENSION + "|unread_attribute|RefLogicalLevel@logicalLevelRef");
                }
            } else if (type.equals(PHYSICAL_TABLE) && open.size() == 1 && tag.equals("PhysicalColumn")) {
                parent.columns++;
                boolean read = parent.id != null && usable(name);
                String scale = xml.getAttributeValue(null, "scale");
                if (read) {
                    putCells(facts.added("physical_columns"), parent.id + "." + name,
                            new String[] {scale == null ? "" : scale, Integer.toString(parent.columns),
                                    resolver.physicalColumn(attribute(xml, "sourceColumnRef"))});
                } else {
                    unread(facts, xml, "scale", PHYSICAL_TABLE + "|unread_attribute|PhysicalColumn@scale");
                    unread(facts, xml, "sourceColumnRef", PHYSICAL_TABLE + "|unread_attribute|PhysicalColumn@sourceColumnRef");
                }
            } else if (type.equals(PHYSICAL_TABLE) && open.size() == 1 && tag.equals("PhysicalKey")) {
                parent.keys++;
                keyStart(here, parent.id, name.isEmpty() ? "unnamed-" + parent.keys : name, xml);
                // In a table that has an identifier the jar asks every key for its name, whatever follows.
                if (parent.id == null) {
                    count(facts, PHYSICAL_TABLE + "|unread_element|PhysicalKey");
                    if (xml.getAttributeValue(null, "name") != null) {
                        count(facts, PHYSICAL_TABLE + "|unread_attribute|PhysicalKey@name");
                    }
                }
            } else if (type.equals(PHYSICAL_TABLE) && open.size() == 1 && tag.equals("PhysicalForeignKey")) {
                // Read by the jar and by the reference alike; here only so that its column list is told apart.
                here.readKey = parent.id != null;
            } else if (type.equals(PHYSICAL_TABLE) && tag.equals("RefColumns")) {
                boolean ofKey = open.size() == 2 && (parent.tag.equals("PhysicalKey") || parent.tag.equals("PhysicalForeignKey"));
                here.readList = ofKey && parent.readKey && !parent.sawList;
                if (ofKey) {
                    parent.sawList = true;
                }
                if (!here.readList) {
                    count(facts, PHYSICAL_TABLE + "|unread_element|RefColumns");
                }
            } else if (type.equals(PHYSICAL_TABLE) && tag.equals("RefColumn")) {
                boolean read = parent.tag.equals("RefColumns") && parent.readList;
                String reference = xml.getAttributeValue(null, "columnRef");
                if (!read) {
                    count(facts, PHYSICAL_TABLE + "|unread_element|RefColumn");
                    if (reference != null) {
                        count(facts, PHYSICAL_TABLE + "|unread_attribute|RefColumn@columnRef");
                    }
                } else if (grand != null && grand.tag.equals("PhysicalKey")) {
                    SourceElement column = source.get(idOf(reference));
                    String columnId = column == null ? null : KbCompare.physicalColumnIdentifier(estate, column, source);
                    grand.keyColumns.add(columnId == null ? "" : columnId);
                }
            } else if (type.equals(LOGICAL_TABLE) && open.size() == 1 && tag.equals("LogicalKey")) {
                parent.keys++;
                keyStart(here, parent.id, name.isEmpty() ? "unnamed-" + parent.keys : name, xml);
                notRead(here, parent.id != null, LOGICAL_TABLE, xml, facts);
            } else if (type.equals(DIMENSION) && open.size() == 1 && tag.equals("LogicalLevel")) {
                here.id = parent.id != null && usable(name) ? parent.id + "." + name : null;
                if (here.id != null) {
                    parent.levels.add(here);
                }
            } else if (type.equals(DIMENSION) && open.size() == 2 && tag.equals("LogicalKey") && parent.tag.equals("LogicalLevel")) {
                parent.keys++;
                keyStart(here, parent.id, name.isEmpty() ? "unnamed-" + parent.keys : name, xml);
                notRead(here, parent.id != null, DIMENSION, xml, facts);
            } else if (!type.equals(PHYSICAL_TABLE) && tag.equals("LogicalKey")) {
                // A key anywhere else is read by nothing.
                notRead(here, false, type, xml, facts);
            } else if (!type.equals(PHYSICAL_TABLE) && tag.equals("RefLogicalColumn")) {
                Open key = null;
                for (Open ancestor : open) {
                    key = ancestor.readKey ? ancestor : key;
                }
                String reference = xml.getAttributeValue(null, "logicalColumnRef");
                if (key == null) {
                    count(facts, type + "|unread_element|RefLogicalColumn");
                    if (reference != null) {
                        count(facts, type + "|unread_attribute|RefLogicalColumn@logicalColumnRef");
                    }
                } else {
                    key.keyColumns.add(logicalColumns.getOrDefault(idOf(reference), ""));
                    // Every element between the key and this link was walked through.
                    boolean inside = false;
                    for (Open ancestor : open) {
                        if (inside) {
                            ancestor.hasColumnBelow = true;
                        }
                        inside |= ancestor == key;
                    }
                }
            }
            open.add(here);
        }
        xml.close();
    }

    /** Starts a key the jar reads when its owner has an identifier and the key's name can be part of one. */
    private static void keyStart(Open key, String ownerId, String nameInIdentifier, XMLStreamReader xml) {
        key.readKey = ownerId != null && usable(nameInIdentifier);
        key.id = key.readKey ? ownerId + ".key." + nameInIdentifier : null;
        key.name = attribute(xml, "name");
        key.primary = attribute(xml, "isPrimary");
        key.chronological = attribute(xml, "isChronKey");
        key.drillDown = attribute(xml, "isForDrillDown");
    }

    /**
     * Counts what the jar leaves unread of a logical key it makes no row of. Where the key's owner has an
     * identifier the jar still asks the key for its name; the flags are read only for a key that becomes a row.
     */
    private static void notRead(Open key, boolean ownerHasIdentifier, String type, XMLStreamReader xml, Facts facts) {
        if (key.readKey) {
            return;
        }
        if (!ownerHasIdentifier) {
            count(facts, type + "|unread_element|LogicalKey");
        }
        for (String attribute : List.of("name", "isPrimary", "isChronKey")) {
            if (xml.getAttributeValue(null, attribute) != null && !(ownerHasIdentifier && attribute.equals("name"))) {
                count(facts, type + "|unread_attribute|LogicalKey@" + attribute);
            }
        }
    }

    /** Counts an attribute the jar does not read here as still not read, when the element has it. */
    private static void unread(Facts facts, XMLStreamReader xml, String attribute, String key) {
        if (xml.getAttributeValue(null, attribute) != null) {
            count(facts, key);
        }
    }

    private static void close(Open closed, Open parent, String type, Facts facts) {
        if (type.equals(PHYSICAL_TABLE) && closed.tag.equals("Description")) {
            if (closed.readList) {
                // A description the jar does not read: its text stays unread when it is not blank.
                if (!closed.description.toString().isBlank()) {
                    count(facts, PHYSICAL_TABLE + "|unread_text|Description#text");
                }
            } else if (parent != null && parent.cells != null) {
                parent.cells[0] = closed.description.toString();
            }
        }
        if (type.equals(PHYSICAL_TABLE) && parent == null && closed.id != null && closed.cells != null) {
            putCells(facts.added("physical_tables"), closed.id, closed.cells);
        }
        if (type.equals(DIMENSION) && parent == null) {
            // The parents of a level are the levels of the dimension that list it, in the files' order.
            Map<String, List<String>> parents = new HashMap<>();
            for (Open level : closed.levels) {
                for (String child : level.children) {
                    if (!child.isEmpty()) {
                        parents.computeIfAbsent(child, k -> new ArrayList<>()).add(level.id);
                    }
                }
            }
            for (Open level : closed.levels) {
                putCells(facts.added("logical_levels"), level.id, new String[] {String.join("\n", level.children),
                        String.join("\n", parents.getOrDefault(level.id, List.of()))});
            }
        }
        if (!type.equals(PHYSICAL_TABLE) && closed.tag.equals("RefColumns") && !closed.hasColumnBelow) {
            // A column list with no column link below it that a key reads: nothing in it was looked for.
            count(facts, type + "|unread_element|RefColumns");
        }
        if (!closed.readKey || parent == null || closed.id == null) {
            return;
        }
        String columns = String.join("\n", closed.keyColumns);
        if (closed.tag.equals("PhysicalKey")) {
            facts.physicalKeyCount++;
            putCells(facts.physicalKeys, closed.id, new String[] {closed.id, "", parent.id, closed.name, columns});
        } else if (closed.tag.equals("LogicalKey") && type.equals(LOGICAL_TABLE)) {
            facts.logicalKeyCount++;
            putCells(facts.logicalKeys, closed.id, new String[] {closed.id, "", parent.id, closed.name, closed.primary, columns});
        } else if (closed.tag.equals("LogicalKey")) {
            facts.levelKeyCount++;
            putCells(facts.levelKeys, closed.id, new String[] {closed.id, "", parent.id, closed.name, closed.primary,
                    closed.chronological, closed.drillDown, columns});
        }
    }

    private static void count(Facts facts, String key) {
        facts.remaining.merge(key, 1, Integer::sum);
    }

    private static void put(Map<String, String> map, String id, String value) {
        map.put(id, map.containsKey(id) ? null : value);
    }

    private static void putCells(Map<String, String[]> map, String id, String[] cells) {
        map.put(id, map.containsKey(id) ? null : cells);
    }

    /** The Row identifier of a logical table or a dimension: {@code <estate>::bmm::<business model>.<name>}; null when a name is missing. */
    private static String modelObject(String estate, XMLStreamReader xml, Map<String, SourceElement> source) {
        String name = attribute(xml, "name");
        SourceElement model = source.get(idOf(attribute(xml, "subjectAreaRef")));
        if (!usable(name) || model == null || !usable(model.name)) {
            return null;
        }
        return estate + "::bmm::" + model.name + "." + name;
    }

    /** The name of the business model the root object with this id belongs to, or null. */
    private static String modelName(Path estateFolder, String type, String rootId, Map<String, SourceElement> source)
            throws IOException {
        SourceElement model = source.get(modelsOf(estateFolder, type).getOrDefault(rootId, ""));
        return model == null || !usable(model.name) ? null : model.name;
    }

    private static final Map<String, Map<String, String>> MODEL_OF_TABLE = new HashMap<>();

    /**
     * The Row identifier of the logical table with this id. The table's business model is named by an
     * attribute the shared index does not keep, so the logical table files are read once for it.
     */
    private static String logicalTableIdentifier(String estate, SourceElement table, String tableSourceId, Path estateFolder,
            Map<String, SourceElement> source) throws IOException {
        if (table == null || !table.tag.equals(LOGICAL_TABLE) || !usable(table.name)) {
            return null;
        }
        Map<String, String> models = modelsOf(estateFolder, LOGICAL_TABLE);
        SourceElement model = source.get(models.getOrDefault(tableSourceId, ""));
        return model == null || !usable(model.name) ? null : estate + "::bmm::" + model.name + "." + table.name;
    }

    /** For the files of one object type: the root object's id to the id its {@code subjectAreaRef} names. */
    private static Map<String, String> modelsOf(Path estateFolder, String type) throws IOException {
        Path folder = estateFolder.resolve("rpd").resolve("oracle").resolve("bi").resolve("server").resolve("base")
                .resolve(type).toAbsolutePath();
        Map<String, String> models = MODEL_OF_TABLE.get(folder.toString());
        if (models == null) {
            models = new HashMap<>();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            if (Files.isDirectory(folder)) {
                List<Path> files;
                try (Stream<Path> list = Files.list(folder)) {
                    files = list.filter(p -> p.toString().endsWith(".xml") && Files.isRegularFile(p)).sorted()
                            .collect(Collectors.toList());
                }
                for (Path file : files) {
                    try (InputStream in = Files.newInputStream(file)) {
                        XMLStreamReader xml = factory.createXMLStreamReader(in);
                        while (xml.hasNext()) {
                            if (xml.next() == XMLStreamConstants.START_ELEMENT) {
                                models.put(attribute(xml, "mdsid"), idOf(attribute(xml, "subjectAreaRef")));
                                break;
                            }
                        }
                        xml.close();
                    } catch (XMLStreamException e) {
                        // No facts from a file that cannot be parsed.
                    }
                }
            }
            MODEL_OF_TABLE.put(folder.toString(), models);
        }
        return models;
    }

    /** True when a name can be part of a Row identifier: not blank, and without the identifier separator. */
    private static boolean usable(String name) {
        return name != null && !name.isBlank() && !name.contains("::");
    }

    private static String idOf(String reference) {
        int hash = reference == null ? -1 : reference.indexOf('#');
        return hash < 0 ? "" : reference.substring(hash + 1);
    }

    private static String attribute(XMLStreamReader xml, String name) {
        String value = xml.getAttributeValue(null, name);
        return value == null ? "" : value;
    }
}
