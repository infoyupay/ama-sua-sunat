package com.yupay.amasua.fv3800.services.persistence.validation;

import com.yupay.amasua.fv3800.services.persistence.QueryResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;

/**
 * Utility class responsible for executing validation logic asynchronously
 * and adapting the result to the {@link QueryResult} contract.
 *
 * <p>
 * This helper centralizes the creation of asynchronous validation pipelines,
 * allowing individual validators to focus exclusively on domain consistency
 * checks without dealing with {@link CompletableFuture} mechanics or
 * exception-to-result adaptation.
 * </p>
 *
 * <p>
 * Validators are expected to signal domain inconsistencies by throwing
 * {@code FV3800PersistenceValidatorException}. Any other {@link RuntimeException}
 * is considered an unexpected failure (e.g. programming error or infrastructure
 * issue) and is intentionally handled in the same way, ensuring that no
 * exception escapes the asynchronous pipeline.
 * </p>
 *
 * <p>
 * This design guarantees a uniform failure channel for all validation steps,
 * preserving pipeline stability and simplifying error propagation.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public final class AsyncValidator {

    /**
     * Utility class pattern, avoids instantiation.
     */
    private AsyncValidator() {
    }

    /**
     * Executes the given validator asynchronously using the provided executor
     * and wraps the outcome into a {@link QueryResult}.
     *
     * <p>
     * Successful validation results in {@link QueryResult#success(Object)}.
     * Any {@link RuntimeException} thrown during validation is captured and
     * returned as {@link QueryResult#failure(Throwable)}.
     * </p>
     *
     * @param validator domain validation logic to be executed
     * @param value     value to be validated
     * @param executor  executor used for asynchronous execution
     * @param <T>       validated value type
     * @return a {@code CompletableFuture} holding the validation result
     */
    @Contract("_, _, _ -> new")
    public static <T> @NotNull CompletableFuture<QueryResult<T>> validateAsync(
            UnaryOperator<T> validator,
            T value,
            Executor executor) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return QueryResult.success(validator.apply(value));
            } catch (RuntimeException e) {
                return QueryResult.failure(e);
            }
        }, executor);
    }
}
