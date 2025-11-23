------------------------------------------------------------
-- HEADER INFO
------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "header_info"
(
    "id"                  INTEGER PRIMARY KEY NOT NULL DEFAULT 1 CHECK ("id" = 1),
    "ruc"                 TEXT                NOT NULL,
    "periodo"             TEXT                NOT NULL,
    "is_incorporated"     INTEGER             NOT NULL CHECK ("is_incorporated" IN (0, 1)),

    "entity_subtype"      TEXT CHECK ("entity_subtype" IN
                                      ('INVESTMENT_FUND', 'FIDEICOMISO', 'TRUST', 'OTHER')),
    "entity_subtype_name" TEXT,

    "tin"                 TEXT,
    "legal_name"          TEXT                NOT NULL,
    "country"             INTEGER             NOT NULL,
    "mobile"              TEXT                NOT NULL,
    "mail"                TEXT                NOT NULL,

    -- master CHECK: incorporated vs not incorporated.
    CHECK (
        (
            is_incorporated = 1
                AND entity_subtype IS NULL
                AND entity_subtype_name IS NULL
                AND tin IS NULL
            )
            OR
        (
            is_incorporated = 0
                AND entity_subtype IS NOT NULL
                AND (entity_subtype <> 'OTHER' OR entity_subtype_name IS NOT NULL)
                AND tin IS NOT NULL
            )
        ),

    FOREIGN KEY ("country") REFERENCES "code_list" ("id")
);

------------------------------------------------------------
-- FILING HISTORY
------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "filing_history"
(
    "id"           INTEGER PRIMARY KEY NOT NULL,
    "order_number" TEXT                NOT NULL,
    "filed_at"     INTEGER             NOT NULL,
    "period"       TEXT                NOT NULL
);

CREATE UNIQUE INDEX ux_filing_history_order
    ON filing_history ("order_number");

------------------------------------------------------------
-- CODE LIST
------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "code_list"
(
    "id"          INTEGER PRIMARY KEY NOT NULL,
    "list_id"     TEXT                NOT NULL CHECK ("list_id" IN (
                                                                    'COUNTRY',
                                                                    'CORPORATE_ROLE',
                                                                    'INDIRECT_INVOLVEMENT',
                                                                    'INDIRECT_RELATIONSHIP',
                                                                    'VALUE_TYPE')),
    "code"        TEXT                NOT NULL,
    "description" TEXT                NOT NULL,
    "sort_order"  INTEGER             NOT NULL CHECK ("sort_order" >= 0),
    "active"      INTEGER             NOT NULL CHECK ("active" IN (0, 1))
);

CREATE UNIQUE INDEX ux_code_list
    ON code_list ("list_id", "code");

------------------------------------------------------------
-- SPOUSE PARTY
------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "spouse_party"
(
    "id"               INTEGER PRIMARY KEY NOT NULL,
    "name"             TEXT                NOT NULL,
    "doi_number"       TEXT                NOT NULL,
    "certain_date"     INTEGER             NOT NULL,
    "certain_document" TEXT                NOT NULL
);

------------------------------------------------------------
-- PAYKUNA
------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "paykuna"
(
    "id"                      INTEGER PRIMARY KEY NOT NULL,
    "resident"                INTEGER             NOT NULL CHECK ("resident" IN (0, 1)),
    "doi_type"                TEXT                NOT NULL CHECK ("doi_type" IN ('RUC', 'DNI', 'CEX', 'PAS', 'TIN', 'ID')),
    "doi_number"              TEXT                NOT NULL,
    "name"                    TEXT                NOT NULL,

    "country_residence"       INTEGER             NOT NULL,
    "birthday"                INTEGER             NOT NULL,
    "country_nationality"     INTEGER             NOT NULL,

    "address"                 TEXT                NOT NULL,
    "corporate_role"          INTEGER             NOT NULL,

    "phone"                   TEXT                NOT NULL,
    "marital_status"          TEXT                NOT NULL CHECK ("marital_status" IN
                                                                  ('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOW',
                                                                   'CONCUBINE')),

    "notice_date"             INTEGER             NOT NULL,
    "spouse"                  INTEGER,
    "is_legal_representative" INTEGER             NOT NULL CHECK ("is_legal_representative" IN (0, 1)),

    FOREIGN KEY ("country_residence") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("country_nationality") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("corporate_role") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("spouse") REFERENCES "spouse_party" ("id")
);

CREATE UNIQUE INDEX ux_paykuna_doi
    ON paykuna ("doi_type", "doi_number");

------------------------------------------------------------
-- INVOLVEMENT  (1:1  PAYKUNA + role flags)
------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "involvement"
(
    "id"                       INTEGER PRIMARY KEY NOT NULL,

    -- Role flags
    "has_ownership"            INTEGER             NOT NULL CHECK ("has_ownership" IN (0, 1)),
    "has_control"              INTEGER             NOT NULL CHECK ("has_control" IN (0, 1)),
    "has_senior_mgmt"          INTEGER             NOT NULL CHECK ("has_senior_mgmt" IN (0, 1)),

    -- Block A
    "is_direct"                INTEGER             NOT NULL CHECK ("is_direct" IN (0, 1)),
    "indirect_type"            INTEGER,
    "indirect_relationship"    INTEGER,

    "titular_doi_type"         TEXT CHECK ("titular_doi_type" IN ('RUC', 'DNI', 'CEX', 'PAS', 'TIN', 'ID')),
    "titular_doi_number"       TEXT,

    "value_type"               INTEGER             NOT NULL,
    "value_count"              INTEGER             NOT NULL CHECK ("value_count" > 0),
    "value_nominal"            INTEGER             NOT NULL CHECK ("value_nominal" >= 0),
    "participation_percentage" INTEGER             NOT NULL CHECK ("participation_percentage" >= 0),

    "has_voting_rights"        INTEGER             NOT NULL CHECK ("has_voting_rights" IN (0, 1)),
    "deposit_country"          INTEGER             NOT NULL,

    -- Block B (only control involvement)
    "control_description"      TEXT,

    -- Rule: if invovemente hasControl, must have description
    CHECK (NOT ("has_control" = 1 AND "control_description" IS NULL)),

    FOREIGN KEY ("id") REFERENCES "paykuna" ("id"),
    FOREIGN KEY ("indirect_type") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("indirect_relationship") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("value_type") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("deposit_country") REFERENCES "code_list" ("id")
);
