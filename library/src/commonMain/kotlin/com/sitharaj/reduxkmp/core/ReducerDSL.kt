package com.sitharaj.reduxkmp.core

/**
 * Creates a reducer using a type-safe DSL.
 * 
 * This provides a cleaner, more declarative way to define reducers compared to
 * using `when` expressions, especially when handling many action types.
 * 
 * Example:
 * ```kotlin
 * val counterReducer = reducer<CounterState> {
 *     on<CounterAction.Increment> { state, _ ->
 *         state.copy(count = state.count + 1)
 *     }
 *     on<CounterAction.Add> { state, action ->
 *         state.copy(count = state.count + action.value)
 *     }
 * }
 * ```
 */
public fun <S : State> reducer(block: ReducerBuilder<S>.() -> Unit): Reducer<S> {
    val builder = ReducerBuilder<S>()
    builder.block()
    return builder.build()
}

/**
 * Builder class for the reducer DSL.
 */
public class ReducerBuilder<S : State> {
    @PublishedApi
    internal val handlers: MutableList<Handler<S>> = mutableListOf()

    /**
     * Registers a handler for a specific action type.
     * 
     * @param A The type of action to handle (must implement Action)
     * @param handler The function to calculate the new state
     */
    public inline fun <reified A : Action> on(crossinline handler: (S, A) -> S) {
        handlers.add(object : Handler<S> {
            override fun isMatching(action: Action): Boolean = action is A
            
            @Suppress("UNCHECKED_CAST")
            override fun handle(state: S, action: Action): S {
                return handler(state, action as A)
            }
        })
    }

    /**
     * Internal interface for action handlers.
     */
    public interface Handler<S> {
        public fun isMatching(action: Action): Boolean
        public fun handle(state: S, action: Action): S
    }

    /**
     * Builds the final reducer function.
     */
    public fun build(): Reducer<S> = { state, action ->
        // Find the first handler that matches the action
        val handler = handlers.firstOrNull { it.isMatching(action) }
        
        if (handler != null) {
            handler.handle(state, action)
        } else {
            state
        }
    }
}
