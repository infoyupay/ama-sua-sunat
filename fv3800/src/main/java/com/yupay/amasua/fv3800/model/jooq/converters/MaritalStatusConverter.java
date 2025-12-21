package com.yupay.amasua.fv3800.model.jooq.converters;

import com.yupay.amasua.fv3800.model.enums.MaritalStatus;

/**
 * JOOQ converter for mapping {@link MaritalStatus} enum values to and from
 * {@code VARCHAR} database fields.<br/>
 * <br/>
 * This converter provides the technical bridge between the relational
 * representation of a person's legally recognized marital status and the
 * strongly typed {@link MaritalStatus} enum used in the FV-3800 domain model.<br/>
 * <br/>
 * It is intended to be referenced from JOOQ {@code ForcedType}
 * configurations to ensure consistent behavior during both code
 * generation and runtime execution.
 *
 * <p>
 * This converter is strictly infrastructural. It does not infer patrimonial
 * regimes, establish family relationships, or apply any legal or fiscal
 * semantics. Its sole responsibility is to translate between the database
 * string representation and the corresponding enum constant.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public final class MaritalStatusConverter extends EnumJooqConverter<MaritalStatus> {

    /**
     * Creates a new converter instance bound to the {@link MaritalStatus} enum.
     *
     * <p>
     * This constructor establishes a fixed association between the
     * {@code VARCHAR} database representation and the {@link MaritalStatus}
     * enum constants, enabling deterministic and lossless round-trip
     * conversions.
     * </p>
     */
    public MaritalStatusConverter() {
        super(MaritalStatus.class);
    }

}
