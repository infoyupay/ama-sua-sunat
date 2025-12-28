package com.yupay.amasua.fv3800.services.persistence.validation;


import java.util.regex.Pattern;

/**
 * Centralized regular expression patterns used exclusively for
 * Formulario Virtual 3800 validation.
 *
 * <p>
 * These patterns represent normative constraints defined by the
 * SUNAT FV3800 specification and must not be reused outside this context.
 * </p>
 * <p style="border: 1px solid #C9A227;">
 * Note: JEP 526 (Lazy Constants) will be adopted when available as stable release.
 * </p>
 *
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
public final class Patterns {

    /**
     * Order number associated with FV3800 records.
     *
     * <p>
     * Must be a numeric string with a length between 9 and 20 digits.
     * </p>
     */
    public static final Pattern ORDER_NUMBER =
            Pattern.compile("^\\d{9,20}$");
    /**
     * Tax period in {@code YYYYMM} format, restricted to years 2000–2099
     * and months from 01 to 12.
     */
    public static final Pattern TAX_PERIOD =
            Pattern.compile("^20\\d{2}(0[1-9]|1[0-2])$");

    /**
     * Utility class pattern, avoids instantiation.
     */
    private Patterns() {
    }
}
