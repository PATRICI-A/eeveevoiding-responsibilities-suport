-- V1: Initial schema for PATRICIA Wellness & Support Service
-- Tables: wellness_resources, survey_responses, behavior_reports

CREATE TABLE IF NOT EXISTS wellness_resources (
    id               VARCHAR(255) NOT NULL,
    name             VARCHAR(255) NOT NULL,
    description      VARCHAR(1000) NOT NULL,
    category         VARCHAR(50)  NOT NULL,
    location         VARCHAR(255) NOT NULL,
    contact_info     VARCHAR(255),
    schedule         VARCHAR(255),
    available        BOOLEAN      NOT NULL DEFAULT TRUE,
    appointment_email   VARCHAR(255),
    psychologist_name   VARCHAR(255),
    CONSTRAINT pk_wellness_resources PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS survey_responses (
    id           VARCHAR(255) NOT NULL,
    student_id   VARCHAR(255) NOT NULL,
    answers      JSONB        NOT NULL,
    submitted_at TIMESTAMP,
    CONSTRAINT pk_survey_responses PRIMARY KEY (id)
);

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
