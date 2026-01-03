package `in`.sitharaj.reduxkmp.core

/**
 * A reducer is a pure function that takes the previous state and an action,
 * and returns the next state.
 * 
 * Reducers must be pure functions with no side effects:
 * - Same inputs always produce the same output
 * - No API calls, no date/time operations, no random values
 * - Don't modify the state parameter - create a new one
 * 
 * @param S The type of state this reducer operates on
 * 
 * Example:
 * ```kotlin
 * val counterReducer: Reducer<CounterState> = { state, action ->
 *     when (action) {
 *         is CounterAction.Increment -> state.copy(count = state.count + 1)
 *         is CounterAction.Decrement -> state.copy(count = state.count - 1)
 *         is CounterAction.Add -> state.copy(count = state.count + action.value)
 *         else -> state
 *     }
 * }
 * ```
 */
public typealias Reducer<S> = (state: S, action: Action) -> S

/**
 * Combines multiple reducers into a single reducer function.
 * Each reducer is called in sequence with the result of the previous reducer.
 * 
 * This is useful when you want to compose multiple reducers that operate
 * on the same state type but handle different actions.
 * 
 * @param reducers The reducers to combine
 * @return A single reducer that calls all the provided reducers in sequence
 * 
 * Example:
 * ```kotlin
 * val combinedReducer = combineReducers(
 *     counterReducer,
 *     userReducer,
 *     settingsReducer
 * )
 * ```
 */
public fun <S : State> combineReducers(vararg reducers: Reducer<S>): Reducer<S> {
    return { state, action ->
        reducers.fold(state) { currentState, reducer ->
            reducer(currentState, action)
        }
    }
}

/**
 * Creates a reducer that only handles specific action types.
 * If the action doesn't match the predicate, the state is returned unchanged.
 * 
 * @param predicate Function to test if the reducer should handle the action
 * @param reducer The reducer to apply if the predicate matches
 * @return A reducer that conditionally applies the provided reducer
 * 
 * Example:
 * ```kotlin
 * val counterReducer = createReducer<CounterState>(
 *     predicate = { it is CounterAction }
 * ) { state, action ->
 *     when (action) {
 *         is CounterAction.Increment -> state.copy(count = state.count + 1)
 *         else -> state
 *     }
 * }
 * ```
 */
public fun <S : State> createReducer(
    predicate: (Action) -> Boolean,
    reducer: Reducer<S>
): Reducer<S> {
    return { state, action ->
        if (predicate(action)) {
            reducer(state, action)
        } else {
            state
        }
    }
}
