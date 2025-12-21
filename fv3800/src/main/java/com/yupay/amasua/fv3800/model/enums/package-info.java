/**
 * Enumerations defining fixed, tax-facing categorical values used throughout
 * the FV-3800 module.<br/>
 * <br/>
 * This package centralizes closed, type-safe catalogs that represent legally
 * or administratively defined classifications (e.g. tax codes, entity subtypes,
 * indirect relationship types, marital status) as required by SUNAT-related
 * disclosures and validations.<br/>
 * <br/>
 * The enums declared here are <b>descriptive by design</b>: they model declared
 * facts or classifications, but do not encode business rules, legal inferences,
 * or decision logic. Any interpretation or behavioral semantics must be handled
 * explicitly in higher layers of the system.<br/>
 * <br/>
 * All enums in this package are treated as stable domain contracts and may be
 * referenced by DTOs, persistence models, validation layers, or integration
 * components. Changes must be performed with care, as they may impact
 * serialization formats, database mappings, or interoperability with external
 * systems.<br/>
 * <br/>
 * The enums declared in this package follow the InfoYupay Way: explicit semantics,
 * closed catalogs, and responsibilities that are clear, narrow, and auditable.
 *
 * @author David Vidal
 * @version 1.0
 */
package com.yupay.amasua.fv3800.model.enums;
