package com.yupay.amasua.fv3800.model.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the type of <em>indirect ownership or control</em> as defined by the
 * official FV-3800 specification issued by SUNAT. These categories describe
 * the mechanisms through which a natural person may exercise control over a
 * legal entity without appearing as a direct shareholder.
 * <br>
 * This enum is a faithful and literal implementation of the FV-3800 taxonomy,
 * ensuring complete interoperability with SUNAT’s reporting formats and legal
 * interpretation frameworks. Each constant includes both:
 *
 * <ul>
 *     <li>{@code description} – A human-friendly label used in UI and reports.</li>
 *     <li>{@code taxId} – The exact FV-3800 code associated with the category.</li>
 * </ul>
 * This classification is essential for determining the Beneficiario Final
 * (Ultimate Beneficial Owner) when the individual does not hold shares directly,
 * but instead exercises control through intermediaries or legal arrangements.
 * <br/>
 * This type is immutable, thread-safe and suitable for database persistence and DTO mapping.
 *
 * @author David Vidal
 * @version  1.0
 */
public enum IndirectType {

    /**
     * Represents indirect control exercised through <b>family or kinship relationships</b>.
     * <p>
     * In FV-3800 terminology, this subtype applies when the natural person exerts
     * influence or control through relatives who are direct shareholders or who
     * participate in the ownership chain.
     * </p>
     */
    FAMILY("Por Parientes", "01"),

    /**
     * Represents indirect control exercised through a <b>chain of legal entities</b>.
     * <p>
     * This subtype applies when ownership or control is structured through multiple
     * legal persons (companies, societies, foreign entities), forming a layered
     * or cascading chain that ultimately links back to the natural person.
     * </p>
     */
    ENTITY_CHAIN("Por otras Personas Jurídicas", "02"),

    /**
     * Represents indirect control obtained via a <b>mandate, legal act, or contractual
     * arrangement</b>.
     * <p>
     * This subtype applies when the person has authority, rights, or powers granted
     * through legal documents (mandates, powers of attorney, agreements) that allow
     * them to exercise effective control despite not holding shares directly.
     * </p>
     */
    MANDATE("Por un mandato o acto jurídico", "03");

    @NotNull
    private final String description;

    @NotNull
    private final String taxId;

    /**
     * Constructs an {@link IndirectType} with its human-readable description and
     * its corresponding FV-3800 tax identifier code.
     *
     * @param description a citizen-facing label describing the mode of indirect control;
     *                    never {@code null}.
     * @param taxId       the exact FV-3800 code associated with this subtype; never {@code null}.
     */
    IndirectType(@NotNull String description, @NotNull String taxId) {
        this.description = description;
        this.taxId = taxId;
    }

    /**
     * Returns the descriptive label associated with this subtype. This is intended
     * for user interfaces, reports, and explanatory documents.
     *
     * @return a non-null, human-friendly description.
     */
    @Contract(pure = true)
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Returns the FV-3800 tax identifier code associated with this subtype. This value
     * is used internally by fv3800 to ensure compatibility with SUNAT’s data model
     * and for stable persistence in databases.
     *
     * @return a non-null short code representing this subtype.
     */
    @Contract(pure = true)
    @NotNull
    public String getTaxId() {
        return taxId;
    }
}
