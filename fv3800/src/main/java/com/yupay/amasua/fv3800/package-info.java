/**
 * Root package of the FV-3800 module.<br/>
 * <br/>
 * This module provides a structured, explicit, and auditable model for working
 * with data and processes related to the Formulario Virtual 3800, with a primary
 * focus on correctness, traceability, and long-term maintainability in regulated
 * fiscal contexts.<br/>
 * <br/>
 * The FV-3800 module is intentionally designed as a <b>cohesive monolith</b>.
 * Its internal packages are organized by responsibility (domain catalogs,
 * persistence integration, generated schema models, etc.), but they are
 * developed, versioned, and released as a single unit to ensure consistency
 * and eliminate hidden coupling across modules.<br/>
 * <br/>
 * <b>Design principles:</b><br/>
 * <ul>
 *   <li>Explicit domain modeling using closed, well-documented catalogs.</li>
 *   <li>Clear separation between domain semantics and persistence mechanics.</li>
 *   <li>Deterministic, auditable persistence strategies suitable for tax data.</li>
 *   <li>Fail-fast behavior for contract violations and malformed data.</li>
 *   <li>Preference for clarity and correctness over convenience or implicit behavior.</li>
 * </ul>
 * <br/>
 * This package acts as the architectural anchor of the module, providing
 * a stable namespace under which all FV-3800-related components coexist.
 * While internal structures may evolve, the module boundary defined here
 * represents a single, deliberate unit of deployment and responsibility.<br/>
 * <br/>
 * The FV-3800 module follows the InfoYupay Way: explicit contracts, narrow
 * responsibilities, generated code treated as an artifact, and documentation
 * used as a first-class design tool.
 *
 * @author David Vidal
 * @version 1.0
 */
package com.yupay.amasua.fv3800;
