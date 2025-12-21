package com.yupay.amasua.fv3800.model.jooq.converters;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.AbstractConverter;

/**
 * Base converter for mapping JOOQ {@code VARCHAR} fields to Java {@link Enum} values.<br/>
 * This sealed class provides a generic, type-safe mechanism to translate between the
 * database string representation of an enum and its corresponding Java enum constant.<br/>
 * <br/>
 * Subclasses define the concrete enum type to be handled (e.g. {@code DoiType},
 * {@code EntitySubtype}, etc.) and are referenced from the JOOQ {@code ForcedType}
 * configuration to ensure correct runtime and code-generation behavior.<br/>
 * <br/>
 * Conversion rules:<br/>
 * - {@code null} database values map to {@code null} enum instances.<br/>
 * - {@code null} enum values map to {@code null} database strings.<br/>
 * - Non-null strings are resolved using {@link Enum#valueOf(Class, String)} against
 * the target enum type.<br/>
 * - Serialization uses {@link Enum#name()} to guarantee stable, lossless round-trips.<br/>
 * <br/>
 * This converter is designed to be deterministic, side-effect-free and fully compatible
 * with JOOQ's code generation and runtime execution model.
 *
 * @param <E> the enum type handled by this converter
 * @author David Vidal
 * @version 1.0
 */
public sealed class EnumJooqConverter<E extends Enum<E>>
        extends AbstractConverter<String, E>
        permits DoiTypeConverter, EntitySubtypeConverter, IndirectTypeConverter,
        ListIdsConverter, MaritalStatusConverter {

    /**
     * Creates a new enum converter for the given enum type.<br/>
     * The provided {@code enumType} is used to resolve values during conversion
     * via {@link Enum#valueOf(Class, String)}.
     *
     * @param enumType the concrete enum class to bind to this converter
     */
    @Contract(pure = true)
    public EnumJooqConverter(@NotNull Class<E> enumType) {
        super(String.class, enumType);
    }

    @Contract("null -> null; !null -> !null")
    @Override
    public E from(String s) {
        return s == null ? null : Enum.valueOf(toType(), s);
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Override
    public String to(E e) {
        return e == null ? null : e.name();
    }

}
