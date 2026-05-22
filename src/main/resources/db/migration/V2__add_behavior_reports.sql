-- V2: Add behavior_reports table if missing
-- Some environments were baselined by Flyway without this table
CREATE TABLE IF NOT EXISTS behavior_reports (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    reporter_id  UUID         NOT NULL,
    report_type  VARCHAR(50)  NOT NULL,
    description  VARCHAR(1000) NOT NULL,
    reference_id VARCHAR(255),
    status       VARCHAR(50)  NOT NULL,
    case_number  VARCHAR(50)  UNIQUE,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    CONSTRAINT pk_behavior_reports PRIMARY KEY (id)
);
