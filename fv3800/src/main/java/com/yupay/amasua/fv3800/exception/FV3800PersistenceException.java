package com.yupay.amasua.fv3800.exception;

/**
 * Unchecked exception representing persistence-layer failures in FV3800.
 * <p>
 * {@code FV3800PersistenceException} is thrown when an error occurs while
 * interacting with the data storage layer, including (but not limited to)
 * SQL execution, connection handling, transaction boundaries, or ORM / DSL
 * integration issues.
 * <p>
 * This exception acts as a semantic wrapper around lower-level technical
 * exceptions such as {@link java.sql.SQLException} or framework-specific
 * errors, allowing the rest of the application to reason in terms of
 * <b>persistence failures</b> rather than vendor or technology details.
 * <p>
 * Design notes:
 * <ul>
 *   <li>This exception should be thrown at repository or persistence-adapter
 *       boundaries.</li>
 *   <li>Low-level checked exceptions must be wrapped into this type (or a more
 *       specific subclass) before propagating upward.</li>
 *   <li>Callers are not expected to recover locally; handling typically
 *       involves logging, transaction rollback, or user-facing error
 *       translation.</li>
 * </ul>
 *
 * <p>
 * Future subclasses may further specialize persistence failures
 * (e.g. constraint violations, optimistic locking, connection exhaustion).
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class FV3800PersistenceException extends FV3800Exception {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public FV3800PersistenceException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public FV3800PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public FV3800PersistenceException(Throwable cause) {
        super(cause);
    }
}
