package com.yupay.amasua.fv3800.model.jooq.converters;

import com.yupay.amasua.fv3800.model.enums.EntitySubtype;

/**
 * JOOQ converter for mapping {@link EntitySubtype} enum values to and from
 * {@code VARCHAR} database fields.<br/>
 * <br/>
 * This converter provides the technical bridge between the relational
 * representation of non-standard or irregular legal entity subtypes
 * and the strongly typed {@link EntitySubtype} enum used in the FV-3800
 * domain model.<br/>
 * <br/>
 * It is intended to be referenced from JOOQ {@code ForcedType}
 * configurations to ensure consistent behavior during both code
 * generation and runtime execution.
 *
 * <p>
 * This converter is strictly infrastructural. It does not validate the
 * legal status of an entity, infer classification rules, or apply any
 * business semantics. Its sole responsibility is to translate between
 * the database string representation and the corresponding enum constant.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public final class EntitySubtypeConverter extends EnumJooqConverter<EntitySubtype> {

    /**
     * Creates a new converter instance bound to the {@link EntitySubtype} enum.
     *
     * <p>
     * This constructor establishes a fixed association between the
     * {@code VARCHAR} database representation and the {@link EntitySubtype}
     * enum constants, enabling deterministic and lossless round-trip
     * conversions.
     * </p>
     */
    public EntitySubtypeConverter() {
        super(EntitySubtype.class);
    }

}
