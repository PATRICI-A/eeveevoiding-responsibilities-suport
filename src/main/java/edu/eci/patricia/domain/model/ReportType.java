package edu.eci.patricia.domain.model;

/**
 * Enumeration of inappropriate behavior report types available to students.
 */
public enum ReportType {

    /** Unwanted or offensive behavior of a sexual or personal nature. */
    HARASSMENT,

    /** Repeated aggressive behavior intended to intimidate or hurt. */
    BULLYING,

    /** Unfair treatment based on personal characteristics. */
    DISCRIMINATION,

    /** Physical harm or threat of physical harm. */
    VIOLENCE,

    /** Any behavior not covered by the specific categories above. */
    OTHER
}
