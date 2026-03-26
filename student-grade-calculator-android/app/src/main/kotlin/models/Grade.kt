package models

/**
 * SEALED CLASS — represents all possible grade outcomes.
 *
 * From the lecturer's notes (Slide 2):
 * "Sealed classes for restricted hierarchies"
 *
 * Why sealed instead of a plain String like "A" or "B+"?
 * - The compiler knows ALL possible grades at compile time
 * - No invalid grade like "Z" can ever exist
 * - `when` expressions on a sealed class are exhaustive — the compiler
 *   forces you to handle every case, preventing bugs
 *
 * Each subclass is a data class (carries its GPA value) or object
 * (singleton — no extra data needed beyond the type itself).
 */
sealed class Grade {
    /** The letter representation, e.g. "A", "B+". */
    abstract val letter: String
    /** GPA value on a 4.0 scale. */
    abstract val gpa: Double
    /** True for any grade that is not F. */
    val isPassing: Boolean get() = this !is F

    // ── Concrete grade subclasses ──────────────────────────────────────────
    // Each is an `object` (singleton) because there is only one "A" grade.

    object A     : Grade() { override val letter = "A";  override val gpa = 4.0 }
    object BPlus : Grade() { override val letter = "B+"; override val gpa = 3.5 }
    object B     : Grade() { override val letter = "B";  override val gpa = 3.0 }
    object CPlus : Grade() { override val letter = "C+"; override val gpa = 2.5 }
    object C     : Grade() { override val letter = "C";  override val gpa = 2.0 }
    object DPlus : Grade() { override val letter = "D+"; override val gpa = 1.5 }
    object D     : Grade() { override val letter = "D";  override val gpa = 1.0 }
    object F     : Grade() { override val letter = "F";  override val gpa = 0.0 }

    /**
     * Status string — derived from the sealed type, not a separate field.
     * POLYMORPHISM: the correct result is determined by the actual subtype.
     */
    fun status(): String = if (isPassing) "PASS" else "FAIL"

    companion object {
        /**
         * Factory: converts a numeric average to the correct [Grade] subclass.
         *
         * Uses a `when` expression — exhaustive by design.
         * Scope function note: this could also be called via `let`:
         *   average.let { Grade.from(it) }
         */
        fun from(average: Double): Grade = when {
            average >= 80.0 -> A
            average >= 70.0 -> BPlus
            average >= 60.0 -> B
            average >= 55.0 -> CPlus
            average >= 50.0 -> C
            average >= 45.0 -> DPlus
            average >= 40.0 -> D
            else            -> F
        }
    }

    override fun toString(): String = letter
}