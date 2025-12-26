package com.yupay.amasua.fv3800.services.persistence.validation;

import com.yupay.amasua.fv3800.exception.FV3800PersistenceValidationException;
import com.yupay.amasua.fv3800.model.enums.MaritalStatus;
import com.yupay.amasua.fv3800.model.jooq.tables.pojos.Paykuna;
import com.yupay.amasua.fv3800.services.persistence.QueryResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Period;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

/**
 * Validation utility for {@link Paykuna} persistence operations.
 * <br/>
 * This class centralizes all business and consistency validations required
 * before a {@link Paykuna} instance can be persisted. It performs purely
 * synchronous validation and signals invalid states by throwing
 * {@link FV3800PersistenceValidationException}.
 * <br/>
 * An asynchronous adapter is also provided to integrate validation into
 * asynchronous service flows without leaking exceptions, translating
 * validation failures into {@link QueryResult} instances instead.
 * <br/>
 * This class does not perform any persistence operation and does not depend
 * on jOOQ or transaction management. Its sole responsibility is to validate
 * input data according to domain and persistence rules.
 *
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
public final class PaykunaValidator {

    /**
     * Address validation pattern.
     * <br/>
     * Requires a minimum length, at least two separators (comma or dash),
     * at least one digit, and at least one alphabetic character, ensuring
     * that street, district, and state information are present.
     */
    private static final Pattern ADDRESS_PATTERN =
            Pattern.compile("^(?=.{25,}$)(?=(?:.*[,\\-]){2,})(?=.*\\d)(?=.*[A-Za-z]).*$");

    /**
     * Utility class constructor.
     * <br/>
     * This class is not intended to be instantiated.
     */
    private PaykunaValidator() {
    }

    /**
     * Performs synchronous validation of a {@link Paykuna} instance.
     * <br/>
     * This method enforces all domain and persistence-level constraints required
     * for storing a Paykuna record, including DOI consistency, residency rules,
     * marital status constraints, age checks, address format, and basic identity
     * validation.
     * <br/>
     * If any validation rule is violated, a
     * {@link FV3800PersistenceValidationException} is thrown immediately.
     *
     * @param paykuna the Paykuna instance to validate.
     * @return the same Paykuna instance if validation succeeds.
     * @throws FV3800PersistenceValidationException if any validation rule fails.
     */
    public static Paykuna validatePaykuna(Paykuna paykuna) {
        if (paykuna == null) {
            throw new FV3800PersistenceValidationException("Cannot store a null value.");
        }
        if (paykuna.getDoiNumber() == null) {
            throw new FV3800PersistenceValidationException("Paykuna must have a DOI number.");
        }
        if (paykuna.getDoiType() == null) {
            throw new FV3800PersistenceValidationException("Paykuna must have a DOI type.");
        }
        if (!paykuna.getDoiType().test(paykuna.getDoiNumber())) {
            throw new FV3800PersistenceValidationException("Paykuna DOI number must be valid.");
        }
        if (paykuna.getResident() == true && !paykuna.getDoiType().isValidForResidents()) {
            throw new FV3800PersistenceValidationException(
                    "DOI type " + paykuna.getDoiType() + " is not valid for residents."
            );
        }
        if (paykuna.getResident() == false && !paykuna.getDoiType().isValidForNonResidents()) {
            throw new FV3800PersistenceValidationException(
                    "DOI type " + paykuna.getDoiType() + " is not valid for non residents."
            );
        }
        if (paykuna.getBirthday() == null) {
            throw new FV3800PersistenceValidationException("Paykuna must have a birthday.");
        }
        if (paykuna.getMaritalStatus() != MaritalStatus.SINGLE) {
            if (paykuna.getNoticeDate() == null) {
                throw new FV3800PersistenceValidationException(
                        "If Paykuna is not single, must provide a notice date."
                );
            } else if (Period.between(paykuna.getBirthday(), paykuna.getNoticeDate()).getYears() < 12) {
                throw new FV3800PersistenceValidationException(
                        "Notice date is too soon for Paykuna birthday."
                );
            }
        } else {
            if (paykuna.getNoticeDate() != null) {
                throw new FV3800PersistenceValidationException(
                        "Paykuna is single, cannot have a notice date."
                );
            }
        }
        if (paykuna.getAddress() == null
                || !ADDRESS_PATTERN.matcher(paykuna.getAddress()).matches()) {
            throw new FV3800PersistenceValidationException(
                    "Paykuna address is invalid. At least Street, district and state are required."
            );
        }
        if (paykuna.getName() == null || paykuna.getName().strip().length() < 2) {
            throw new FV3800PersistenceValidationException("Paykuna must have a valid name.");
        }
        return paykuna;
    }

    /**
     * Asynchronous adapter for {@link #validatePaykuna(Paykuna)}.
     * <br/>
     * This method executes validation logic asynchronously using the provided
     * {@link Executor}. Any {@link FV3800PersistenceValidationException} thrown
     * during validation is captured and translated into a
     * {@link QueryResult#failure(Throwable)} result.
     * <br/>
     * This adapter allows validation to participate in asynchronous service
     * pipelines without propagating exceptions or breaking
     * {@link CompletableFuture} composition.
     *
     * @param paykuna  the Paykuna instance to validate.
     * @param executor the executor used to run the validation asynchronously.
     * @return a {@link CompletableFuture} containing a {@link QueryResult}
     * representing either successful validation or a failure state.
     */
    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<QueryResult<Paykuna>>
    validatePaykunaAsync(Paykuna paykuna, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return QueryResult.success(validatePaykuna(paykuna));
            } catch (FV3800PersistenceValidationException e) {
                return QueryResult.failure(e);
            }
        }, executor);
    }
}
