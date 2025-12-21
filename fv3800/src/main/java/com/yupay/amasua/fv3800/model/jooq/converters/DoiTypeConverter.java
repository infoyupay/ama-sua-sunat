package com.yupay.amasua.fv3800.model.jooq.converters;

import com.yupay.amasua.fv3800.model.enums.DoiType;

/**
 * JOOQ converter for mapping {@link DoiType} enum values to and from
 * {@code VARCHAR} database fields.<br/>
 * <br/>
 * This converter acts as a concrete adapter between the relational
 * representation of document-of-identity types and the strongly typed
 * {@link DoiType} enum used in the FV-3800 domain model.<br/>
 * <br/>
 * It is intended to be referenced from JOOQ {@code ForcedType}
 * configurations to ensure consistent behavior during both code
 * generation and runtime execution.
 *
 * <p>
 * This converter does not perform validation, normalization, or semantic
 * interpretation of identity documents. It strictly maps database values
 * to enum constants using their {@link Enum#name()} representation.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public final class DoiTypeConverter extends EnumJooqConverter<DoiType> {

    /**
     * Creates a new converter instance bound to the {@link DoiType} enum.
     *
     * <p>
     * This constructor establishes the static association between the
     * {@code VARCHAR} database representation and the {@link DoiType}
     * enum constants, enabling deterministic and lossless round-trip
     * conversions.
     * </p>
     */
    public DoiTypeConverter() {
        super(DoiType.class);
    }

}
