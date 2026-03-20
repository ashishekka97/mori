package me.ashishekka.mori.persona.sensor

/**
 * Categorizes the battery impact of a [StateProvider].
 */
enum class EnergyRating {
    /** Near-zero cost. Uses passive system signals. */
    GRADE_A,
    
    /** Low cost. Active sensors used in brief bursts/snapshots. */
    GRADE_B,
    
    /** Medium/High cost. Periodic workers or high-frequency listeners. */
    GRADE_C
}
