/**
 * Core implementation of the FV-3800 module.<br/>
 * This module encapsulates domain models, helpers, DTO processing and
 * workflow-specific utilities required for the FV-3800 subsystem within the
 * AmaSua platform.<br/>
 * <br/>
 * Being monolithic by design, the module consolidates domain logic, parsing
 * routines, validations and support classes into a single deployment unit,
 * maintaining a strict boundary against external subsystems.<br/>
 * <br/>
 * The architecture adheres to the InfoYupay Way, emphasizing clear semantics,
 * deterministic behavior and predictable performance under sustained load.<br/>
 * <br/>
 * Internal packages include:
 * <ul>
 *   <li><b>helpers</b>: low-level, allocation-efficient utilities for string
 *       sanitization and preprocessing (e.g., {@code Strings}).</li>
 *   <li><i>(otros paquetes se documentarán conforme sean introducidos)</i></li>
 * </ul>
 * <br/>
 * This module exports only the packages explicitly required by dependent
 * components of AmaSua, keeping all other internals encapsulated.
 *
 * @author David Vidal
 */
@SuppressWarnings("module")
module fv3800 {
    requires org.jooq;
    requires org.jetbrains.annotations;
}