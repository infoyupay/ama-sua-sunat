package com.yupay.amasua.fv3800.services.persistence;

import com.yupay.amasua.fv3800.infra.AppContext;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * High-level persistence entry point for jOOQ-based operations.
 * <p>
 * {@code JooqPersistenceContext} defines the semantic boundary between low-level JDBC
 * execution and higher-level persistence logic expressed in terms of
 * {@link DSLContext}.
 *
 * <p>
 * Implementors are only required to provide access to the {@link AppContext}.
 * All query and transaction handling, including synchronous and asynchronous
 * execution, error capture, and executor selection, is provided via default
 * methods.
 * </p>
 *
 * <p>
 * This interface intentionally converts exceptions into {@link QueryResult}
 * instances, making persistence outcomes explicit and composable without
 * control-flow driven by exceptions.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public interface JooqPersistenceContext {

    /**
     * Function responsible for creating a {@link DSLContext} from a JDBC
     * {@link Connection}.
     */
    static final Function<Connection, DSLContext> DSL_CREATOR = DSL::using;

    /**
     * Returns the application context associated with this instance.
     *
     * @return the {@link AppContext}
     */
    AppContext context();

    /**
     * Executes a synchronous, non-transactional read operation.
     *
     * @param query the query expressed in terms of {@link DSLContext}
     * @param <R>   the result type
     * @return a {@link QueryResult} representing success or failure
     */
    default <R> QueryResult<R> readSync(Function<DSLContext, R> query) {
        try {
            return JdbcHelper.lowlevelQuerySync(
                    context(),
                    DSL_CREATOR.andThen(query).andThen(QueryResult::success)
            );
        } catch (Exception e) {
            return QueryResult.failure(e);
        }
    }

    /**
     * Executes a non-transactional read operation asynchronously.
     *
     * @param query the query expressed in terms of {@link DSLContext}
     * @param <R>   the result type
     * @return a {@link CompletableFuture} yielding a {@link QueryResult}
     */
    default <R> CompletableFuture<QueryResult<R>> readAsync(Function<DSLContext, R> query) {
        return JdbcHelper.lowlevelQueryAsync(
                        context(),
                        DSL_CREATOR.andThen(query)
                )
                .thenApplyAsync(QueryResult::success, context().generalExecutor())
                .exceptionallyAsync(QueryResult::failure, context().generalExecutor());
    }

    /**
     * Executes a synchronous transactional operation.
     *
     * @param body the transactional body expressed in terms of {@link DSLContext}
     * @param <R>  the result type
     * @return a {@link QueryResult} representing success or failure
     */
    default <R> QueryResult<R> inTransactionSync(Function<DSLContext, R> body) {
        try {
            return JdbcHelper.lowlevelTransactionSync(
                    context(),
                    DSL_CREATOR.andThen(body).andThen(QueryResult::success)
            );
        } catch (Exception e) {
            return QueryResult.failure(e);
        }
    }

    /**
     * Executes a transactional operation asynchronously.
     *
     * @param body the transactional body expressed in terms of {@link DSLContext}
     * @param <R>  the result type
     * @return a {@link CompletableFuture} yielding a {@link QueryResult}
     */
    default <R> CompletableFuture<QueryResult<R>> inTransactionAsync(Function<DSLContext, R> body) {
        return JdbcHelper.lowlevelTransactionAsync(
                        context(),
                        DSL_CREATOR.andThen(body)
                )
                .thenApplyAsync(QueryResult::success, context().generalExecutor())
                .exceptionallyAsync(QueryResult::failure, context().generalExecutor());
    }
}
