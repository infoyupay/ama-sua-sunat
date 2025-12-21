package com.yupay.amasua.fv3800.model.jooq.converters;

import com.yupay.amasua.fv3800.model.enums.ListIds;

/**
 * JOOQ converter for mapping {@link ListIds} enum values to and from
 * {@code VARCHAR} database fields.<br/>
 * <br/>
 * This converter provides the technical bridge between the relational
 * representation of administrative or tax catalog identifiers and the
 * strongly typed {@link ListIds} enum used within the FV-3800 module.<br/>
 * <br/>
 * It is intended to be referenced from JOOQ {@code ForcedType}
 * configurations to ensure consistent behavior during both code
 * generation and runtime execution.
 *
 * <p>
 * This converter is strictly infrastructural. It does not interpret the
 * meaning of a catalog, resolve list contents, or apply validation logic.
 * Its sole responsibility is to translate between the database string
 * representation and the corresponding enum constant.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public final class ListIdsConverter extends EnumJooqConverter<ListIds> {

    /**
     * Creates a new converter instance bound to the {@link ListIds} enum.
     *
     * <p>
     * This constructor establishes a fixed association between the
     * {@code VARCHAR} database representation and the {@link ListIds}
     * enum constants, enabling deterministic and lossless round-trip
     * conversions.
     * </p>
     */
    public ListIdsConverter() {
        super(ListIds.class);
    }

}
