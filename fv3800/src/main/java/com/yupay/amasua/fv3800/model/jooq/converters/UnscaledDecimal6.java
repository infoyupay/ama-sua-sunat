package com.yupay.amasua.fv3800.model.jooq.converters;

import org.jooq.impl.AbstractConverter;

import java.math.BigDecimal;

/**
 * JOOQ converter for mapping {@link BigDecimal} values to and from a
 * {@code LONG} database representation using a fixed scale of 6 decimal places.<br/>
 * <br/>
 * This converter persists decimal values as unscaled integers, assuming
 * a scale of {@code 6}, in accordance with FV-3800 specifications and
 * conservative fiscal precision requirements. The database value represents
 * the {@link BigDecimal#unscaledValue()} of the number, while the scale is
 * implicitly fixed and enforced by this converter.<br/>
 * <br/>
 * <b>Persistence strategy:</b><br/>
 * <ul>
 *   <li>{@link BigDecimal} values are stored as {@code LONG} unscaled integers.</li>
 *   <li>A fixed scale of {@code 6} decimal places is assumed at all times.</li>
 *   <li>No floating-point types are used, avoiding rounding and precision loss.</li>
 *   <li>The conversion is deterministic and fully reversible within the defined scale.</li>
 * </ul>
 * <br/>
 * This strategy ensures predictable behavior, portability across database engines,
 * and compatibility with SQLite, which lacks native fixed-precision decimal types.
 *
 * <p>
 * This converter is strictly infrastructural. It does not perform rounding,
 * scale normalization, or validation beyond enforcing the fixed scale contract.
 * Any semantic interpretation or additional constraints must be handled
 * explicitly in higher layers.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public class UnscaledDecimal6 extends AbstractConverter<Long, BigDecimal> {

    /**
     * Creates a new converter for mapping fixed-scale decimal values
     * to and from their unscaled {@code LONG} representation.
     */
    public UnscaledDecimal6() {
        super(Long.class, BigDecimal.class);
    }

    /**
     * Converts a database {@code LONG} value into a {@link BigDecimal}
     * assuming a fixed scale of {@code 6}.
     *
     * <p>
     * The input value is interpreted as an unscaled integer whose scale
     * is implicitly {@code 6}, producing a {@link BigDecimal} equivalent
     * to {@code databaseObject × 10⁻⁶}.
     * </p>
     *
     * @param databaseObject the unscaled decimal value from the database,
     *                       may be {@code null}
     * @return the corresponding {@link BigDecimal}, or {@code null} if the input is {@code null}
     */
    @Override
    public BigDecimal from(Long databaseObject) {
        return databaseObject == null ? null : BigDecimal.valueOf(databaseObject, 6);
    }

    /**
     * Converts a {@link BigDecimal} into its unscaled {@code LONG} representation
     * assuming a fixed scale of {@code 6}.
     *
     * <p>
     * The provided value must strictly conform to the expected scale.
     * No rounding or rescaling is performed. If the scale differs from
     * {@code 6}, the conversion fails immediately.
     * </p>
     *
     * <p>
     * The conversion uses {@link BigDecimal#unscaledValue()} and requires the
     * resulting value to fit exactly into a {@code long}.
     * </p>
     *
     * @param userObject the {@link BigDecimal} to convert, may be {@code null}
     * @return the unscaled {@code LONG} representation, or {@code null} if the input is {@code null}
     * @throws IllegalArgumentException if the {@link BigDecimal} scale is not exactly {@code 6}
     * @throws ArithmeticException      if the unscaled value cannot be represented exactly as a {@code long}
     */
    @Override
    public Long to(BigDecimal userObject) {
        if (userObject == null) return null;

        var scale = userObject.scale();
        if (scale != 6) {
            throw new IllegalArgumentException(
                    "Illegal scale for unscaled decimal, expected: 6, found: " + scale
            );
        }

        return userObject.unscaledValue().longValueExact();
    }

}
