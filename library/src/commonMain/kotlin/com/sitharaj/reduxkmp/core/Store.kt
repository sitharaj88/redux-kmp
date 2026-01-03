package com.sitharaj.reduxkmp.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Type alias for the dispatch function that sends actions to the store.
 */
public typealias Dispatcher = (Action) -> Unit

/**
 * Type alias for getting the current state from the store.
 */
public typealias GetState<S> = () -> S

/**
 * The Store holds the complete state tree of your application.
 * The only way to change the state inside it is to dispatch an action on it.
 * 
 * A Store is created with:
 * - An initial state
 * - A reducer function
 * - Optional middleware for handling side effects
 * - A CoroutineScope for async operations
 * 
 * @param S The type of state held by this store
 * 
 * Example:
 * ```kotlin
 * val store = Store(
 *     initialState = CounterState(),
 *     reducer = counterReducer,
 *     middlewares = listOf(thunkMiddleware(), loggingMiddleware()),
 *     scope = CoroutineScope(Dispatchers.Main)
 * )
 * 
 * // Observe state changes
 * store.state.collect { state ->
 *     println("Count: ${state.count}")
 * }
 * 
 * // Dispatch actions
 * store.dispatch(CounterAction.Increment)
 * ```
 */
public class Store<S : State>(
    initialState: S,
    reducer: Reducer<S>,
    private val middlewares: List<Middleware<S>> = emptyList(),
    internal val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(initialState)
    
    // Mutable property to allow hot-swapping reducers
    private var currentReducer: Reducer<S> = reducer
    
    /**
     * StateFlow that emits the current state and all subsequent state changes.
     * This can be collected in UI components to react to state changes.
     */
    public val state: StateFlow<S> = _state.asStateFlow()
    
    /**
     * Gets the current state synchronously.
     */
    public val currentState: S
        get() = _state.value
    
    private val middlewareChain: Dispatcher
    
    init {
        // Build the middleware chain by folding right
        // This creates a chain where each middleware can call the next
        middlewareChain = middlewares.foldRight(
            initial = { action: Action -> reduce(action) }
        ) { middleware, next ->
            { action: Action -> middleware.apply(this, action, next) }
        }
    }
    
    /**
     * Dispatches an action to the store.
     * The action will pass through the middleware chain before reaching the reducer.
     * 
     * @param action The action to dispatch
     */
    public fun dispatch(action: Action) {
        middlewareChain(action)
    }
    
    /**
     * Replaces the current reducer with a new one.
     * This is useful for code splitting, dynamic module loading, or hot reloading.
     * 
     * @param newReducer The new reducer to use
     */
    public fun replaceReducer(newReducer: Reducer<S>) {
        currentReducer = newReducer
    }
    
    /**
     * Internal method that applies the reducer to update the state.
     * This is called by the middleware chain after all middleware has processed the action.
     */
    private fun reduce(action: Action) {
        val currentState = _state.value
        val newState = currentReducer(currentState, action)
        if (newState !== currentState) {
            _state.value = newState
        }
    }
    
    /**
     * Gets the current state. Used by middleware to access state.
     */
    public fun getState(): S = currentState
}

/**
 * Builder function to create a Store with a more fluent API.
 * 
 * Example:
 * ```kotlin
 * val store = createStore(
 *     initialState = CounterState(),
 *     reducer = counterReducer,
 *     scope = CoroutineScope(Dispatchers.Main)
 * ) {
 *     addMiddleware(thunkMiddleware())
 *     addMiddleware(loggingMiddleware())
 * }
 * ```
 */
public fun <S : State> createStore(
    initialState: S,
    reducer: Reducer<S>,
    scope: CoroutineScope,
    configure: StoreBuilder<S>.() -> Unit = {}
): Store<S> {
    val builder = StoreBuilder<S>()
    builder.configure()
    return Store(
        initialState = initialState,
        reducer = reducer,
        middlewares = builder.middlewares,
        scope = scope
    )
}

/**
 * Builder class for configuring a Store.
 */
public class StoreBuilder<S : State> {
    internal val middlewares = mutableListOf<Middleware<S>>()
    
    /**
     * Adds a middleware to the store.
     */
    public fun addMiddleware(middleware: Middleware<S>) {
        middlewares.add(middleware)
    }
    
    /**
     * Adds multiple middlewares to the store.
     */
    public fun addMiddlewares(vararg middlewares: Middleware<S>) {
        this.middlewares.addAll(middlewares)
    }
}
