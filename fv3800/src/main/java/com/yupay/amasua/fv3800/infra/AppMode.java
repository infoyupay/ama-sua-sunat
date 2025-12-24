package com.yupay.amasua.fv3800.infra;

/**
 * Defines the execution mode of the FV3800 application.
 * <p>
 * {@code AppMode} expresses how the application should behave with respect to
 * resource management, safety guarantees, and operational constraints.
 * <p>
 * This enum intentionally defines only two modes:
 * <ul>
 *   <li>{@link #TEST} – for isolated, disposable execution during automated or
 *       local testing.</li>
 *   <li>{@link #PRODUCTION} – for normal operation against a live database
 *       file.</li>
 * </ul>
 *
 * <p>
 * The absence of a {@code SANDBOX} mode is deliberate. In FV3800, persistence
 * is file-based; using a different database file (e.g. {@code sandbox.db})
 * constitutes a sandbox by itself, without requiring a distinct execution
 * mode.
 * </p>
 *
 * <p>
 * {@code AppMode} is expected to be consumed primarily by infrastructure-level
 * components such as the {@code AppContext}, not by domain logic.
 * </p>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public enum AppMode {

    /**
     * Test execution mode.
     * <p>
     * In this mode, the application operates against disposable resources,
     * typically using temporary database files and isolated execution
     * contexts.
     * <p>
     * Data created in this mode has no operational or legal value and is
     * expected to be discarded after execution.
     */
    TEST,

    /**
     * Production execution mode.
     * <p>
     * In this mode, the application operates against a live, persistent
     * database file.
     * <p>
     * Data created or modified in this mode is considered authoritative and
     * must be handled with full operational responsibility.
     */
    PRODUCTION
}
