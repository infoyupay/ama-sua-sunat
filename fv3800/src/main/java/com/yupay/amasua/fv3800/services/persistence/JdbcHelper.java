package com.yupay.amasua.fv3800.services.persistence;

import com.yupay.amasua.fv3800.exception.FV3800PersistenceException;
import com.yupay.amasua.fv3800.infra.AppContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Low-level JDBC helper utilities for FV3800.
 * <br/>
 * This class provides the most primitive execution layer for JDBC interactions,
 * handling:
 * <ul>
 *   <li>Connection lifecycle</li>
 *   <li>Transaction boundaries</li>
 *   <li>Rollback semantics</li>
 *   <li>Asynchronous execution with context epoch safety</li>
 * </ul>
 *
 * <p>
 * This layer is intentionally minimal and infrastructure-oriented.
 * Higher layers are expected to add semantic meaning (jOOQ DSL usage,
 * DTO mapping, domain results, etc.).
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public final class JdbcHelper {

    /**
     * Utility class constructor.
     * <p>
     * Prevents instantiation. This class is intended to be used in a purely
     * static manner.
     */
    private JdbcHelper() {
    }

    /**
     * Attempts to rollback the current transaction in a best-effort manner.
     * <p>
     * Rollback failures are deliberately ignored, as rollback is only attempted
     * during exceptional flows where the primary error must be preserved.
     *
     * @param conn the JDBC connection to rollback
     */
    public static void rollback(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException _) {
            // Best-effort rollback: ignore secondary failures
        }
    }

    /**
     * Executes a synchronous transactional unit of work.
     *
     * @param ctx  the application context providing the connection
     * @param body the transactional body to execute
     * @param <R>  the result type
     * @return the result produced by the transaction
     * @throws FV3800PersistenceException if execution fails
     */
    public static <R> R lowlevelTransactionSync(
            AppContext ctx,
            Function<Connection, R> body
    ) {
        return lowlevelTransaction(
                ctx.supplyConnection(ctx.currentEpoch()),
                body
        );
    }

    /**
     * Executes a transactional unit of work asynchronously.
     * <p>
     * IMPORTANT:
     * The context epoch MUST be captured <b>before</b> entering the asynchronous
     * execution. Capturing it inside the future would invalidate the epoch
     * mechanism and allow stale tasks to execute against a new context.
     *
     * @param ctx  the application context
     * @param body the transactional body to execute
     * @param <R>  the result type
     * @return a {@link CompletableFuture} representing the asynchronous result
     */
    public static <R> CompletableFuture<R> lowlevelTransactionAsync(
            AppContext ctx,
            Function<Connection, R> body
    ) {
        // Snapshot the epoch BEFORE entering the future.
        var epoch = ctx.currentEpoch();

        return CompletableFuture.supplyAsync(
                () -> lowlevelTransaction(ctx.supplyConnection(epoch), body),
                ctx.jooqExecutor()
        );
    }

    /**
     * Executes a synchronous, non-transactional query.
     *
     * @param ctx   the application context providing the connection
     * @param query the query body to execute
     * @param <R>   the result type
     * @return the query result
     * @throws FV3800PersistenceException if execution fails
     */
    public static <R> R lowlevelQuerySync(
            AppContext ctx,
            Function<Connection, R> query
    ) {
        return lowlevelQuery(
                ctx.supplyConnection(ctx.currentEpoch()),
                query
        );
    }

    /**
     * Executes a non-transactional query asynchronously.
     * <p>
     * As with transactional execution, the context epoch must be captured
     * before scheduling the asynchronous task to avoid stale execution.
     *
     * @param ctx   the application context
     * @param query the query body to execute
     * @param <R>   the result type
     * @return a {@link CompletableFuture} representing the asynchronous result
     */
    public static <R> CompletableFuture<R> lowlevelQueryAsync(
            AppContext ctx,
            Function<Connection, R> query
    ) {
        // Snapshot the epoch BEFORE entering the future.
        var epoch = ctx.currentEpoch();

        return CompletableFuture.supplyAsync(
                () -> lowlevelQuery(ctx.supplyConnection(epoch), query),
                ctx.jooqExecutor()
        );
    }

    /**
     * Executes a transactional unit of work using the provided connection.
     *
     * @param cnx  the JDBC connection
     * @param body the transactional body
     * @param <R>  the result type
     * @return the result produced by the transaction
     * @throws FV3800PersistenceException if execution fails
     */
    public static <R> R lowlevelTransaction(
            Connection cnx,
            Function<Connection, R> body
    ) {
        try (cnx) {
            cnx.setAutoCommit(false);
            var result = body.apply(cnx);
            cnx.commit();
            return result;
        } catch (Exception e) {
            rollback(cnx);
            if (e instanceof FV3800PersistenceException f) {
                throw f;
            }
            throw new FV3800PersistenceException(
                    "Couldn't run transaction via JDBC.",
                    e
            );
        }
    }

    /**
     * Executes a non-transactional query using the provided connection.
     *
     * @param cnx   the JDBC connection
     * @param query the query body
     * @param <R>   the result type
     * @return the query result
     * @throws FV3800PersistenceException if execution fails
     */
    public static <R> R lowlevelQuery(
            Connection cnx,
            Function<Connection, R> query
    ) {
        try (cnx) {
            return query.apply(cnx);
        } catch (Exception e) {
            if (e instanceof FV3800PersistenceException f) {
                throw f;
            }
            throw new FV3800PersistenceException(
                    "Couldn't run query via JDBC.",
                    e
            );
        }
    }
}
