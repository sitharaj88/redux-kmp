package `in`.sitharaj.reduxkmp.core

import kotlinx.coroutines.CoroutineScope

/**
 * DSL builder for creating a Redux Store with a fluent, type-safe API.
 * 
 * Example:
 * ```kotlin
 * val store = store<AppState> {
 *     initialState { AppState() }
 *     
 *     reducer {
 *         on<CounterAction.Increment> { state, _ ->
 *             state.copy(count = state.count + 1)
 *         }
 *         on<CounterAction.Decrement> { state, _ ->
 *             state.copy(count = state.count - 1)
 *         }
 *     }
 *     
 *     middlewares {
 *         +ThunkMiddleware()
 *         +LoggingMiddleware()
 *     }
 *     
 *     scope(CoroutineScope(Dispatchers.Main))
 * }
 * ```
 */
@DslMarker
public annotation class StoreDsl

/**
 * Creates a Store using a DSL builder.
 */
public fun <S : State> store(
    block: StoreDslBuilder<S>.() -> Unit
): Store<S> {
    val builder = StoreDslBuilder<S>()
    builder.block()
    return builder.build()
}

/**
 * DSL builder class for Store configuration.
 */
@StoreDsl
public class StoreDslBuilder<S : State> {
    private var initialState: S? = null
    private var reducerFn: Reducer<S>? = null
    private val middlewares = mutableListOf<Middleware<S>>()
    private var scope: CoroutineScope? = null
    
    /**
     * Sets the initial state of the store.
     */
    public fun initialState(provider: () -> S) {
        initialState = provider()
    }
    
    /**
     * Sets the initial state of the store directly.
     */
    public fun initialState(state: S) {
        initialState = state
    }
    
    /**
     * Configures the reducer using the reducer DSL.
     */
    public fun reducer(block: ReducerBuilder<S>.() -> Unit) {
        reducerFn = `in`.sitharaj.reduxkmp.core.reducer(block)
    }
    
    /**
     * Sets the reducer directly.
     */
    public fun reducer(reducer: Reducer<S>) {
        this.reducerFn = reducer
    }
    
    /**
     * Configures middlewares.
     */
    public fun middlewares(block: MiddlewareListBuilder<S>.() -> Unit) {
        val middlewareBuilder = MiddlewareListBuilder<S>()
        middlewareBuilder.block()
        middlewares.addAll(middlewareBuilder.middlewares)
    }
    
    /**
     * Adds a single middleware.
     */
    public fun middleware(middleware: Middleware<S>) {
        middlewares.add(middleware)
    }
    
    /**
     * Sets the CoroutineScope for async operations.
     */
    public fun scope(scope: CoroutineScope) {
        this.scope = scope
    }
    
    /**
     * Builds the Store instance.
     */
    public fun build(): Store<S> {
        return Store(
            initialState = requireNotNull(initialState) { 
                "Initial state must be provided. Use initialState { YourState() } or initialState(YourState())" 
            },
            reducer = requireNotNull(reducerFn) { 
                "Reducer must be provided. Use reducer { ... } or reducer(yourReducer)" 
            },
            middlewares = middlewares,
            scope = requireNotNull(scope) { 
                "CoroutineScope must be provided. Use scope(yourCoroutineScope)" 
            }
        )
    }
}

/**
 * Builder for adding middlewares using DSL.
 */
@StoreDsl
public class MiddlewareListBuilder<S : State> {
    internal val middlewares = mutableListOf<Middleware<S>>()
    
    /**
     * Adds a middleware using the unary plus operator.
     */
    public operator fun Middleware<S>.unaryPlus() {
        middlewares.add(this)
    }
    
    /**
     * Adds a middleware using the add function.
     */
    public fun add(middleware: Middleware<S>) {
        middlewares.add(middleware)
    }
}
