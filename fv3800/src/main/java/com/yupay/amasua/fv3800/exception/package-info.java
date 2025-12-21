/**
 * Provides the exception hierarchy for the FV3800 project.
 * <p>
 * This package defines the root unchecked exception types used across the
 * application to represent failures that cross module or subsystem
 * boundaries.
 * <p>
 * The exception model is intentionally structured as a small, extensible
 * hierarchy rooted at {@link com.yupay.amasua.fv3800.exception.FV3800Exception},
 * allowing higher layers to reason about failures in semantic terms
 * (e.g. persistence, integration) rather than low-level technical details.
 * <p>
 * Design principles:
 * <ul>
 *   <li>Exceptions in this package are unchecked and represent non-recoverable
 *       conditions at the immediate call site.</li>
 *   <li>Lower-level checked exceptions should be wrapped before escaping
 *       infrastructure or adapter layers.</li>
 *   <li>The hierarchy is expected to grow gradually as new concerns emerge,
 *       without breaking existing contracts.</li>
 * </ul>
 *
 * <p>
 * This package does not define error-handling policies; it only defines
 * exception semantics. Handling strategies are responsibility of the
 * application layers that consume these exceptions.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
package com.yupay.amasua.fv3800.exception;
