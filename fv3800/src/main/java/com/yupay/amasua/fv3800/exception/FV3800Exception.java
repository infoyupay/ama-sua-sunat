package com.yupay.amasua.fv3800.exception;

/**
 * Base unchecked exception for the FV3800 project.
 * <p>
 * {@code FV3800Exception} serves as the root of the custom exception hierarchy
 * and represents domain- or infrastructure-level failures that are considered
 * unrecoverable at the immediate call site.
 * <p>
 * This exception intentionally extends {@link RuntimeException} to avoid
 * polluting method signatures with technical concerns, while still allowing
 * precise categorization through specialized subclasses (e.g. persistence,
 * integration, validation).
 * <p>
 * Design notes:
 * <ul>
 *   <li>This class is meant to be thrown by core modules when a failure crosses
 *       a subsystem boundary.</li>
 *   <li>It supports message-only, cause-only, and message-plus-cause
 *       construction to preserve diagnostic context.</li>
 *   <li>Checked exceptions should be wrapped into {@code FV3800Exception}
 *       or one of its subclasses when escaping low-level layers.</li>
 * </ul>
 *
 * <p>
 * Subclasses are expected to express <b>where</b> and <b>why</b> the failure
 * occurred, not <b>how to recover</b> from it.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class FV3800Exception extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public FV3800Exception(String message) {
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
    public FV3800Exception(String message, Throwable cause) {
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
    public FV3800Exception(Throwable cause) {
        super(cause);
    }
}
