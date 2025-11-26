package com.yupay.amasua.fv3800.helpers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntPredicate;

/**
 * Utility methods for lightweight string sanitization and filtering.<br/>
 * The operations provided here are pure, stateless and designed for
 * performance-critical paths where stream-based filtering would add
 * unnecessary overhead.
 *
 * @author David Vidal
 * @version 1.0
 */
public class Strings {

    /**
     * Private constructor to honour utility class pattern.
     */
    @Contract(pure = true)
    private Strings() {
    }

    /**
     * Returns a new string containing only the characters from the input
     * {@code s} that satisfy the given {@link IntPredicate}.<br/>
     * Characters that pass the filter are upper-cased before being appended
     * to the output buffer.<br/>
     * If {@code maxChars} is greater than zero, the result will be truncated
     * once that limit is reached.
     *
     * @param s        the input string to sanitize (never {@code null})
     * @param filter   predicate used to decide whether each character is accepted
     * @param maxChars maximum number of accepted characters to include;
     *                 if {@code maxChars <= 0}, no limit is applied
     * @return a new sanitized string containing only the filtered characters
     */
    @Contract("!null, _, _ -> new; null, _, _ -> null")
    public static @Nullable String sanitize(@Nullable String s,
                                            IntPredicate filter,
                                            int maxChars) {
        if (s == null) return null;
        var len = s.length();
        var out = new char[len];
        var j = 0;

        for (var i = 0; i < len; i++) {
            var c = s.charAt(i);
            if (filter.test(c)) {
                out[j++] = Character.toUpperCase(c);

                // Early stop if the limit is reached
                if (maxChars > 0 && j >= maxChars) {
                    return new String(out, 0, maxChars);
                }
            }
        }

        // No limit, or limit not reached
        return new String(out, 0, j);
    }

    /**
     * Returns a string containing only the digit characters found in {@code s}.<br/>
     * If {@code maxChars} is greater than zero, the output is truncated to
     * that number of characters.
     *
     * @param s        the input string (never {@code null})
     * @param maxChars maximum number of digits to include;
     *                 if {@code maxChars <= 0}, no limit is applied
     * @return a string composed exclusively of digit characters
     */
    @Contract("!null, _ -> new; null, _ -> null")
    public static @Nullable String onlyDigits(@Nullable String s, int maxChars) {
        return sanitize(s, Character::isDigit, maxChars);
    }

    /**
     * Returns a string containing only the digit characters found in {@code s}.<br/>
     * No character limit is applied.
     *
     * @param s the input string (never {@code null})
     * @return a string composed exclusively of digit characters
     */
    @Contract("!null -> new; null -> null")
    public static @Nullable String onlyDigits(@Nullable String s) {
        return onlyDigits(s, 0);
    }

    /**
     * Returns a string containing only alphanumeric characters found in {@code s}.<br/>
     * As with {@link #sanitize(String, IntPredicate, int)}, accepted characters are
     * upper-cased. If {@code maxChars} is greater than zero, the output is
     * truncated to that number of characters.
     *
     * @param s        the input string (never {@code null})
     * @param maxChars maximum number of characters to include;
     *                 if {@code maxChars <= 0}, no limit is applied
     * @return a string composed exclusively of alphanumeric characters
     */
    @Contract("!null, _ -> new; null, _ -> null")
    public static @Nullable String alnum(@Nullable String s, int maxChars) {
        return sanitize(s, Character::isLetterOrDigit, maxChars);
    }

    /**
     * Returns a string containing only alphanumeric characters found in {@code s}.<br/>
     * No character limit is applied.
     *
     * @param s the input string (never {@code null})
     * @return a string composed exclusively of alphanumeric characters
     */
    @Contract("!null -> new; null -> null")
    public static @Nullable String alnum(@Nullable String s) {
        return alnum(s, 0);
    }
}
