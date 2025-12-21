package com.yupay.amasua.fv3800.model.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the subtype of a legal entity according to the fv3800 model.
 * <br/>
 * This enumeration provides a normalized and citizen-centric classification of
 * fiduciary and investment structures commonly encountered in Peruvian and
 * international tax contexts. While the official FV-3800 form issued by SUNAT
 * mixes terminology from civil-law and common-law systems, this enum provides a
 * clear conceptual separation intended to:
 * <ul>
 *     <li>Allow citizens and compliance officers to correctly identify the nature
 *         of a legal structure.</li>
 *     <li>Map each subtype to a stable <i>tax identifier code</i> ("FV-3800 code")
 *         used internally by fv3800 for persistence and reporting.</li>
 *     <li>Offer accurate distinctions between Peruvian fiduciary structures
 *         (fideicomisos) and foreign trust arrangements.</li>
 *     <li>Provide an extensible model capable of representing entities that do not
 *         fit neatly into SUNAT’s predefined categories.</li>
 * </ul>
 * <p>
 * This enum is <b>legal-model-accurate</b> and at the same time
 * <b>user-centric</b>: it presents terminology understandable to the citizen,
 * while the application layer translates these subtypes to the more cryptic
 * and occasionally inconsistent categories of the SUNAT FV-3800 form.
 * <br>
 * <p>
 * Each constant carries two attributes:
 * <ul>
 *     <li>{@code description} – A human-readable label displayed to citizens.</li>
 *     <li>{@code taxId} – The internal FV-3800 code associated with the subtype.</li>
 * </ul>
 * <p>
 * This enum is immutable, thread-safe, and suitable for use in persistence and DTO layers.
 *
 * @author David Vidal
 * @version 1.0
 */
public enum EntitySubtype {

    /**
     * Represents a standardized investment fund regulated under Peruvian securities
     * law (SMV). These structures operate with separate patrimony managed by a
     * fund administrator and do not constitute fiduciary arrangements in the
     * sense of fideicomisos or foreign trusts.
     */
    INVESTMENT_FUND("Fondo de Inversión", "01"),

    /**
     * Represents a Peruvian fiduciary arrangement regulated by the SBS, in which
     * a fiduciary entity manages a separate estate under the rules of the civil-law
     * fideicomiso. Distinct from a common-law trust, although both share functional
     * characteristics relevant for beneficiary-final identification.
     */
    FIDEICOMISO("Fideicomiso (Perú)", "02"),

    /**
     * Represents a foreign trust—typically created under common-law jurisdictions—
     * including revocable, irrevocable, discretionary and grantor trusts. This
     * subtype is conceptually separate from Peruvian fideicomisos and appears as a
     * distinct category in cross-border tax frameworks (FATCA, CRS, OECD/GAFI).
     */
    TRUST("Fideicomiso (Extranjero)", "03"),

    /**
     * Represents any legal structure that does not fall within the predefined
     * subtypes of fv3800. Examples include SPVs, PCCs, protected cell structures,
     * foreign partnerships, foundations, Anstalt, and similar entities whose
     * patrimonial or governance characteristics differ from funds, trusts, or
     * fideicomisos.
     */
    OTHER("Otras", "04");

    @NotNull
    private final String description;

    @NotNull
    private final String taxId;

    /**
     * Constructs an {@link EntitySubtype} with its display description and its
     * corresponding FV-3800 tax identifier code.
     *
     * @param description a human-friendly label used for UI display and textual
     *                    representations; never {@code null}.
     * @param taxId       the internal FV-3800 code associated with this subtype,
     *                    used for persistence and interoperability; never {@code null}.
     */
    @Contract(pure = true)
    EntitySubtype(@NotNull String description, @NotNull String taxId) {
        this.description = description;
        this.taxId = taxId;
    }

    /**
     * Resolves an {@link EntitySubtype} from its associated {@code taxId}. This helper
     * method enables safe deserialization and database lookups based on FV-3800 codes.
     *
     * @param code the FV-3800 tax identifier; may not be {@code null}.
     * @return the matching {@link EntitySubtype}.
     * @throws IllegalArgumentException if the code does not correspond to any subtype.
     */
    @NotNull
    public static EntitySubtype fromTaxId(@NotNull String code) {
        for (var e : values()) {
            if (e.taxId.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown EntitySubtype taxId: " + code);
    }

    /**
     * Returns the human-readable description of this subtype. This value is intended
     * for UI components, reports, and citizen-facing outputs.
     *
     * @return a non-null descriptive label for this subtype.
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Returns the FV-3800 tax identifier code associated with this subtype. This is
     * used internally by fv3800 to ensure compatibility with SUNAT’s reporting
     * schemes and to maintain stable persistence values in the database.
     *
     * @return a non-null short code representing this subtype in fv3800.
     */
    @NotNull
    public String getTaxId() {
        return taxId;
    }
}
