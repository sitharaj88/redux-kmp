package `in`.sitharaj.reduxkmp.core

/**
 * Middleware provides a third-party extension point between dispatching an action
 * and the moment it reaches the reducer.
 * 
 * Middleware can:
 * - Intercept actions before they reach the reducer
 * - Dispatch additional actions
 * - Perform side effects (API calls, logging, etc.)
 * - Transform actions
 * - Stop actions from reaching the reducer
 * 
 * Example:
 * ```kotlin
 * class LoggingMiddleware<S : State> : Middleware<S> {
 *     override fun apply(store: Store<S>, action: Action, next: Dispatcher) {
 *         println("Dispatching action: $action")
 *         val prevState = store.currentState
 *         next(action)
 *         println("New state: ${store.currentState}")
 *     }
 * }
 * ```
 */
public interface Middleware<S : State> {
    /**
     * Applies the middleware logic.
     * 
     * @param store The store instance, providing access to state and dispatch
     * @param action The action being dispatched
     * @param next The next middleware in the chain, or the reducer if this is the last middleware
     */
    public fun apply(store: Store<S>, action: Action, next: Dispatcher)
}

/**
 * Creates a middleware from a lambda function.
 * 
 * Example:
 * ```kotlin
 * val loggingMiddleware = createMiddleware<AppState> { store, action, next ->
 *     println("Action: $action")
 *     next(action)
 * }
 * ```
 */
public fun <S : State> createMiddleware(
    apply: (store: Store<S>, action: Action, next: Dispatcher) -> Unit
): Middleware<S> {
    return object : Middleware<S> {
        override fun apply(store: Store<S>, action: Action, next: Dispatcher) {
            apply(store, action, next)
        }
    }
}
