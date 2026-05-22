package edu.eci.patricia.domain.model;

/**
 * Wellness resource categories (PTR23).
 * Aligns with the requirements document values.
 */
public enum WellnessCategory {

    /** All categories (used for filtering). */
    ALL,

    /** Psychological counselling and emotional-support services. */
    EMOTIONAL_SUPPORT,

    /** Medical, nutritional and physical health services. */
    HEALTH,

    /** Sports and physical-activity services. */
    SPORTS,

    /** Cultural, artistic and social engagement services. */
    CULTURE,

    /** Personalized recommendations based on student survey. */
    RECOMMENDATIONS
}
