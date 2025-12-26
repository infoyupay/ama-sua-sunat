package com.yupay.amasua.fv3800.exception;
/**
 * Exception thrown when persistence-level validation rules are violated.
 * <br/>
 * This exception represents validation errors detected before attempting
 * any database interaction. It is typically raised by validation utilities
 * to signal that the provided data does not satisfy the constraints required
 * for persistence.
 * <br/>
 * Although this exception is a runtime exception, it is not intended to
 * propagate beyond service boundaries. In asynchronous service flows, it is
 * usually captured and translated into a {@link com.yupay.amasua.fv3800.services.persistence.QueryResult}
 * failure, preserving a stable, exception-free public contract.
 * <br/>
 * This exception does not indicate database or infrastructure failures, but
 * rather invalid input or inconsistent domain state at persistence time.
 *
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
public class FV3800PersistenceValidationException extends FV3800PersistenceException {

    /**
     * Constructs a new validation exception with the specified detail message.
     * <br/>
     * The cause is not initialized and may be set later via
     * {@link #initCause(Throwable)} if required.
     *
     * @param message the detail message describing the validation failure.
     */
    public FV3800PersistenceValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new validation exception with the specified detail message
     * and cause.
     * <br/>
     * The detail message associated with {@code cause} is not automatically
     * incorporated into this exception's message.
     *
     * @param message the detail message describing the validation failure.
     * @param cause   the underlying cause of the validation failure, if any.
     */
    public FV3800PersistenceValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new validation exception with the specified cause.
     * <br/>
     * The detail message is set to {@code cause.toString()}, which typically
     * includes the class name and message of the cause.
     * <br/>
     * This constructor is useful when the validation error is primarily a
     * wrapper around another throwable.
     *
     * @param cause the underlying cause of the validation failure.
     */
    public FV3800PersistenceValidationException(Throwable cause) {
        super(cause);
    }
}
