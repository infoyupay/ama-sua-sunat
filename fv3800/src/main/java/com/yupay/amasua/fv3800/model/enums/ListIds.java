package com.yupay.amasua.fv3800.model.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Identifies the internal catalog (code list) used within the fv3800 data model.
 * <br/>
 * This enumeration does <em>not</em> correspond to any SUNAT or FV-3800
 * specification. Instead, it exists to provide a clear and stable way of
 * referencing the different logical catalogs stored in the {@code code_list}
 * table of the application.
 * <br/>
 * Each constant represents one catalog and carries a human-readable description
 * used for display purposes (UI, diagnostics, logs, etc.). The enum values are
 * stable identifiers that allow type-safe references throughout the codebase,
 * avoiding string-based lookups and magic constants.
 *
 * @author David Vidal
 * @version 1.0
 */
public enum ListIds {

    /**
     * Catalog of countries recognized by the system.
     * <br/>
     * This list is typically used for nationality, residency, and cross-border
     * declarations.
     */
    COUNTRY("Países"),

    /**
     * Catalog representing the roles or relationships a natural person or legal
     * entity may have with a company (e.g., director, gerente, accionista).
     */
    CORPORATE_ROLE("Relaciones con la Compañía"),

    /**
     * Catalog describing the types of indirect relationships used in the
     * beneficiary-final model (e.g., por parientes, por otras personas jurídicas,
     * por mandato). Although inspired by FV-3800 categories, this list is
     * maintained internally by the application.
     */
    INDIRECT_RELATIONSHIP("Relaciones Indirectas"),

    /**
     * Catalog of value types or classifications used by the system for modeling
     * ownership, equity, voting power, or similar attributes.
     */
    VALUE_TYPE("Tipos de Valores");

    @NotNull
    private final String description;

    /**
     * Constructs a {@link ListIds} constant with its associated human-readable
     * description. This value is used primarily for display and debugging.
     *
     * @param description a short label describing the catalog; never {@code null}.
     */
    ListIds(@NotNull String description) {
        this.description = description;
    }

    /**
     * Returns the human-friendly label associated with this catalog.
     * <br/>
     * This value is suitable for UI representation, logs and non-technical
     * diagnostics.
     *
     * @return a non-null descriptive label for the catalog.
     */
    @Override
    public String toString() {
        return description;
    }
}
