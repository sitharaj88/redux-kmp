package `in`.sitharaj.reduxkmp.middleware

import `in`.sitharaj.reduxkmp.core.Action
import `in`.sitharaj.reduxkmp.core.Dispatcher
import `in`.sitharaj.reduxkmp.core.Middleware
import `in`.sitharaj.reduxkmp.core.State
import `in`.sitharaj.reduxkmp.core.Store

/**
 * Middleware that logs all dispatched actions and state changes.
 * Useful for debugging and development.
 * 
 * Logs:
 * - The action being dispatched
 * - The previous state
 * - The new state after the reducer
 * - Time taken to process the action
 * 
 * Example:
 * ```kotlin
 * val store = createStore(
 *     initialState = AppState(),
 *     reducer = appReducer,
 *     scope = coroutineScope
 * ) {
 *     addMiddleware(LoggingMiddleware(tag = "AppStore"))
 * }
 * ```
 */
public class LoggingMiddleware<S : State>(
    private val tag: String = "Redux",
    private val logger: Logger = DefaultLogger()
) : Middleware<S> {
    
    override fun apply(store: Store<S>, action: Action, next: Dispatcher) {
        val prevState = store.currentState
        val startTime = currentTimeMillis()
        
        logger.log(tag, "┌─ Action: ${action::class.simpleName}")
        logger.log(tag, "│  ${action}")
        logger.log(tag, "│")
        logger.log(tag, "│  Previous State:")
        logger.log(tag, "│  ${prevState}")
        
        // Pass action to next middleware/reducer
        next(action)
        
        val newState = store.currentState
        val duration = currentTimeMillis() - startTime
        
        logger.log(tag, "│")
        logger.log(tag, "│  New State:")
        logger.log(tag, "│  ${newState}")
        logger.log(tag, "│")
        logger.log(tag, "│  Duration: ${duration}ms")
        logger.log(tag, "└─────────────────────────────────")
        logger.log(tag, "")
    }
}

/**
 * Interface for custom loggers.
 * Implement this to integrate with your preferred logging framework.
 */
public interface Logger {
    public fun log(tag: String, message: String)
}

/**
 * Default logger that prints to console.
 */
public class DefaultLogger : Logger {
    override fun log(tag: String, message: String) {
        println("[$tag] $message")
    }
}

/**
 * Gets the current time in milliseconds.
 * This is a cross-platform function.
 */
internal expect fun currentTimeMillis(): Long
