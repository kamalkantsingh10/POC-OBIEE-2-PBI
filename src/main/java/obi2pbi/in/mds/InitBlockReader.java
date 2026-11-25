package obi2pbi.in.mds;

import obi2pbi.core.ExtractionError;
import obi2pbi.core.Identifiers;
import obi2pbi.core.InitBlock;
import obi2pbi.in.xml.XmlNode;

/**
 * Reads {@code InitBlock} files: the query that fills variables, exactly as written.
 *
 * <p>The query text is never withheld and the block's name is not tested, as in the reference
 * implementation; only a variable's own value is (see {@link VariableReader}).
 *
 * <p>Not read, and therefore reported in {@code unparsed.csv}: the per-database variants of the query,
 * which variables the block fills (that link is on the variable), the blocks that must run first, and the
 * connection pool. Connection details are not read anywhere.
 */
public final class InitBlockReader implements ObjectReader {

    private static final String INIT_BLOCK = "InitBlock";

    @Override
    public String objectType() {
        return INIT_BLOCK;
    }

    @Override
    public void bind(XmlNode root, ReadContext context) {
        String name = root.attr("name");
        if (name.isEmpty()) {
            context.error(ExtractionError.MISSING_NAME, ReadContext.sourceIdOf(root), INIT_BLOCK, "no name");
            return;
        }
        String rowId = context.makeId(root, INIT_BLOCK, Identifiers.VARIABLE, "init", name);
        if (rowId != null) {
            context.index.bind(root, rowId);
        }
    }

    @Override
    public void read(XmlNode root, ReadContext context) {
        String rowId = context.index.identifierOf(root);
        if (rowId == null) {
            return;
        }
        XmlNode initString = root.firstChild("InitString");
        context.estate.initBlocks.add(new InitBlock(
                rowId, context.rpdId, root.attr("name"), root.attr("refreshPeriod"), root.attr("isSessionVar"),
                root.attr("isEnabled"), initString == null ? "" : initString.text()));
        context.parsed(INIT_BLOCK);
    }
}
