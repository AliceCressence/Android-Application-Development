package logging

/**
 * Logger system using CLASS DELEGATION — directly from Exercise 2, Slide 15.
 *
 * The lecturer's notes say:
 * "Define an interface Logger with a function log(message: String).
 *  Provide two implementations: ConsoleLogger and FileLogger.
 *  Create a class Application that delegates logging to a Logger."
 *
 * DELEGATION (`by` keyword):
 * Instead of Application implementing log() itself, it delegates
 * all Logger calls to whichever Logger was passed in.
 * This is COMPOSITION OVER INHERITANCE — a core OOP principle.
 */

// ── Interface ──────────────────────────────────────────────────────────────
interface Logger {
    fun log(message: String)
}

// ── Implementation 1: ConsoleLogger ───────────────────────────────────────
class ConsoleLogger : Logger {
    override fun log(message: String) = println("[LOG] $message")
}

// ── Implementation 2: FileLogger ──────────────────────────────────────────
// Simulated with println as the notes suggest ("simulate with println")
class FileLogger(private val fileName: String = "app.log") : Logger {
    override fun log(message: String) = println("File($fileName): $message")
}

// ── Application using CLASS DELEGATION ────────────────────────────────────
/**
 * DELEGATION with `by`:
 * Application does NOT implement log() manually.
 * The `by logger` clause tells Kotlin to forward ALL Logger calls
 * to the [logger] instance automatically.
 *
 * Swap ConsoleLogger for FileLogger without changing Application at all —
 * this is the power of delegation.
 */
class Application(logger: Logger) : Logger by logger {
    fun start() {
        log("Application started")   // delegates to whichever logger was passed
    }
    fun stop() {
        log("Application stopped")
    }
}