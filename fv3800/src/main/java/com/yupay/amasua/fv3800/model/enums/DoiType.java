package com.yupay.amasua.fv3800.model.enums;

import java.util.function.Predicate;

/**
 * Enumerates the DOI (Documento Oficial de Identificación) types recognized by the
 * fv3800 domain, acting as an adapter over the canonical identifiers provided by
 * {@code com.infoyupay.validator.doi.DoiType}.
 * <br/>
 * <br/>
 * This enum bridges fv3800's historical DOI definitions and SUNAT tax–code mappings
 * with the formal, standardized DOI types exposed by the {@code validator-doi} library.
 * Each constant delegates validation, sanitization and tax–code retrieval to its
 * canonical counterpart, ensuring normative correctness and preserving backward
 * compatibility with fv3800's database model.
 * <br/><br/>
 *
 * <b>Responsibilities:</b>
 * <ul>
 *   <li>Expose fv3800's traditional DOI set (RUC, DNI, CEX, PAS, TIN, ID).</li>
 *   <li>Provide transparent delegation to the official validator-doi logic.</li>
 *   <li>Expose SUNAT’s FV-3800 tax-code identifiers.</li>
 *   <li>Represent whether each DOI is valid for residents or non-residents.</li>
 *   <li>Offer input normalization through {@link #sanitize(String)}.</li>
 * </ul>
 * <br/>
 *
 * <b>Non-resident support:</b><br/>
 * fv3800 distinguishes DOIs accepted for Peruvian residents and foreign/non-resident
 * taxpayers. This enum models that rule through
 * {@link #isValidForNonResidents()} and {@link #isValidForResidents()}.
 * <br/><br/>
 *
 * <b>Design notes:</b><br/>
 * This adapter exists because fv3800 predates validator-doi and maintains database
 * semantics that cannot be refactored without breaking backward compatibility.
 * <br/><br/>
 * <p>
 * This type is stateless and fully thread-safe.
 *
 * @author David Vidal
 * @version 1.0
 */
public enum DoiType implements Predicate<String> {

    /**
     * Represents the Peruvian {@code RUC} (Registro Único de Contribuyentes).
     * <br/>
     * <br/>
     * This DOI is:
     * <ul>
     *     <li><b>Not valid</b> for non-resident taxpayers.</li>
     *     <li>Delegated to {@code validator-doi}'s canonical {@code RUC} type for
     *         checksum validation, length rules and sanitization.</li>
     *     <li>Mapped to the SUNAT FV-3800 tax-code associated to RUC identifiers.</li>
     * </ul>
     */
    RUC {
        @Override
        public boolean isValidForNonResidents() {
            return false;
        }

        @Override
        public boolean test(String s) {
            return com.infoyupay.validator.doi.DoiType.RUC.validateNumber(s, false);
        }

        @Override
        public String sanitize(String s) {
            return com.infoyupay.validator.doi.DoiType.RUC.sanitize(s);
        }

        @Override
        public String getTaxCode() {
            return com.infoyupay.validator.doi.DoiType.RUC.getFv3800Id();
        }
    },

    /**
     * Represents the Peruvian {@code DNI} (Documento Nacional de Identidad).
     * <br/><br/>
     * Characteristics:
     * <ul>
     *     <li>Only valid for resident taxpayers.</li>
     *     <li>Validation logic delegated to the canonical {@code DNI} type in validator-doi.</li>
     *     <li>Provides the associated SUNAT FV-3800 tax-code.</li>
     * </ul>
     */
    DNI {
        @Override
        public boolean isValidForNonResidents() {
            return false;
        }

        @Override
        public boolean test(String s) {
            return com.infoyupay.validator.doi.DoiType.DNI.validateNumber(s, false);
        }

        @Override
        public String sanitize(String s) {
            return com.infoyupay.validator.doi.DoiType.DNI.sanitize(s);
        }

        @Override
        public String getTaxCode() {
            return com.infoyupay.validator.doi.DoiType.DNI.getFv3800Id();
        }
    },

    /**
     * Represents the Peruvian {@code CEX} (Carné de Extranjería).
     * <br/><br/>
     * Notes:
     * <ul>
     *   <li>Although issued to foreign citizens, fv3800 considers CEX identifiers
     *       <b>not valid</b> for non-resident tax purposes.</li>
     *   <li>Delegates all logic to the canonical {@code CE} type.</li>
     *   <li>Returns the FV-3800 tax-code corresponding to CEX.</li>
     * </ul>
     */
    CEX {
        @Override
        public boolean isValidForNonResidents() {
            return false;
        }

        @Override
        public boolean test(String s) {
            return com.infoyupay.validator.doi.DoiType.CE.validateNumber(s, false);
        }

        @Override
        public String sanitize(String s) {
            return com.infoyupay.validator.doi.DoiType.CE.sanitize(s);
        }

        @Override
        public String getTaxCode() {
            return com.infoyupay.validator.doi.DoiType.CE.getFv3800Id();
        }
    },

    /**
     * Represents a standard {@code PAS} (Passport) used as an identification
     * document for foreign citizens.
     * <br/><br/>
     * Notes:
     * <ul>
     *     <li>According to fv3800 semantics, passports are not considered valid
     *         identifiers for non-resident tax obligations.</li>
     *     <li>Delegates validation/sanitization to {@code PASSPORT}.</li>
     *     <li>Includes the FV-3800 tax-code associated with passport DOIs.</li>
     * </ul>
     */
    PAS {
        @Override
        public boolean isValidForNonResidents() {
            return false;
        }

        @Override
        public boolean test(String s) {
            return com.infoyupay.validator.doi.DoiType.PASSPORT.validateNumber(s, false);
        }

        @Override
        public String sanitize(String s) {
            return com.infoyupay.validator.doi.DoiType.PASSPORT.sanitize(s);
        }

        @Override
        public String getTaxCode() {
            return com.infoyupay.validator.doi.DoiType.PASSPORT.getFv3800Id();
        }
    },

    /**
     * Represents a generic foreign {@code TIN} (Tax Identification Number).
     * <br/><br/>
     * Characteristics:
     * <ul>
     *     <li><b>Valid for non-resident taxpayers.</b></li>
     *     <li>Delegates logic to the canonical {@code TIN} type.</li>
     *     <li>Maps to the SUNAT FV-3800 identifier for foreign TIN documents.</li>
     * </ul>
     */
    TIN {
        @Override
        public boolean isValidForNonResidents() {
            return true;
        }

        @Override
        public boolean test(String s) {
            return com.infoyupay.validator.doi.DoiType.TIN.validateNumber(s, false);
        }

        @Override
        public String sanitize(String s) {
            return com.infoyupay.validator.doi.DoiType.TIN.sanitize(s);
        }

        @Override
        public String getTaxCode() {
            return com.infoyupay.validator.doi.DoiType.TIN.getFv3800Id();
        }
    },

    /**
     * Represents a generic foreign {@code ID} (international identification document).
     * <br/><br/>
     * Characteristics:
     * <ul>
     *     <li><b>Valid for non-resident taxpayers.</b></li>
     *     <li>Delegates to the canonical {@code ID} identifier type.</li>
     *     <li>Returns the associated FV-3800 tax-code.</li>
     * </ul>
     */
    ID {
        @Override
        public boolean isValidForNonResidents() {
            return true;
        }

        @Override
        public boolean test(String s) {
            return com.infoyupay.validator.doi.DoiType.ID.validateNumber(s, false);
        }

        @Override
        public String sanitize(String s) {
            return com.infoyupay.validator.doi.DoiType.ID.sanitize(s);
        }

        @Override
        public String getTaxCode() {
            return com.infoyupay.validator.doi.DoiType.ID.getFv3800Id();
        }
    };

    /**
     * Returns the SUNAT FV-3800 tax-code associated with this DOI type.
     * <br/><br/>
     * The returned value is delegated to the canonical DOI type from
     * {@code validator-doi}, ensuring compliance with SUNAT's regulatory
     * catalog of identification types.
     *
     * @return the FV-3800 tax-code string for this DOI type.
     */
    public abstract String getTaxCode();

    /**
     * Indicates whether this DOI type is valid for taxpayers who are
     * <b>not residents of Peru</b>, according to fv3800’s model.
     * <br/><br/>
     *
     * @return {@code true} if acceptable for non-resident taxpayers;
     * {@code false} otherwise.
     */
    public abstract boolean isValidForNonResidents();

    /**
     * Indicates whether this DOI type is valid for <b>Peruvian residents</b>.
     * <br/><br/>
     * The default implementation is the logical negation of
     * {@link #isValidForNonResidents()}.
     *
     * @return {@code true} if acceptable for resident taxpayers.
     */
    public boolean isValidForResidents() {
        return !isValidForNonResidents();
    }

    /**
     * Produces a normalized, cleaned version of the input identifier by
     * delegating to the canonical validator-doi implementation.
     * <br/><br/>
     * Typical sanitization includes trimming, removing formatting characters,
     * and applying DOI-type specific normalization rules.
     *
     * @param s the raw input string (may be {@code null}).
     * @return a normalized representation of the DOI, or {@code null} if the
     * canonical sanitizer returns null.
     */
    public abstract String sanitize(String s);
}
