/**
 * High-level persistence services for the FV3800 project.
 * <p>
 * This package defines the semantic persistence layer built on top of
 * low-level JDBC and jOOQ infrastructure. Its responsibility is to expose
 * persistence operations as explicit services, rather than traditional
 * DAO-style abstractions.
 * </p>
 *
 * <p>
 * Key characteristics of this layer include:
 * </p>
 * <ul>
 *   <li>Explicit ownership of transaction boundaries at the service level</li>
 *   <li>Clear separation between infrastructure concerns and domain logic</li>
 *   <li>Support for synchronous and asynchronous execution models</li>
 *   <li>Consistent representation of outcomes via {@code QueryResult}</li>
 *   <li>Integration with {@link com.yupay.amasua.fv3800.infra.AppContext}</li>
 * </ul>
 *
 * <p>
 * Classes in this package are intended to be implemented by persistence
 * services that perform validation, mapping, and coordination of database
 * operations using jOOQ, while delegating connection management, concurrency,
 * and error handling to the underlying infrastructure.
 * </p>
 *
 * <p>
 * This design favors explicitness, composability, and architectural honesty
 * over implicit behavior and hidden side effects.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
package com.yupay.amasua.fv3800.services.persistence;
