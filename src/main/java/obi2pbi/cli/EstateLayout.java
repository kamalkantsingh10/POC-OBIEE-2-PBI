package obi2pbi.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import obi2pbi.core.IdentifierException;
import obi2pbi.core.Identifiers;

/**
 * What the Operator pointed the jar at: an estate folder, its name, and which halves it holds.
 *
 * <p>Decided from folder names alone; no file is opened. The estate name is the folder's own name
 * (Architecture AD-4), and a repository and a catalog belong together only because the Operator put them
 * in one estate folder (AD-7). Links are followed, so {@code rpd} and {@code catalog} may be links to
 * folders elsewhere.
 */
public final class EstateLayout {

    public static final String RPD = "rpd";
    public static final String CATALOG = "catalog";

    public static final String EXPECTED =
            "expected layout:\n"
                    + "  <estate-folder>/rpd/oracle/bi/server/base/<ObjectType>/*.xml   the repository export (MDS XML)\n"
                    + "  <estate-folder>/catalog/...                                     the Web Catalog\n"
                    + "Either half may be absent. The estate name is the folder's own name: lower-case\n"
                    + "letters and digits, with single hyphens between groups, for example 'sales-prod'.\n";

    public final String name;
    public final Path folder;
    /** The repository export's root, or null when the estate has no {@code rpd/}. */
    public final Path rpd;
    /** The Web Catalog's root, or null when the estate has no {@code catalog/}. */
    public final Path catalog;

    private EstateLayout(String name, Path folder, Path rpd, Path catalog) {
        this.name = name;
        this.folder = folder;
        this.rpd = rpd;
        this.catalog = catalog;
    }

    /** The halves present, in the fixed order {@code rpd}, {@code catalog}. */
    public List<String> halves() {
        List<String> halves = new ArrayList<>();
        if (rpd != null) {
            halves.add(RPD);
        }
        if (catalog != null) {
            halves.add(CATALOG);
        }
        return halves;
    }

    public static EstateLayout resolve(Path folder) throws UsageException {
        if (!Files.isDirectory(folder)) {
            throw new UsageException(folder + " is not a folder.\n\n" + EXPECTED, false);
        }
        Path rpd = folder.resolve(RPD);
        Path catalog = folder.resolve(CATALOG);
        boolean hasRpd = Files.isDirectory(rpd);
        boolean hasCatalog = Files.isDirectory(catalog);
        if (!hasRpd && !hasCatalog) {
            String hint = "";
            if (Files.isDirectory(folder.resolve("oracle").resolve("bi").resolve("server").resolve("base"))) {
                hint = "It looks like a repository export itself. Put it (or a link to it) at "
                        + "<estate-folder>/rpd and give the estate folder.\n";
            }
            throw new UsageException(
                    folder + " is not an estate folder: it holds neither rpd/ nor catalog/.\n" + hint + "\n"
                            + EXPECTED, false);
        }
        Path named = folder.toAbsolutePath().normalize().getFileName();
        String name = named == null ? "" : named.toString();
        try {
            Identifiers.validateEstateName(name);
        } catch (IdentifierException e) {
            throw new UsageException(e.getMessage() + "\nRename the estate folder.\n\n" + EXPECTED, false);
        }
        return new EstateLayout(name, folder, hasRpd ? rpd : null, hasCatalog ? catalog : null);
    }
}
