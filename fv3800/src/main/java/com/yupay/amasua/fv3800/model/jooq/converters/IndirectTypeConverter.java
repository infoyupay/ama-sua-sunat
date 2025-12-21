package com.yupay.amasua.fv3800.model.jooq.converters;

import com.yupay.amasua.fv3800.model.enums.IndirectType;

/**
 * JOOQ converter for mapping {@link IndirectType} enum values to and from
 * {@code VARCHAR} database fields.<br/>
 * <br/>
 * This converter provides the technical bridge between the relational
 * representation of indirect ownership or control relationship types
 * and the strongly typed {@link IndirectType} enum used in the FV-3800
 * domain model.<br/>
 * <br/>
 * It is intended to be referenced from JOOQ {@code ForcedType}
 * configurations to ensure consistent behavior during both code
 * generation and runtime execution.
 *
 * <p>
 * This converter is strictly infrastructural. It does not infer indirect
 * relationships, evaluate control paths, or apply any business logic.
 * Its sole responsibility is to translate between the database string
 * representation and the corresponding enum constant.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public final class IndirectTypeConverter extends EnumJooqConverter<IndirectType> {

    /**
     * Creates a new converter instance bound to the {@link IndirectType} enum.
     *
     * <p>
     * This constructor establishes a fixed association between the
     * {@code VARCHAR} database representation and the {@link IndirectType}
     * enum constants, enabling deterministic and lossless round-trip
     * conversions.
     * </p>
     */
    public IndirectTypeConverter() {
        super(IndirectType.class);
    }

}
