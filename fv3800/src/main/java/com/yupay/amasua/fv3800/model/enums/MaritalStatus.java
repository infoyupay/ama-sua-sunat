package com.yupay.amasua.fv3800.model.enums;

/**
 * Enumerates the legally recognized marital statuses applicable to a
 * natural person in the context of tax, ownership, or control disclosures,
 * such as those required by SUNAT forms (e.g. Formulario Virtual 3800).
 *
 * <p>
 * This enum represents <strong>legal civil status only</strong>, not social,
 * affective, or informal relationships beyond those explicitly recognized
 * by law. Its role is strictly descriptive and does not imply ownership,
 * control, or representation by itself.
 * </p>
 * <strong>Design notes:</strong><br/>
 * <ul>
 *   <li>This enum does <em>not</em> encode patrimonial regimes
 *       (e.g. community property, separation of assets).</li>
 *   <li>No automatic legal or fiscal inferences must be derived from
 *       a marital status value.</li>
 *   <li>The catalog is intentionally closed and code-based to ensure
 *       consistency with tax authority representations.</li>
 * </ul>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public enum MaritalStatus {

    /**
     * Represents a natural person who is legally single.
     *
     * <p>
     * This status indicates the absence of a legally recognized marital
     * or equivalent civil bond at the time of declaration.
     * </p>
     */
    SINGLE("01", "Solterx"),

    /**
     * Represents a natural person who is legally married.
     *
     * <p>
     * This value does not specify the patrimonial regime applicable
     * to the marriage; such information, if required, must be modeled
     * separately.
     * </p>
     */
    MARRIED("02", "Casadx"),

    /**
     * Represents a natural person whose marriage has been legally dissolved.
     *
     * <p>
     * This status reflects a finalized legal divorce and does not
     * imply the existence or absence of ongoing obligations.
     * </p>
     */
    DIVORCED("03", "Divorciadx"),

    /**
     * Represents a natural person whose spouse has legally deceased.
     *
     * <p>
     * This value indicates widowhood as a civil status and carries
     * no implicit patrimonial or inheritance semantics.
     * </p>
     */
    WIDOW("04", "Viudx"),

    /**
     * Represents a natural person in a legally recognized de facto union.
     *
     * <p>
     * This value corresponds to a formalized "Unión de Hecho" as
     * recognized by applicable law, and should not be confused with
     * informal cohabitation arrangements.
     * </p>
     */
    CONCUBINE("05", "Unión de Hecho");

    /**
     * Tax authority or catalog code associated with this marital status.
     *
     * <p>
     * This code is intended for serialization, catalog mapping, and
     * interoperability with external systems (e.g. SUNAT forms).
     * It must not be interpreted as a business rule by itself.
     * </p>
     */
    private final String taxCode;

    /**
     * Human-readable description of the marital status.
     *
     * <p>
     * This field exists exclusively for display or explanatory purposes
     * and must not be used as a source of truth for legal or fiscal logic.
     * </p>
     */
    private final String description;

    /**
     * Constructs a {@code MaritalStatus} enum value with its associated
     * tax code and human-readable description.
     *
     * <p>
     * This constructor is intentionally package-private and is not
     * intended for use outside the enum definition.
     * </p>
     *
     * @param taxCode     the tax authority or catalog code
     * @param description a human-readable description of the status
     */
    MaritalStatus(String taxCode, String description) {
        this.taxCode = taxCode;
        this.description = description;
    }

    /**
     * Returns the tax authority code associated with this marital status.
     *
     * <p>
     * The returned value is suitable for persistence, serialization,
     * or integration with tax-related interfaces.
     * </p>
     *
     * @return the tax catalog code representing this marital status
     */
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * Returns the human-readable description of this marital status.
     *
     * <p>
     * This value is intended for UI rendering or documentation only
     * and must not be relied upon for business or validation logic.
     * </p>
     *
     * @return a descriptive label for the marital status
     */
    public String getDescription() {
        return description;
    }
}
