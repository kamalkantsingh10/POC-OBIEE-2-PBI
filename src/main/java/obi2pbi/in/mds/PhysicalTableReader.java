package obi2pbi.in.mds;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import obi2pbi.core.Expression;
import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.PhysicalColumn;
import obi2pbi.core.PhysicalJoin;
import obi2pbi.core.PhysicalKey;
import obi2pbi.core.PhysicalTable;
import obi2pbi.core.PhysicalTableDefinition;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code PhysicalTable} files: the table, its definitions, its columns, its keys and its foreign keys.
 *
 * <p>A foreign key is followed to the key it points at (deliberate difference 26): the table that key is on
 * and its columns. Every key of the table is a row of its own, whether or not a foreign key points at it
 * (deliberate difference 28), and a column carries its scale and its place in the table (difference 31).
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: row counts, update times and cache
 * settings.
 */
public final class PhysicalTableReader implements ObjectReader {

    private static final String TABLE = "PhysicalTable";
    private static final String COLUMN = "PhysicalColumn";
    private static final String FOREIGN_KEY = "PhysicalForeignKey";
    private static final String KEY = "PhysicalKey";

    @Override
    public String objectType() {
        return TABLE;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        List<String> container = containerPath(root, context.index);
        if (name.isEmpty() || container.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), TABLE,
                    "table name or container could not be resolved");
            return;
        }
        String tableId = context.makeId(root, TABLE, Identifiers.PHYSICAL, parts(container, name));
        if (tableId == null) {
            return;
        }
        context.index.bind(root, tableId);

        for (XmlNode column : root.children(COLUMN)) {
            String columnName = column.attr("name");
            if (columnName.isEmpty()) {
                context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(column), COLUMN,
                        "column has no name");
                continue;
            }
            String columnId =
                    context.makeId(column, COLUMN, Identifiers.PHYSICAL, parts(container, name, columnName));
            if (columnId != null) {
                context.index.bind(column, columnId);
            }
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String tableId = context.index.identifierOf(root);
        if (tableId == null) {
            return;
        }
        String rpdId = context.rpdId;
        String name = root.attr("name");
        List<String> container = containerPath(root, context.index);

        context.estate.physicalTables.add(new PhysicalTable(
                tableId, rpdId, String.join(".", container), name, root.attr("type"), root.attr("maxConn"),
                aliasSource(root, tableId, context), description(root),
                // Difference 36: the four columns that make a table the relationship table of a parent-child
                // hierarchy. A link that does not resolve leaves the cell empty, like a foreign key's column.
                context.index.resolve(root.attr("memberKeyRef")), context.index.resolve(root.attr("parentKeyRef")),
                context.index.resolve(root.attr("distanceColumnRef")), context.index.resolve(root.attr("leafColumnRef"))));
        context.parsed(TABLE);

        // One entry per database type. For a table of type "select" the value is its SQL; otherwise it is
        // the name of the object in the database. Kept exactly as written and never parsed.
        int position = 0;
        for (XmlNode item : root.children("DBMapItem")) {
            position++;
            String database = item.attr("name");
            List<String> texts = new ArrayList<>();
            for (XmlNode value : item.children("Value")) {
                texts.add(value.text());
            }
            if (texts.isEmpty()) {
                // An entry with no value is still an entry: the table is mapped for that database type.
                texts.add("");
            }
            String definitionId = context.makeId(root, TABLE, Identifiers.PHYSICAL,
                    parts(container, name, "def", database.isEmpty() ? "unnamed-" + position : database));
            if (definitionId == null) {
                continue;
            }
            for (String text : texts) {
                context.estate.physicalTableDefinitions.add(
                        new PhysicalTableDefinition(definitionId, rpdId, tableId, database, text));
            }
        }

        // Difference 31: a column's place in the table, from 1. A column that was not named in the first
        // phase still takes its place, so the numbers can have gaps.
        int place = 0;
        for (XmlNode column : root.children(COLUMN)) {
            place++;
            String columnId = context.index.identifierOf(column);
            if (columnId == null) {
                continue;
            }
            context.estate.physicalColumns.add(new PhysicalColumn(
                    columnId, rpdId, tableId, column.attr("name"), column.attr("dataType"),
                    column.attr("specialType"), column.attr("precision"), column.attr("nullable"),
                    column.attr("extName"), column.attr("scale"), place,
                    // Difference 37: for a column of an alias, the column of the aliased table it stands for.
                    context.index.resolve(column.attr("sourceColumnRef"))));
            context.parsed(COLUMN);
        }

        // Difference 28: every key of the table, with its columns in source order. The source has no flag
        // that says which key is the primary key, so none is written.
        int keyPlace = 0;
        for (XmlNode key : root.children(KEY)) {
            keyPlace++;
            String keyName = key.attr("name");
            String keyId = context.makeId(key, KEY, Identifiers.PHYSICAL,
                    parts(container, name, "key", keyName.isEmpty() ? "unnamed-" + keyPlace : keyName));
            if (keyId == null) {
                continue;
            }
            List<String> columns = new ArrayList<>();
            XmlNode refColumns = key.firstChild("RefColumns");
            if (refColumns != null) {
                for (XmlNode ref : refColumns.children("RefColumn")) {
                    columns.add(context.index.resolve(ref.attr("columnRef")));
                }
            }
            context.estate.physicalKeys.add(new PhysicalKey(keyId, rpdId, tableId, keyName, columns));
            context.parsed(KEY);
        }

        for (XmlNode foreignKey : root.children(FOREIGN_KEY)) {
            String keyName = foreignKey.attr("name");
            List<String> columns = new ArrayList<>();
            XmlNode refColumns = foreignKey.firstChild("RefColumns");
            if (refColumns != null) {
                for (XmlNode ref : refColumns.children("RefColumn")) {
                    columns.add(context.index.resolve(ref.attr("columnRef")));
                }
            }
            String keyId = context.makeId(root, FOREIGN_KEY, Identifiers.PHYSICAL,
                    parts(container, name, keyName.isEmpty() ? "fk" : keyName));
            if (keyId == null) {
                continue;
            }
            // Difference 26: the key this foreign key points at says which table it joins to, and on
            // which columns. The reference program did not read the link.
            String referencedTable = "";
            List<String> referencedColumns = new ArrayList<>();
            String counterpart = foreignKey.attr("counterPartKeyRef");
            if (!counterpart.isEmpty()) {
                String keyMdsid = ReferenceIndex.mdsidOf(counterpart);
                ReferenceIndex.Entry entry = context.index.entry(keyMdsid);
                XmlNode key = context.index.node(keyMdsid);
                if (entry != null && key != null && entry.tag.equals(KEY)) {
                    referencedTable = context.index.identifier(entry.ownerMdsid);
                    XmlNode keyColumns = key.seek().firstChild("RefColumns");
                    if (keyColumns != null) {
                        for (XmlNode ref : keyColumns.children("RefColumn")) {
                            referencedColumns.add(context.index.resolve(ref.attr("columnRef")));
                        }
                    }
                }
                if (referencedTable.isEmpty()) {
                    context.error(ExtractionError.UNRESOLVED_REFERENCE, keyId, FOREIGN_KEY,
                            "the key this foreign key points at could not be resolved to a physical table, "
                                    + "so the row does not say which table it joins to");
                }
            }
            context.estate.physicalJoins.add(new PhysicalJoin(
                    keyId, rpdId, PhysicalJoin.FOREIGN_KEY, keyName, tableId, referencedTable, "",
                    Expression.EMPTY, columns, referencedColumns));
            context.parsed(FOREIGN_KEY);
        }
    }

    /** Difference 37: the table's description, exactly as written; empty when it has none. */
    private static String description(XmlNode root) {
        XmlNode description = root.firstChild("Description");
        return description == null ? "" : description.text();
    }

    /**
     * The Row identifier of the physical join a reference points at: a foreign key of a physical table, or a
     * complex join. Empty when it points at neither, or the identifier cannot be built. Neither kind of join
     * is bound in the index (nothing else refers to one), so the identifier is built here, from the same
     * names their own readers use.
     */
    static String physicalJoinId(String reference, ReadContext context) {
        ReferenceIndex.Entry entry = context.index.entry(ReferenceIndex.mdsidOf(reference));
        if (entry == null) {
            return "";
        }
        if (entry.tag.equals(FOREIGN_KEY)) {
            String tableId = context.index.identifier(entry.ownerMdsid);
            return tableId.isEmpty() ? "" : tableId + "." + (entry.name.isEmpty() ? "fk" : entry.name);
        }
        if (entry.tag.equals("ComplexJoin") && !entry.name.isEmpty()) {
            try {
                return Identifiers.makeId(context.rpdId, Identifiers.PHYSICAL, "joins", entry.name);
            } catch (obi2pbi.core.IdentifierException e) {
                return "";
            }
        }
        return "";
    }

    /**
     * For an alias, the table it stands for. A link that is present and does not resolve is an error and
     * the value stays empty: an alias shown as an ordinary table would send a reader looking for a database
     * object that does not exist.
     */
    private static String aliasSource(XmlNode root, String tableId, ReadContext context) {
        String reference = root.attr("sourceTableRef");
        if (reference.isEmpty()) {
            return "";
        }
        String sourceId = context.index.resolve(reference);
        if (sourceId.isEmpty()) {
            context.error(ExtractionError.UNRESOLVED_REFERENCE, tableId, TABLE,
                    "this table is an alias, but the table it is an alias of could not be resolved to a "
                            + "Knowledge Base identifier");
        }
        return sourceId;
    }

    /**
     * The names of the containers above a table, outermost first, by following {@code containerRef} up the
     * chain. A single container name does not identify a table: the sample data has three schemas named
     * {@code SH} under three databases. A hand-edited repository can hold a cycle, so each container is
     * visited once.
     */
    static List<String> containerPath(XmlNode element, ReferenceIndex index) {
        String mdsid = ReferenceIndex.mdsidOf(element.attr("containerRef"));
        List<String> names = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        while (!mdsid.isEmpty() && seen.add(mdsid)) {
            ReferenceIndex.Entry entry = index.entry(mdsid);
            if (entry == null || entry.name.isEmpty()) {
                break;
            }
            names.add(0, entry.name);
            mdsid = entry.containerMdsid;
        }
        return names;
    }

    private static String[] parts(List<String> container, String... rest) {
        List<String> all = new ArrayList<>(container);
        for (String part : rest) {
            all.add(part);
        }
        return all.toArray(new String[0]);
    }
}
