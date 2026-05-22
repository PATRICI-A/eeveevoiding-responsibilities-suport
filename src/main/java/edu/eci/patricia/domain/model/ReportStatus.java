package edu.eci.patricia.domain.model;

/**
 * Enumeration representing the lifecycle status of a behavior report.
 */
public enum ReportStatus {

    /** Report has been received and is awaiting review. */
    PENDING,

    /** Report is currently being investigated. */
    UNDER_REVIEW,

    /** Report investigation is complete and action has been taken. */
    RESOLVED,

    /** Report was evaluated and found to require no further action. */
    DISMISSED
}
