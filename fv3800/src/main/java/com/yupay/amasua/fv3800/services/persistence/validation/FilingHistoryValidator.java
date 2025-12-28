package com.yupay.amasua.fv3800.services.persistence.validation;

import com.yupay.amasua.fv3800.exception.FV3800PersistenceValidationException;
import com.yupay.amasua.fv3800.model.jooq.tables.pojos.FilingHistory;
import com.yupay.amasua.fv3800.services.persistence.QueryResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.yupay.amasua.fv3800.services.persistence.validation.Patterns.ORDER_NUMBER;
import static com.yupay.amasua.fv3800.services.persistence.validation.Patterns.TAX_PERIOD;

/**
 * Domain validator for {@link com.yupay.amasua.fv3800.model.jooq.tables.pojos.FilingHistory} entities according to
 * Formulario Virtual 3800 persistence rules.
 *
 * <p>
 * This validator enforces normative constraints defined by the FV3800
 * specification and signals any inconsistency by throwing
 * {@link com.yupay.amasua.fv3800.exception.FV3800PersistenceValidationException}.
 * </p>
 *
 * <p>
 * The synchronous validation logic is exposed via {@link #validate(FilingHistory)},
 * while {@link #validateAsync(FilingHistory, Executor)} provides an asynchronous
 * adaptation using the centralized async validation pipeline.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public final class FilingHistoryValidator {

    /**
     * Utility class pattern, avoids instantiation.
     */
    private FilingHistoryValidator() {
    }

    /**
     * Validates the given {@link FilingHistory} instance against FV3800
     * persistence rules.
     *
     * <p>
     * This method performs only domain-level validation and does not handle
     * asynchronous execution or error adaptation.
     * </p>
     *
     * @param h filing history instance to validate
     * @return the same {@code FilingHistory} instance if validation succeeds
     * @throws FV3800PersistenceValidationException if any normative constraint
     *                                              is violated
     */
    @Contract("null -> fail")
    public static @NotNull FilingHistory validate(FilingHistory h) {

        if (h == null) {
            throw new FV3800PersistenceValidationException("FilingHistory is null.");
        }
        if (h.getOrderNumber() == null) {
            throw new FV3800PersistenceValidationException("Order number must be present.");
        }
        if (!ORDER_NUMBER.matcher(h.getOrderNumber()).matches()) {
            throw new FV3800PersistenceValidationException(
                    "Order number must be a numeric text between 9 and 20 digits.");
        }
        if (h.getFiledAt() == null) {
            throw new FV3800PersistenceValidationException("Filed at must be present.");
        }
        if (h.getFiledAt().isAfter(LocalDate.now())) {
            throw new FV3800PersistenceValidationException("Filed at cannot be in the future.");
        }
        if (h.getPeriod() == null) {
            throw new FV3800PersistenceValidationException("Filing period must be present.");
        }
        if (!TAX_PERIOD.matcher(h.getPeriod()).matches()) {
            throw new FV3800PersistenceValidationException(
                    "Filing period must be in the format YYYYMM.");
        }
        // Null or zero id is acceptable in some circumstances. Negative: NEVER.
        if (h.getId() != null && h.getId() < 0) {
            throw new FV3800PersistenceValidationException("FilingHistory id must be positive.");
        }
        return h;
    }

    /**
     * Executes {@link #validate(FilingHistory)} asynchronously using the provided
     * executor and adapts the outcome to the {@link QueryResult} contract.
     *
     * <p>
     * Any {@link RuntimeException} thrown during validation is captured and
     * returned as {@link QueryResult#failure(Throwable)}, ensuring that no
     * exception escapes the asynchronous pipeline.
     * </p>
     *
     * @param h        filing history instance to validate
     * @param executor executor used for asynchronous execution
     * @return a {@code CompletableFuture} holding the validation result
     */
    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<QueryResult<FilingHistory>>
    validateAsync(FilingHistory h, Executor executor) {
        return AsyncValidator.validateAsync(FilingHistoryValidator::validate, h, executor);
    }
}
