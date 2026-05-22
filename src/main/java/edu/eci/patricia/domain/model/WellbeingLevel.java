package edu.eci.patricia.domain.model;

/**
 * Enumeration representing the level of student wellbeing derived from survey scores.
 * Each level corresponds to an average score range across the five survey dimensions.
 */
public enum WellbeingLevel {

    /** Average score below 2.0 — immediate support recommended. */
    CRITICAL,

    /** Average score between 2.0 and 2.9 — significant concerns present. */
    LOW,

    /** Average score between 3.0 and 3.4 — some areas need attention. */
    MODERATE,

    /** Average score between 3.5 and 4.2 — student is generally well. */
    GOOD,

    /** Average score above 4.2 — student is thriving. */
    EXCELLENT
}
