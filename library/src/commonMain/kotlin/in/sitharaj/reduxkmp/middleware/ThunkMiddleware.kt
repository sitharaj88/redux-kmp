package `in`.sitharaj.reduxkmp.middleware

import `in`.sitharaj.reduxkmp.core.Action
import `in`.sitharaj.reduxkmp.core.Dispatcher
import `in`.sitharaj.reduxkmp.core.GetState
import `in`.sitharaj.reduxkmp.core.Middleware
import `in`.sitharaj.reduxkmp.core.State
import `in`.sitharaj.reduxkmp.core.Store
import kotlinx.coroutines.launch

/**
 * A Thunk is an action that contains asynchronous logic.
 * 
 * Instead of dispatching a plain action object, you dispatch a function
 * that receives `dispatch` and `getState` as arguments. This allows you to:
 * - Make async API calls
 * - Dispatch multiple actions
 * - Dispatch actions conditionally based on state
 * - Perform side effects
 * 
 * Example:
 * ```kotlin
 * class FetchUserThunk(private val userId: String) : ThunkAction<AppState> {
 *     override suspend fun execute(dispatch: Dispatcher, getState: GetState<AppState>) {
 *         dispatch(UserAction.Loading)
 *         try {
 *             val user = api.fetchUser(userId)
 *             dispatch(UserAction.Success(user))
 *         } catch (e: Exception) {
 *             dispatch(UserAction.Error(e.message))
 *         }
 *     }
 * }
 * 
 * // Usage
 * store.dispatch(FetchUserThunk("123"))
 * ```
 */
public interface ThunkAction<S : State> : Action {
    /**
     * Executes the thunk's async logic.
     * 
     * @param dispatch Function to dispatch actions
     * @param getState Function to get the current state
     */
    public suspend fun execute(dispatch: Dispatcher, getState: GetState<S>)
}

/**
 * Middleware that intercepts ThunkActions and executes them.
 * 
 * When a ThunkAction is dispatched:
 * 1. The middleware catches it before it reaches the reducer
 * 2. Executes the thunk's suspend function in a coroutine
 * 3. Provides dispatch and getState to the thunk
 * 
 * Regular actions pass through to the next middleware/reducer.
 * 
 * Example:
 * ```kotlin
 * val store = createStore(
 *     initialState = AppState(),
 *     reducer = appReducer,
 *     scope = coroutineScope
 * ) {
 *     addMiddleware(ThunkMiddleware())
 * }
 * ```
 */
public class ThunkMiddleware<S : State> : Middleware<S> {
    override fun apply(store: Store<S>, action: Action, next: Dispatcher) {
        if (action is ThunkAction<*>) {
            // Cast is safe because we know S matches the store's state type
            @Suppress("UNCHECKED_CAST")
            val thunk = action as ThunkAction<S>
            
            // Launch the thunk in the store's coroutine scope
            store.scope.launch {
                thunk.execute(
                    dispatch = store::dispatch,
                    getState = store::getState
                )
            }
        } else {
            // Not a thunk, pass it to the next middleware
            next(action)
        }
    }
}

/**
 * Creates a ThunkAction from a lambda function.
 * This is a convenience function for creating simple thunks without defining a class.
 * 
 * Example:
 * ```kotlin
 * val fetchUser = thunk<AppState> { dispatch, getState ->
 *     dispatch(UserAction.Loading)
 *     delay(1000)
 *     dispatch(UserAction.Success(User("John")))
 * }
 * 
 * store.dispatch(fetchUser)
 * ```
 */
public fun <S : State> thunk(
    execute: suspend (dispatch: Dispatcher, getState: GetState<S>) -> Unit
): ThunkAction<S> {
    return object : ThunkAction<S> {
        override suspend fun execute(dispatch: Dispatcher, getState: GetState<S>) {
            execute(dispatch, getState)
        }
    }
}
