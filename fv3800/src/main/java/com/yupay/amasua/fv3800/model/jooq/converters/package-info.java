/**
 * JOOQ converters defining explicit persistence strategies for the FV-3800 module.<br/>
 * <br/>
 * This package centralizes all {@link org.jooq.Converter} implementations used
 * to translate between database representations and strongly typed Java values
 * when interacting with SQLite-backed schemas.<br/>
 * <br/>
 * The converters declared here embody <b>explicit storage decisions</b>, not
 * domain logic. They define how values are persisted and retrieved, enforcing
 * deterministic, auditable, and reversible mappings suitable for regulated
 * fiscal and tax-related data.<br/>
 * <br/>
 * <b>Design principles:</b><br/>
 * <ul>
 *   <li>No silent normalization, rounding, or coercion is performed.</li>
 *   <li>All conversions are deterministic and side-effect-free.</li>
 *   <li>Contract violations fail fast and explicitly.</li>
 *   <li>Semantic interpretation belongs to higher layers, never to converters.</li>
 * </ul>
 * <br/>
 * The package includes:
 * <ul>
 *   <li>Enum converters that map closed, tax-facing catalogs using
 *       {@link java.lang.Enum#name()} for stable round-trip persistence.</li>
 *   <li>Date converters that persist {@link java.time.LocalDate} values using
 *       epoch-day representations, avoiding time-zone and timestamp ambiguity.</li>
 *   <li>Fixed-scale decimal converters that persist {@link java.math.BigDecimal}
 *       values as unscaled integers, enforcing precision contracts explicitly.</li>
 * </ul>
 * <br/>
 * All converters in this package are intended to be referenced from JOOQ
 * {@code ForcedType} configurations and are considered part of the module's
 * persistence contract. Changes must be performed with care, as they may affect
 * database schemas, serialized data, and interoperability with external systems.<br/>
 * <br/>
 * The converters declared in this package follow the InfoYupay Way: explicit
 * contracts, narrow responsibilities, and zero hidden behavior.
 *
 * @author David Vidal
 * @version 1.0
 */
package com.yupay.amasua.fv3800.model.jooq.converters;
