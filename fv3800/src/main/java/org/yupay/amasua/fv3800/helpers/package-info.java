/**
 * Helper classes for lightweight, allocation-efficient string operations.<br/>
 * This package provides utilities intended for fast preprocessing of textual
 * input in FV-3800 workflows, where sanitization and predictable filtering
 * are required prior to domain parsing.<br/>
 * <br/>
 * The helpers included here are intentionally minimal, side-effect free and
 * designed for repeated use in performance-sensitive sections of the system.
 * They avoid stream pipelines and other high-overhead constructs, favoring
 * direct character iteration for maximum throughput.<br/>
 * <br/>
 * At present, this package contains the {@link org.yupay.amasua.fv3800.helpers.Strings}
 * utility class, which offers methods for extracting digits, alphanumeric
 * characters and custom-filtered subsets of a string with optional length
 * limits.<br/>
 * <br/>
 * Future helpers may be added as the FV-3800 module evolves, keeping the
 * principle of clear semantics and low-level efficiency consistent with the
 * InfoYupay Way.
 *
 * @author David Vidal
 * @version 1.0
 */
package org.yupay.amasua.fv3800.helpers;
