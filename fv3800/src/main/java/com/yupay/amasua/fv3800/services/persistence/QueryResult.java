package com.yupay.amasua.fv3800.services.persistence;

import com.yupay.amasua.fv3800.exception.FV3800Exception;

/**
 * Represents the outcome of a low-level persistence operation.
 * <p>
 * A {@code QueryResult} models a computation that can either:
 * <ul>
 *   <li>complete successfully with a non-null value, or</li>
 *   <li>fail with a non-null {@link Throwable}.</li>
 * </ul>
 *
 * <p>
 * Exactly one of {@code value} or {@code error} must be non-null.
 * This invariant is enforced at construction time.
 * </p>
 *
 * <p>
 * This type is intended to be used as a transport and composition
 * mechanism in infrastructure and persistence layers, avoiding
 * control-flow driven by exceptions while still preserving error
 * information.
 * </p>
 *
 * @param value the successful result value, or {@code null} if the operation failed
 * @param error the failure cause, or {@code null} if the operation succeeded
 * @param <R>   the result type
 * @author InfoYupay SACS
 * @version 1.0
 */
public record QueryResult<R>(R value, Throwable error) {

    /**
     * Canonical constructor enforcing the invariant that exactly one
     * of {@code value} or {@code error} must be non-null.
     *
     * @param error the failure cause, or {@code null} if the operation succeeded
     * @param value the successful result value, or {@code null} if the operation failed
     * @throws FV3800Exception if both parameters are null
     *                         or both are non-null
     */
    public QueryResult {
        if ((value == null) == (error == null)) {
            throw new FV3800Exception(
                    "Exactly one of value or error must be non-null."
            );
        }
    }

    /**
     * Creates a successful {@code QueryResult} holding the given value.
     *
     * @param value the successful result value
     * @param <R>   the result type
     * @return a successful {@code QueryResult}
     */
    public static <R> QueryResult<R> success(R value) {
        return new QueryResult<>(value, null);
    }

    /**
     * Creates a failed {@code QueryResult} holding the given error.
     *
     * @param error the failure cause
     * @param <R>   the result type
     * @return a failed {@code QueryResult}
     */
    public static <R> QueryResult<R> failure(Throwable error) {
        return new QueryResult<>(null, error);
    }

    /**
     * Indicates whether this result represents a successful outcome.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * Indicates whether this result represents a failed outcome.
     *
     * @return {@code true} if the operation failed, {@code false} otherwise
     */
    public boolean isFailure() {
        return error != null;
    }
}
