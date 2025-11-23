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
    "id"                 INTEGER PRIMARY KEY NOT NULL,
    "list_id"            TEXT                NOT NULL CHECK ("list_id" IN (
                                                                           'COUNTRY',
                                                                           'CORPORATE_ROLE',
                                                                           'INDIRECT_RELATIONSHIP',
                                                                           'VALUE_TYPE')),
    "code"               TEXT                NOT NULL,
    "description"        TEXT                NOT NULL,
    "sort_order"         INTEGER             NOT NULL CHECK ("sort_order" >= 0),
    "tax_alternate_code" TEXT,
    "active"             INTEGER             NOT NULL CHECK ("active" IN (0, 1))
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
    "indirect_type"            TEXT CHECK ("indirect_type" IN ('FAMILY', 'ENTITY_CHAIN', 'MANDATE')),
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

    -- Rule: if involvemente hasControl, must have description
    CHECK (NOT ("has_control" = 1 AND "control_description" IS NULL)),
    CHECK (
        -- Direct involvement: both fields must be NULL
        ("is_direct" = 1 AND indirect_type IS NULL AND indirect_relationship IS NULL)
            OR
            -- Indirect involvement: both fields must be NOT NULL
        ("is_direct" = 0 AND indirect_type IS NOT NULL AND indirect_relationship IS NOT NULL)
        ),

    FOREIGN KEY ("id") REFERENCES "paykuna" ("id"),
    FOREIGN KEY ("indirect_relationship") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("value_type") REFERENCES "code_list" ("id"),
    FOREIGN KEY ("deposit_country") REFERENCES "code_list" ("id")
);

-- Catalog SEEDS
-- Countries
INSERT INTO "code_list"("list_id", "code", "description", "sort_order", "active")
VALUES ('COUNTRY', 'AF', 'AFGHANISTAN', 1, 1),
       ('COUNTRY', 'AX', 'ÅLAND ISLANDS', 2, 1),
       ('COUNTRY', 'AL', 'ALBANIA', 3, 1),
       ('COUNTRY', 'DZ', 'ALGERIA', 4, 1),
       ('COUNTRY', 'AS', 'AMERICAN SAMOA', 5, 1),
       ('COUNTRY', 'AD', 'ANDORRA', 6, 1),
       ('COUNTRY', 'AO', 'ANGOLA', 7, 1),
       ('COUNTRY', 'AI', 'ANGUILLA', 8, 1),
       ('COUNTRY', 'AQ', 'ANTARCTICA', 9, 1),
       ('COUNTRY', 'AG', 'ANTIGUA AND BARBUDA', 10, 1),
       ('COUNTRY', 'AR', 'ARGENTINA', 11, 1),
       ('COUNTRY', 'AM', 'ARMENIA', 12, 1),
       ('COUNTRY', 'AW', 'ARUBA', 13, 1),
       ('COUNTRY', 'AT', 'AUSTRIA', 14, 1),
       ('COUNTRY', 'AU', 'AUSTRALIA', 15, 1),
       ('COUNTRY', 'AZ', 'AZERBAIJAN', 16, 1),
       ('COUNTRY', 'BS', 'BAHAMAS', 17, 1),
       ('COUNTRY', 'BH', 'BAHRAIN', 18, 1),
       ('COUNTRY', 'BD', 'BANGLADESH', 19, 1),
       ('COUNTRY', 'BB', 'BARBADOS', 20, 1),
       ('COUNTRY', 'BY', 'BELARUS', 21, 1),
       ('COUNTRY', 'BE', 'BELGIUM', 22, 1),
       ('COUNTRY', 'BZ', 'BELIZE', 23, 1),
       ('COUNTRY', 'BJ', 'BENIN', 24, 1),
       ('COUNTRY', 'BM', 'BERMUDA', 25, 1),
       ('COUNTRY', 'BT', 'BHUTAN', 26, 1),
       ('COUNTRY', 'BO', 'BOLIVIA', 27, 1),
       ('COUNTRY', 'BA', 'BOSNIA AND HERZEGOVINA', 28, 1),
       ('COUNTRY', 'BW', 'BOTSWANA', 29, 1),
       ('COUNTRY', 'BV', 'BOUVET ISLAND', 30, 1),
       ('COUNTRY', 'BR', 'BRAZIL', 31, 1),
       ('COUNTRY', 'IO', 'BRITISH INDIAN OCEAN TERRITORY', 32, 1),
       ('COUNTRY', 'BN', 'BRUNEI DARUSSALAM', 33, 1),
       ('COUNTRY', 'BG', 'BULGARIA', 34, 1),
       ('COUNTRY', 'BF', 'BURKINA FASO', 35, 1),
       ('COUNTRY', 'BI', 'BURUNDI', 36, 1),
       ('COUNTRY', 'KH', 'CAMBODIA', 37, 1),
       ('COUNTRY', 'CM', 'CAMEROON', 38, 1),
       ('COUNTRY', 'CA', 'CANADA', 39, 1),
       ('COUNTRY', 'CV', 'CAPE VERDE', 40, 1),
       ('COUNTRY', 'KY', 'CAYMAN ISLANDS', 41, 1),
       ('COUNTRY', 'CF', 'CENTRAL AFRICAN REPUBLIC', 42, 1),
       ('COUNTRY', 'TD', 'CHAD', 43, 1),
       ('COUNTRY', 'CL', 'CHILE', 44, 1),
       ('COUNTRY', 'CN', 'CHINA', 45, 1),
       ('COUNTRY', 'CX', 'CHRISTMAS ISLAND', 46, 1),
       ('COUNTRY', 'CC', 'COCOS (KEELING) ISLANDS', 47, 1),
       ('COUNTRY', 'CO', 'COLOMBIA', 48, 1),
       ('COUNTRY', 'KM', 'COMOROS', 49, 1),
       ('COUNTRY', 'CG', 'CONGO', 50, 1),
       ('COUNTRY', 'CD', 'CONGO, THE DEMOCRATIC REPUBLIC OF THE', 51, 1),
       ('COUNTRY', 'CK', 'COOK ISLANDS', 52, 1),
       ('COUNTRY', 'CR', 'COSTA RICA', 53, 1),
       ('COUNTRY', 'CI', 'CÔTE D''IVOIRE', 54, 1),
       ('COUNTRY', 'HR', 'CROATIA', 55, 1),
       ('COUNTRY', 'CU', 'CUBA', 56, 1),
       ('COUNTRY', 'CY', 'CYPRUS', 57, 1),
       ('COUNTRY', 'CZ', 'CZECH REPUBLIC', 58, 1),
       ('COUNTRY', 'DK', 'DENMARK', 59, 1),
       ('COUNTRY', 'DJ', 'DJIBOUTI', 60, 1),
       ('COUNTRY', 'DM', 'DOMINICA', 61, 1),
       ('COUNTRY', 'DO', 'DOMINICAN REPUBLIC', 62, 1),
       ('COUNTRY', 'EC', 'ECUADOR', 63, 1),
       ('COUNTRY', 'EG', 'EGYPT', 64, 1),
       ('COUNTRY', 'SV', 'EL SALVADOR', 65, 1),
       ('COUNTRY', 'GQ', 'EQUATORIAL GUINEA', 66, 1),
       ('COUNTRY', 'ER', 'ERITREA', 67, 1),
       ('COUNTRY', 'EE', 'ESTONIA', 68, 1),
       ('COUNTRY', 'ET', 'ETHIOPIA', 69, 1),
       ('COUNTRY', 'FK', 'FALKLAND ISLANDS (MALVINAS)', 70, 1),
       ('COUNTRY', 'FO', 'FAROE ISLANDS', 71, 1),
       ('COUNTRY', 'FJ', 'FIJI', 72, 1),
       ('COUNTRY', 'FI', 'FINLAND', 73, 1),
       ('COUNTRY', 'FR', 'FRANCE', 74, 1),
       ('COUNTRY', 'GF', 'FRENCH GUIANA', 75, 1),
       ('COUNTRY', 'PF', 'FRENCH POLYNESIA', 76, 1),
       ('COUNTRY', 'TF', 'FRENCH SOUTHERN TERRITORIES', 77, 1),
       ('COUNTRY', 'GA', 'GABON', 78, 1),
       ('COUNTRY', 'GM', 'GAMBIA', 79, 1),
       ('COUNTRY', 'GG', 'GUERNSEY', 80, 1),
       ('COUNTRY', 'GE', 'GEORGIA', 81, 1),
       ('COUNTRY', 'DE', 'GERMANY', 82, 1),
       ('COUNTRY', 'GH', 'GHANA', 83, 1),
       ('COUNTRY', 'GI', 'GIBRALTAR', 84, 1),
       ('COUNTRY', 'GR', 'GREECE', 85, 1),
       ('COUNTRY', 'GL', 'GREENLAND', 86, 1),
       ('COUNTRY', 'GD', 'GRANADA', 87, 1),
       ('COUNTRY', 'GP', 'GUADELOUPE', 88, 1),
       ('COUNTRY', 'GU', 'GUAM', 89, 1),
       ('COUNTRY', 'GT', 'GUATEMALA', 90, 1),
       ('COUNTRY', 'GN', 'GUINEA', 91, 1),
       ('COUNTRY', 'GW', 'GUINEA-BISSAU', 92, 1),
       ('COUNTRY', 'GY', 'GUYANA', 93, 1),
       ('COUNTRY', 'HT', 'HAITI', 94, 1),
       ('COUNTRY', 'HM', 'HEARD ISLAND AND MCDONALD ISLANDS', 95, 1),
       ('COUNTRY', 'VA', 'HOLY SEE (VATICAN CITY STATE)', 96, 1),
       ('COUNTRY', 'HN', 'HONDURAS', 97, 1),
       ('COUNTRY', 'HK', 'HONG KONG', 98, 1),
       ('COUNTRY', 'HU', 'HUNGARY', 99, 1),
       ('COUNTRY', 'IS', 'ICELAND', 100, 1),
       ('COUNTRY', 'IN', 'INDIA', 101, 1),
       ('COUNTRY', 'ID', 'INDONESIA', 102, 1),
       ('COUNTRY', 'IM', 'ISLE OF MAN', 103, 1),
       ('COUNTRY', 'IR', 'IRAN, ISLAMIC REPUBLIC OF', 104, 1),
       ('COUNTRY', 'IQ', 'IRAQ', 105, 1),
       ('COUNTRY', 'IE', 'IRELAND', 106, 1),
       ('COUNTRY', 'IL', 'ISRAEL', 107, 1),
       ('COUNTRY', 'IT', 'ITALY', 108, 1),
       ('COUNTRY', 'JM', 'JAMAICA', 109, 1),
       ('COUNTRY', 'JP', 'JAPAN', 110, 1),
       ('COUNTRY', 'JE', 'JERSEY', 111, 1),
       ('COUNTRY', 'JO', 'JORDAN', 112, 1),
       ('COUNTRY', 'KZ', 'KAZAKHSTAN', 113, 1),
       ('COUNTRY', 'KE', 'KENYA', 114, 1),
       ('COUNTRY', 'KI', 'KIRIBATI', 115, 1),
       ('COUNTRY', 'KP', 'KOREA, DEMOCRATIC PEOPLE''S REPUBLIC OF', 116, 1),
       ('COUNTRY', 'KR', 'KOREA, REPUBLIC OF', 117, 1),
       ('COUNTRY', 'KW', 'KUWAIT', 118, 1),
       ('COUNTRY', 'KG', 'KYRGYZSTAN', 119, 1),
       ('COUNTRY', 'LA', 'LAO PEOPLE''S DEMOCRATIC REPUBLIC', 120, 1),
       ('COUNTRY', 'LV', 'LATVIA', 121, 1),
       ('COUNTRY', 'LB', 'LEBANON', 122, 1),
       ('COUNTRY', 'LS', 'LESOTHO', 123, 1),
       ('COUNTRY', 'LR', 'LIBERIA', 124, 1),
       ('COUNTRY', 'LY', 'LIBYAN ARAB JAMAHIRIYA', 125, 1),
       ('COUNTRY', 'LI', 'LIECHTENSTEIN', 126, 1),
       ('COUNTRY', 'LT', 'LITHUANIA', 127, 1),
       ('COUNTRY', 'LU', 'LUXEMBOURG', 128, 1),
       ('COUNTRY', 'MO', 'MACAO', 129, 1),
       ('COUNTRY', 'MK', 'NORTH MACEDONIA', 130, 1),
       ('COUNTRY', 'MG', 'MADAGASCAR', 131, 1),
       ('COUNTRY', 'MW', 'MALAWI', 132, 1),
       ('COUNTRY', 'MY', 'MALAYSIA', 133, 1),
       ('COUNTRY', 'MV', 'MALDIVES', 134, 1),
       ('COUNTRY', 'ML', 'MALI', 135, 1),
       ('COUNTRY', 'MT', 'MALTA', 136, 1),
       ('COUNTRY', 'MH', 'MARSHALL ISLANDS', 137, 1),
       ('COUNTRY', 'MQ', 'MARTINIQUE', 138, 1),
       ('COUNTRY', 'MR', 'MAURITANIA', 139, 1),
       ('COUNTRY', 'MU', 'MAURITIUS', 140, 1),
       ('COUNTRY', 'YT', 'MAYOTTE', 141, 1),
       ('COUNTRY', 'MX', 'MEXICO', 142, 1),
       ('COUNTRY', 'FM', 'MICRONESIA, FEDERATED STATES OF', 143, 1),
       ('COUNTRY', 'MD', 'MOLDOVA, REPUBLIC OF', 144, 1),
       ('COUNTRY', 'MC', 'MONACO', 145, 1),
       ('COUNTRY', 'MN', 'MONGOLIA', 146, 1),
       ('COUNTRY', 'MS', 'MONTSERRAT', 147, 1),
       ('COUNTRY', 'MA', 'MOROCCO', 148, 1),
       ('COUNTRY', 'MZ', 'MOZAMBIQUE', 149, 1),
       ('COUNTRY', 'MM', 'MYANMAR', 150, 1),
       ('COUNTRY', 'NA', 'NAMIBIA', 151, 1),
       ('COUNTRY', 'NR', 'NAURU', 152, 1),
       ('COUNTRY', 'NP', 'NEPAL', 153, 1),
       ('COUNTRY', 'NL', 'NETHERLANDS', 154, 1),
       --('COUNTRY', 'AN', 'NETHERLANDS ANTILLES', 155, 1),
       ('COUNTRY', 'NC', 'NEW CALEDONIA', 158, 1),
       ('COUNTRY', 'NZ', 'NEW ZEALAND', 159, 1),
       ('COUNTRY', 'NI', 'NICARAGUA', 160, 1),
       ('COUNTRY', 'NE', 'NIGER', 161, 1),
       ('COUNTRY', 'NG', 'NIGERIA', 162, 1),
       ('COUNTRY', 'NU', 'NIUE', 163, 1),
       ('COUNTRY', 'NF', 'NORFOLK ISLAND', 164, 1),
       ('COUNTRY', 'MP', 'NORTHERN MARIANA ISLANDS', 165, 1),
       ('COUNTRY', 'NO', 'NORWAY', 166, 1),
       ('COUNTRY', 'OM', 'OMAN', 167, 1),
       ('COUNTRY', 'PK', 'PAKISTAN', 168, 1),
       ('COUNTRY', 'PW', 'PALAU', 169, 1),
       ('COUNTRY', 'PS', 'PALESTINIAN TERRITORY, OCCUPIED', 170, 1),
       ('COUNTRY', 'PA', 'PANAMA', 171, 1),
       ('COUNTRY', 'PG', 'PAPUA NEW GUINEA', 172, 1),
       ('COUNTRY', 'PY', 'PARAGUAY', 173, 1),
       ('COUNTRY', 'PE', 'PERÚ', 174, 1),
       ('COUNTRY', 'PH', 'PHILIPPINES', 175, 1),
       ('COUNTRY', 'PN', 'PITCAIRN', 176, 1),
       ('COUNTRY', 'PL', 'POLAND', 177, 1),
       ('COUNTRY', 'PT', 'PORTUGAL', 178, 1),
       ('COUNTRY', 'PR', 'PUERTO RICO', 179, 1),
       ('COUNTRY', 'QA', 'QATAR', 180, 1),
       ('COUNTRY', 'RE', 'RÉUNION', 181, 1),
       ('COUNTRY', 'RO', 'ROMANIA', 182, 1),
       ('COUNTRY', 'RU', 'RUSSIAN FEDERATION', 183, 1),
       ('COUNTRY', 'RW', 'RWANDA', 184, 1),
       ('COUNTRY', 'SH', 'SAINT HELENA', 185, 1),
       ('COUNTRY', 'KN', 'SAINT KITTS AND NEVIS', 186, 1),
       ('COUNTRY', 'LC', 'SAINT LUCIA', 187, 1),
       ('COUNTRY', 'PM', 'SAINT PIERRE AND MIQUELON', 188, 1),
       ('COUNTRY', 'VC', 'SAINT VINCENT AND THE GRENADINES', 189, 1),
       ('COUNTRY', 'WS', 'SAMOA', 190, 1),
       ('COUNTRY', 'SM', 'SAN MARINO', 191, 1),
       ('COUNTRY', 'ST', 'SAO TOME AND PRINCIPE', 192, 1),
       ('COUNTRY', 'SA', 'SAUDI ARABIA', 193, 1),
       ('COUNTRY', 'SN', 'SENEGAL', 194, 1),
       --('COUNTRY', 'CS', 'SERBIA AND MONTENEGRO', 195, 0),
       ('COUNTRY', 'SC', 'SEYCHELLES', 197, 1),
       ('COUNTRY', 'SL', 'SIERRA LEONE', 198, 1),
       ('COUNTRY', 'SG', 'SINGAPORE', 199, 1),
       ('COUNTRY', 'SK', 'SLOVAKIA', 200, 1),
       ('COUNTRY', 'SI', 'SLOVENIA', 201, 1),
       ('COUNTRY', 'SB', 'SOLOMON ISLANDS', 202, 1),
       ('COUNTRY', 'SO', 'SOMALIA', 203, 1),
       ('COUNTRY', 'ZA', 'SOUTH AFRICA', 204, 1),
       ('COUNTRY', 'GS', 'SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS', 205, 1),
       ('COUNTRY', 'ES', 'SPAIN', 206, 1),
       ('COUNTRY', 'LK', 'SRI LANKA', 207, 1),
       ('COUNTRY', 'SD', 'SUDAN', 208, 1),
       ('COUNTRY', 'SR', 'SURINAME', 209, 1),
       ('COUNTRY', 'SJ', 'SVALBARD AND JAN MAYEN', 210, 1),
       ('COUNTRY', 'SZ', 'ESWATINI', 211, 1),
       ('COUNTRY', 'SE', 'SWEDEN', 212, 1),
       ('COUNTRY', 'CH', 'SWITZERLAND', 213, 1),
       ('COUNTRY', 'SY', 'SYRIAN ARAB REPUBLIC', 214, 1),
       ('COUNTRY', 'TW', 'TAIWAN, PROVINCE OF CHINA', 215, 1),
       ('COUNTRY', 'TJ', 'TAJIKISTAN', 216, 1),
       ('COUNTRY', 'TZ', 'TANZANIA, UNITED REPUBLIC OF', 217, 1),
       ('COUNTRY', 'TH', 'THAILAND', 218, 1),
       ('COUNTRY', 'TL', 'TIMOR-LESTE', 219, 1),
       ('COUNTRY', 'TG', 'TOGO', 220, 1),
       ('COUNTRY', 'TK', 'TOKELAU', 221, 1),
       ('COUNTRY', 'TO', 'TONGA', 222, 1),
       ('COUNTRY', 'TT', 'TRINIDAD AND TOBAGO', 223, 1),
       ('COUNTRY', 'TN', 'TUNISIA', 224, 1),
       ('COUNTRY', 'TR', 'TURKEY', 225, 1),
       ('COUNTRY', 'TM', 'TURKMENISTAN', 226, 1),
       ('COUNTRY', 'TC', 'TURKS AND CAICOS ISLANDS', 227, 1),
       ('COUNTRY', 'TV', 'TUVALU', 228, 1),
       ('COUNTRY', 'UG', 'UGANDA', 229, 1),
       ('COUNTRY', 'UA', 'UKRAINE', 230, 1),
       ('COUNTRY', 'AE', 'UNITED ARAB EMIRATES', 231, 1),
       ('COUNTRY', 'GB', 'UNITED KINGDOM', 232, 1),
       ('COUNTRY', 'US', 'UNITED STATES', 233, 1),
       ('COUNTRY', 'UM', 'UNITED STATES MINOR OUTLYING ISLANDS', 234, 1),
       ('COUNTRY', 'UY', 'URUGUAY', 235, 1),
       ('COUNTRY', 'UZ', 'UZBEKISTAN', 236, 1),
       ('COUNTRY', 'VU', 'VANUATU', 237, 1),
       ('COUNTRY', 'VE', 'VENEZUELA', 238, 1),
       ('COUNTRY', 'VN', 'VIET NAM', 239, 1),
       ('COUNTRY', 'VG', 'VIRGIN ISLANDS, BRITISH', 240, 1),
       ('COUNTRY', 'VI', 'VIRGIN ISLANDS, U.S.', 241, 1),
       ('COUNTRY', 'WF', 'WALLIS AND FUTUNA', 242, 1),
       ('COUNTRY', 'EH', 'WESTERN SAHARA', 243, 1),
       ('COUNTRY', 'YE', 'YEMEN', 244, 1),
       ('COUNTRY', 'ZM', 'ZAMBIA', 245, 1),
       ('COUNTRY', 'ZW', 'ZIMBABWE', 246, 1),
       ('COUNTRY', 'OT', 'OTROS TERRITORIOS DE BAJA O NULA IMPOSICION', 247, 1);

INSERT INTO "code_list"("list_id", "code", "tax_alternate_code", "description", "sort_order", "active")
VALUES ('COUNTRY', 'RS', 'CS', 'SERBIA', 195, 1),
       ('COUNTRY', 'ME', 'CS', 'MONTENEGRO', 196, 1),
       ('COUNTRY', 'CW', 'AN', 'CURAÇAO', 155, 1),
       ('COUNTRY', 'SX', 'AN', 'SINT MAARTEN', 156, 1),
       ('COUNTRY', 'BQ', 'AN', 'BONAIRE / SINT EUSTATIUS / SABA (CARIBBEAN NETHERLANDS)', 157, 1),
       ('COUNTRY', 'XK', 'OT', 'KOSOVO', 248, 1),
       ('COUNTRY', 'BL', 'OT', 'SAINT BARTHÉLEMY', 249, 1),
       ('COUNTRY', 'MF', 'OT', 'SAINT MARTIN (FRENCH PART)', 250, 1);

-- CORPORATE ROLE
INSERT INTO "code_list"("list_id", "description", "code", "sort_order", "active")
VALUES ('CORPORATE_ROLE', 'DIRECTOR', '01', 1, 1),
       ('CORPORATE_ROLE', 'DIRECTOR EJECUTIVO', '02', 2, 1),
       ('CORPORATE_ROLE', 'DIRECTOR GENERAL', '03', 3, 1),
       ('CORPORATE_ROLE', 'TERCERO NO VINCULADO', '04', 4, 1),
       ('CORPORATE_ROLE', 'GERENTE GENERAL', '05', 5, 1),
       ('CORPORATE_ROLE', 'GERENTE', '06', 6, 1),
       ('CORPORATE_ROLE', 'SUB GERENTE', '07', 7, 1),
       ('CORPORATE_ROLE', 'GERENTE FINANCIERO', '08', 8, 1),
       ('CORPORATE_ROLE', 'GERENTE DE OPERACIONES', '09', 9, 1),
       ('CORPORATE_ROLE', 'GERENTE TITULAR', '10', 10, 1),
       ('CORPORATE_ROLE', 'GERENTE EJECUTIVO', '11', 11, 1),
       ('CORPORATE_ROLE', 'GERENTE DE VENTAS', '12', 12, 1),
       ('CORPORATE_ROLE', 'GERENTE COMERCIAL', '13', 13, 1),
       ('CORPORATE_ROLE', 'GERENTE ADJUNTO', '14', 14, 1),
       ('CORPORATE_ROLE', 'GERENTE DE COMERCIALIZACIÓN', '15', 15, 1),
       ('CORPORATE_ROLE', 'GERENTE TÉCNICO', '16', 16, 1),
       ('CORPORATE_ROLE', 'GERENTE DE FINANZAS', '17', 17, 1),
       ('CORPORATE_ROLE', 'GERENTE ADMINISTRATIVO', '18', 18, 1),
       ('CORPORATE_ROLE', 'ASESOR', '19', 19, 1),
       ('CORPORATE_ROLE', 'GESTOR', '20', 20, 1),
       ('CORPORATE_ROLE', 'PRESIDENTE', '21', 21, 1),
       ('CORPORATE_ROLE', 'PRESIDENTE DIRECTORIO', '22', 22, 1),
       ('CORPORATE_ROLE', 'PRESIDENTE EJECUTIVO', '23', 23, 1),
       ('CORPORATE_ROLE', 'APODERADO', '24', 24, 1),
       ('CORPORATE_ROLE', 'SOCIO', '25', 25, 1),
       ('CORPORATE_ROLE', 'LIQUIDADOR', '26', 26, 1),
       ('CORPORATE_ROLE', 'SECRETARIO', '27', 27, 1),
       ('CORPORATE_ROLE', 'VICEPRESIDENTE', '28', 28, 1),
       ('CORPORATE_ROLE', 'TESORERO', '29', 29, 1),
       ('CORPORATE_ROLE', 'ACCIONISTA', '30', 30, 1),
       ('CORPORATE_ROLE', 'CONTADOR', '31', 31, 1),
       ('CORPORATE_ROLE', 'ABOGADO', '32', 32, 1),
       ('CORPORATE_ROLE', 'PROPIETARIO', '33', 33, 1),
       ('CORPORATE_ROLE', 'VOCAL', '34', 34, 1),
       ('CORPORATE_ROLE', 'HEREDERO', '35', 35, 1),
       ('CORPORATE_ROLE', 'SOCIO ADMINISTRADOR', '36', 36, 1),
       ('CORPORATE_ROLE', 'ALBACEA', '37', 37, 1),
       ('CORPORATE_ROLE', 'DECANO', '38', 38, 1),
       ('CORPORATE_ROLE', 'TUTOR', '39', 39, 1),
       ('CORPORATE_ROLE', 'ADMINISTRADOR', '40', 40, 1),
       ('CORPORATE_ROLE', 'TITULAR', '41', 41, 1),
       ('CORPORATE_ROLE', 'ALCALDE', '42', 42, 1),
       ('CORPORATE_ROLE', 'PROMOTOR', '43', 43, 1),
       ('CORPORATE_ROLE', 'RECTOR', '44', 44, 1),
       ('CORPORATE_ROLE', 'VICERRECTOR', '45', 45, 1),
       ('CORPORATE_ROLE', 'OTROS', '99', 100, 1);

-- INDIRECT_RELATIONSHIP
INSERT INTO "code_list"("list_id", "code", "description", "sort_order", "active")
VALUES ('INDIRECT_RELATIONSHIP', '01', 'PADRE', 1, 1),
       ('INDIRECT_RELATIONSHIP', '02', 'MADRE', 2, 1),
       ('INDIRECT_RELATIONSHIP', '03', 'HERMANO(A)', 3, 1),
       ('INDIRECT_RELATIONSHIP', '04', 'ABUELO(A)', 4, 1),
       ('INDIRECT_RELATIONSHIP', '05', 'NIETO(A)', 5, 1),
       ('INDIRECT_RELATIONSHIP', '06', 'PADRES DEL CÓNYUGE O DE LA PAREJA DE UNIÓN DE HECHO', 6, 1),
       ('INDIRECT_RELATIONSHIP', '07', 'HIJOS DEL CÓNYUGE O DE LA PAREJA DE UNIÓN DE HECHO', 7, 1),
       ('INDIRECT_RELATIONSHIP', '08', 'CÓNYUGE - PAREJA DE UNIÓN DE HECHO', 8, 1),
       ('INDIRECT_RELATIONSHIP', '09', 'TITULAR', 9, 1),
       ('INDIRECT_RELATIONSHIP', '10', 'MANDATARIO', 10, 1);

-- VALUE_TYPE
INSERT INTO "code_list"("list_id", "code", "description", "sort_order", "active")
VALUES ('VALUE_TYPE', '01', 'ACCIONES', 1, 1),
       ('VALUE_TYPE', '02', 'PARTICIPACIONES', 2, 1),
       ('VALUE_TYPE', '03', 'DERECHOS', 3, 1);