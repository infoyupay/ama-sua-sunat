package com.yupay.amasua.fv3800.infra;

import com.yupay.amasua.fv3800.exception.FV3800Exception;
import com.yupay.amasua.fv3800.exception.FV3800PersistenceException;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Central infrastructure context for the FV3800 application.
 * <p>
 * {@code AppContext} is responsible for managing application-wide infrastructure
 * concerns such as:
 * <ul>
 *   <li>Database file selection and connection creation.</li>
 *   <li>Executor lifecycle and controlled exposure.</li>
 *   <li>Concurrency safety during context switches.</li>
 *   <li>Invalidation of in-flight asynchronous tasks via context epochs.</li>
 * </ul>
 *
 * <p>
 * The context models the concept of a <b>unitary execution universe</b>.
 * When the active database file changes, the previous universe is considered
 * invalid, and all pending work associated with it must be rejected.
 * </p>
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public final class AppContext {

    /**
     * Application execution mode.
     * <p>
     * Currently informational, but kept to allow future policy decisions
     * at the infrastructure level.
     */
    private final AppMode appMode;
    /**
     * Global lifecycle lock.
     * <p>
     * Protects all state transitions related to:
     * <ul>
     *   <li>Database switching</li>
     *   <li>Executor startup and shutdown</li>
     *   <li>Connection provisioning</li>
     * </ul>
     */
    private final ReentrantLock lifecycleLock = new ReentrantLock(true);
    /**
     * Context epoch counter.
     * <p>
     * Incremented every time the execution universe changes.
     * Tasks capture the epoch at creation time and must verify it
     * before mutating any persistent state.
     */
    private final AtomicLong epoch = new AtomicLong(0);
    /**
     * Absolute path of the currently active database file.
     */
    private Path dbPath;
    /**
     * Executor dedicated to persistence and jOOQ-related work.
     */
    private ExecutorService jooqExecutor;
    /**
     * General-purpose executor for CPU-bound or mapping tasks.
     */
    private ExecutorService generalExecutor;

    /**
     * Creates a new {@code AppContext} instance.
     *
     * @param appMode the application execution mode
     */
    public AppContext(AppMode appMode) {
        this.appMode = Objects.requireNonNull(appMode);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Application execution mode.
     *
     * @return value of property.
     */
    public AppMode getAppMode() {
        return appMode;
    }

    /* =========================================================
       Lifecycle management
       ========================================================= */

    /**
     * Switches the active database file and resets the execution context.
     * <p>
     * This operation is atomic and performs the following steps:
     * <ol>
     *   <li>Invalidates all in-flight tasks by incrementing the context epoch.</li>
     *   <li>Stops existing executors gracefully, then forcefully if required.</li>
     *   <li>Sets the new database path.</li>
     *   <li>Starts fresh executors bound to the new context.</li>
     * </ol>
     *
     * @param newDbPath path to the database file to activate
     */
    public void setDbPath(Path newDbPath) {
        Objects.requireNonNull(newDbPath, "dbPath");

        lifecycleLock.lock();
        try {
            epoch.incrementAndGet();
            stopExecutors();
            this.dbPath = newDbPath.toAbsolutePath();
            startExecutors();
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * Initializes the executor services for the current context.
     *
     * @throws FV3800Exception if executors are already started
     */
    private void startExecutors() {
        if (jooqExecutor != null || generalExecutor != null) {
            throw new FV3800Exception("Executors already started.");
        }
        jooqExecutor = Executors.newVirtualThreadPerTaskExecutor();
        generalExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Stops and clears all executor services associated with the current context.
     */
    private void stopExecutors() {
        stopExecutor(jooqExecutor);
        stopExecutor(generalExecutor);
        jooqExecutor = null;
        generalExecutor = null;
    }

    /**
     * Attempts a graceful shutdown of an executor, escalating to a forced
     * shutdown if it does not terminate within a fixed timeout.
     *
     * @param executor the executor to stop
     */
    private void stopExecutor(ExecutorService executor) {
        if (executor != null) {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executor.shutdownNow();
            }
        }
    }

    /**
     * Shutdown hook invoked during JVM termination.
     * <p>
     * Invalidates the current context and releases all resources.
     */
    private void shutdown() {
        lifecycleLock.lock();
        try {
            epoch.incrementAndGet();
            stopExecutors();
        } finally {
            lifecycleLock.unlock();
        }
    }

    /* =========================================================
       Executor exposure
       ========================================================= */

    /**
     * Returns an executor intended for persistence-related tasks.
     *
     * @return an {@link Executor} for jOOQ and database work
     * @throws FV3800Exception if the context is not initialized
     */
    public Executor jooqExecutor() {
        lifecycleLock.lock();
        try {
            ensureStarted();
            return jooqExecutor;
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * Returns a general-purpose executor.
     *
     * @return an {@link Executor} for non-persistence work
     * @throws FV3800Exception if the context is not initialized
     */
    public Executor generalExecutor() {
        lifecycleLock.lock();
        try {
            ensureStarted();
            return generalExecutor;
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * Ensures that the context has been fully initialized.
     *
     * @throws FV3800Exception if the context is not ready
     */
    private void ensureStarted() {
        if (dbPath == null || jooqExecutor == null || generalExecutor == null) {
            throw new FV3800Exception("AppContext has not been initialized.");
        }
    }

    /* =========================================================
       Context epoch
       ========================================================= */

    /**
     * Returns the current context epoch.
     *
     * @return the current epoch value
     */
    public long currentEpoch() {
        return epoch.get();
    }

    /**
     * Determines whether a given epoch value is still valid.
     *
     * @param taskEpoch the epoch captured by a task
     * @return {@code true} if the epoch matches the current context
     */
    public boolean isEpochValid(long taskEpoch) {
        return epoch.get() == taskEpoch;
    }

    /* =========================================================
       Connections
       ========================================================= */

    /**
     * Creates a new JDBC {@link Connection} for the current context.
     * <p>
     * The caller must ensure the provided epoch is still valid before
     * performing any state mutation.
     *
     * @param taskEpoch the epoch associated with the calling task
     * @return a new {@link Connection}
     * @throws FV3800Exception      if the task belongs to a stale context
     * @throws FV3800PersistenceException if the connection cannot be created
     */
    public Connection supplyConnection(long taskEpoch) {
        lifecycleLock.lock();
        try {
            ensureStarted();

            if (!isEpochValid(taskEpoch)) {
                throw new FV3800Exception(
                        "Stale task detected (context epoch mismatch)."
                );
            }

            return DriverManager.getConnection(
                    "jdbc:sqlite:" + dbPath
            );
        } catch (SQLException e) {
            throw new FV3800PersistenceException(
                    "Failed to open SQLite connection for " + dbPath, e
            );
        } finally {
            lifecycleLock.unlock();
        }
    }
}
