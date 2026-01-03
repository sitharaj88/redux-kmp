/**
 * Redux KMP - A Kotlin Multiplatform Redux Implementation
 * 
 * This library provides a Redux-like state management solution for Kotlin Multiplatform
 * projects with a type-safe Kotlin DSL.
 * 
 * ## Architecture
 * 
 * ```
 * ┌─────────────────────────────────────────────────────────────┐
 * │                        Application                         │
 * └─────────────────────────────────────────────────────────────┘
 *                              │
 *                              ▼ dispatch(action)
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Middleware Chain                                          │
 * │  ┌──────────┐   ┌──────────┐   ┌──────────┐                │
 * │  │  Thunk   │──▶│ Logging  │──▶│   ...    │──▶ Reducer    │
 * │  └──────────┘   └──────────┘   └──────────┘                │
 * └─────────────────────────────────────────────────────────────┘
 *                              │
 *                              ▼
 * ┌─────────────────────────────────────────────────────────────┐
 * │                 StateFlow<State>                           │
 * └─────────────────────────────────────────────────────────────┘
 * ```
 * 
 * ## Quick Start
 * 
 * ```kotlin
 * // 1. Define your state
 * data class AppState(
 *     val count: Int = 0,
 *     val isLoading: Boolean = false
 * ) : State
 * 
 * // 2. Define your actions
 * sealed interface AppAction : Action {
 *     object Increment : AppAction
 *     object Decrement : AppAction
 *     data class SetCount(val value: Int) : AppAction
 * }
 * 
 * // 3. Create a store using DSL
 * val store = store<AppState> {
 *     initialState { AppState() }
 *     
 *     reducer {
 *         on<AppAction.Increment> { state, _ ->
 *             state.copy(count = state.count + 1)
 *         }
 *         on<AppAction.Decrement> { state, _ ->
 *             state.copy(count = state.count - 1)
 *         }
 *         on<AppAction.SetCount> { state, action ->
 *             state.copy(count = action.value)
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
 * 
 * // 4. Dispatch actions
 * store.dispatch(AppAction.Increment)
 * 
 * // 5. Observe state changes
 * store.state.collect { state ->
 *     println("Count: ${state.count}")
 * }
 * ```
 * 
 * ## Features
 * 
 * - **Type-Safe DSL**: Create stores and reducers with a clean, Kotlin-idiomatic API
 * - **Middleware Support**: Add logging, async operations (thunks), and more
 * - **Compose Integration**: Seamless integration with Compose Multiplatform
 * - **Flow-Based**: Uses Kotlin Coroutines Flows for state observation
 * - **Multiplatform**: Supports Android, iOS, Desktop, Web (JS/WASM)
 */
package `in`.sitharaj.reduxkmp

// Re-export core APIs for convenience
public typealias Action = `in`.sitharaj.reduxkmp.core.Action
public typealias State = `in`.sitharaj.reduxkmp.core.State
public typealias Reducer<S> = `in`.sitharaj.reduxkmp.core.Reducer<S>
public typealias Store<S> = `in`.sitharaj.reduxkmp.core.Store<S>
public typealias Middleware<S> = `in`.sitharaj.reduxkmp.core.Middleware<S>
public typealias Dispatcher = `in`.sitharaj.reduxkmp.core.Dispatcher
public typealias GetState<S> = `in`.sitharaj.reduxkmp.core.GetState<S>

/**
 * Library version information.
 */
public object ReduxKmp {
    /** The current version of the library */
    public const val VERSION: String = "1.0.0"
    
    /** The library name */
    public const val NAME: String = "Redux KMP"
}
