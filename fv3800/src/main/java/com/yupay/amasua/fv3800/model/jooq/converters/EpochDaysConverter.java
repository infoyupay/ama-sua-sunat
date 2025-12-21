package com.yupay.amasua.fv3800.model.jooq.converters;

import org.jooq.impl.AbstractConverter;

import java.time.LocalDate;

/**
 * JOOQ converter for mapping {@link LocalDate} values to and from a
 * {@code LONG} database representation based on epoch days.<br/>
 * <br/>
 * This converter is designed for use with SQLite, which does not provide
 * a native date type. Dates are persisted as the number of days elapsed
 * since the Unix epoch (1970-01-01), using {@link LocalDate#toEpochDay()}
 * and {@link LocalDate#ofEpochDay(long)} for deterministic, time-zone-free
 * round-trip conversion.<br/>
 * <br/>
 * <strong>Persistence strategy:</strong><br/>
 * <ul>
 *   <li>{@link LocalDate} values are serialized as epoch days ({@code LONG}).</li>
 *   <li>No time-of-day or time-zone information is stored or inferred.</li>
 *   <li>The conversion is fully reversible and lossless for date-only values.</li>
 * </ul>
 * <br/>
 * This converter is intended to be referenced from JOOQ {@code ForcedType}
 * configurations and used consistently across all date fields persisted
 * in SQLite-backed schemas.
 *
 * <p>
 * This class is strictly infrastructural. It does not perform validation,
 * calendar normalization, or semantic interpretation of dates.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public class EpochDaysConverter extends AbstractConverter<Long, LocalDate> {

    /**
     * Creates a new converter for mapping epoch-day values to {@link LocalDate}
     * instances and vice versa.
     */
    public EpochDaysConverter() {
        super(Long.class, LocalDate.class);
    }

    /**
     * Converts a database {@code LONG} value representing epoch days into
     * a {@link LocalDate}.
     *
     * <p>
     * The input value is interpreted as the number of days since
     * {@code 1970-01-01}. No time-zone adjustments or offsets are applied.
     * </p>
     *
     * @param databaseObject the epoch-day value from the database, may be {@code null}
     * @return the corresponding {@link LocalDate}, or {@code null} if the input is {@code null}
     */
    @Override
    public LocalDate from(Long databaseObject) {
        return databaseObject == null ? null : LocalDate.ofEpochDay(databaseObject);
    }

    /**
     * Converts a {@link LocalDate} into its database {@code LONG} representation
     * based on epoch days.
     *
     * <p>
     * The returned value represents the number of days elapsed since
     * {@code 1970-01-01}, ensuring a stable, time-zone-independent
     * persistence format.
     * </p>
     *
     * @param userObject the {@link LocalDate} to convert, may be {@code null}
     * @return the epoch-day value, or {@code null} if the input is {@code null}
     */
    @Override
    public Long to(LocalDate userObject) {
        return userObject == null ? null : userObject.toEpochDay();
    }
}
