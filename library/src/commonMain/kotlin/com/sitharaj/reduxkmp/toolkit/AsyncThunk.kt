package com.sitharaj.reduxkmp.toolkit

import com.sitharaj.reduxkmp.core.Action
import com.sitharaj.reduxkmp.core.Dispatcher
import com.sitharaj.reduxkmp.core.GetState
import com.sitharaj.reduxkmp.core.State
import com.sitharaj.reduxkmp.middleware.ThunkAction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

/**
 * Redux Toolkit's createAsyncThunk equivalent for Kotlin Multiplatform.
 * 
 * Creates an async thunk with lifecycle actions (pending, fulfilled, rejected).
 * 
 * ## Usage
 * 
 * ```kotlin
 * // Define the async thunk
 * val fetchUser = createAsyncThunk<String, User, AppState>(
 *     typePrefix = "users/fetchById"
 * ) { userId, thunkApi ->
 *     val response = api.getUser(userId)
 *     response.body() ?: throw Exception("User not found")
 * }
 * 
 * // Dispatch it
 * store.dispatch(fetchUser("123"))
 * 
 * // Handle in reducer
 * val userReducer = reducer<UserState> {
 *     on<FetchUser.Pending> { state, _ ->
 *         state.copy(loading = true)
 *     }
 *     on<FetchUser.Fulfilled> { state, action ->
 *         state.copy(loading = false, user = action.payload)
 *     }
 *     on<FetchUser.Rejected> { state, action ->
 *         state.copy(loading = false, error = action.error)
 *     }
 * }
 * ```
 */

// ============================================
// Async Thunk Lifecycle Actions
// ============================================

/**
 * Base interface for async thunk lifecycle actions.
 */
public sealed interface AsyncThunkAction : Action {
    public val typePrefix: String
    public val requestId: String
}

/**
 * Dispatched when the async operation starts.
 */
public data class AsyncThunkPending<Arg>(
    override val typePrefix: String,
    override val requestId: String,
    val arg: Arg,
    val meta: Map<String, Any?> = emptyMap()
) : AsyncThunkAction

/**
 * Dispatched when the async operation succeeds.
 */
public data class AsyncThunkFulfilled<Arg, Result>(
    override val typePrefix: String,
    override val requestId: String,
    val arg: Arg,
    val payload: Result,
    val meta: Map<String, Any?> = emptyMap()
) : AsyncThunkAction

/**
 * Dispatched when the async operation fails.
 */
public data class AsyncThunkRejected<Arg>(
    override val typePrefix: String,
    override val requestId: String,
    val arg: Arg,
    val error: Throwable,
    val meta: Map<String, Any?> = emptyMap()
) : AsyncThunkAction

// ============================================
// Thunk API - Context for async operations
// ============================================

/**
 * API object provided to the async thunk payload creator.
 * Provides access to dispatch, getState, and utilities.
 */
public class ThunkAPI<S : State>(
    /** Dispatch an action */
    public val dispatch: Dispatcher,
    /** Get the current state */
    public val getState: GetState<S>,
    /** Unique ID for this request */
    public val requestId: String,
    /** Signal for checking if the thunk was aborted */
    public val signal: AbortSignal,
    /** Extra argument passed to createAsyncThunk */
    public val extra: Any? = null
) {
    /**
     * Rejects the thunk with a specific value (instead of throwing).
     */
    public fun rejectWithValue(value: Any): Nothing {
        throw RejectWithValueException(value)
    }
    
    /**
     * Fulfills the thunk with a specific value (for early return).
     */
    public fun fulfillWithValue(value: Any): Nothing {
        throw FulfillWithValueException(value)
    }
}

/**
 * Signal for checking/triggering abort.
 */
public class AbortSignal {
    public var aborted: Boolean = false
        private set
    
    private var abortReason: String? = null
    
    public fun abort(reason: String = "Aborted") {
        aborted = true
        abortReason = reason
    }
    
    public fun throwIfAborted() {
        if (aborted) {
            throw CancellationException(abortReason ?: "Aborted")
        }
    }
}

// Internal exceptions for control flow
internal class RejectWithValueException(val value: Any) : Exception()
internal class FulfillWithValueException(val value: Any) : Exception()

// ============================================
// AsyncThunk - The returned thunk action creator
// ============================================

/**
 * An async thunk that can be dispatched.
 */
public class AsyncThunk<Arg, Result, S : State> internal constructor(
    public val typePrefix: String,
    private val payloadCreator: suspend (Arg, ThunkAPI<S>) -> Result,
    private val options: AsyncThunkOptions<Arg, Result, S>
) {
    private var requestIdCounter = 0
    private val activeRequests = mutableMapOf<String, Job>()
    
    /**
     * Action type constants for matching in reducers.
     */
    public val pending: String = "$typePrefix/pending"
    public val fulfilled: String = "$typePrefix/fulfilled"
    public val rejected: String = "$typePrefix/rejected"
    
    /**
     * Creates a ThunkAction that can be dispatched.
     */
    public operator fun invoke(arg: Arg): ThunkAction<S> {
        return object : ThunkAction<S> {
            override suspend fun execute(dispatch: Dispatcher, getState: GetState<S>) {
                val requestId = generateRequestId()
                val signal = AbortSignal()
                
                // Check condition before executing
                if (options.condition != null && !options.condition.invoke(arg, getState)) {
                    return
                }
                
                val thunkApi = ThunkAPI(
                    dispatch = dispatch,
                    getState = getState,
                    requestId = requestId,
                    signal = signal,
                    extra = options.extra
                )
                
                // Dispatch pending
                dispatch(AsyncThunkPending(
                    typePrefix = typePrefix,
                    requestId = requestId,
                    arg = arg
                ))
                
                try {
                    signal.throwIfAborted()
                    
                    val result = payloadCreator(arg, thunkApi)
                    
                    signal.throwIfAborted()
                    
                    // Dispatch fulfilled
                    dispatch(AsyncThunkFulfilled(
                        typePrefix = typePrefix,
                        requestId = requestId,
                        arg = arg,
                        payload = result
                    ))
                    
                } catch (e: FulfillWithValueException) {
                    @Suppress("UNCHECKED_CAST")
                    dispatch(AsyncThunkFulfilled(
                        typePrefix = typePrefix,
                        requestId = requestId,
                        arg = arg,
                        payload = e.value as Result
                    ))
                    
                } catch (e: RejectWithValueException) {
                    dispatch(AsyncThunkRejected(
                        typePrefix = typePrefix,
                        requestId = requestId,
                        arg = arg,
                        error = Exception("Rejected with value: ${e.value}")
                    ))
                    
                } catch (e: CancellationException) {
                    dispatch(AsyncThunkRejected(
                        typePrefix = typePrefix,
                        requestId = requestId,
                        arg = arg,
                        error = e
                    ))
                    
                } catch (e: Throwable) {
                    dispatch(AsyncThunkRejected(
                        typePrefix = typePrefix,
                        requestId = requestId,
                        arg = arg,
                        error = e
                    ))
                }
            }
        }
    }
    
    /**
     * Checks if an action matches the pending type.
     */
    public fun matchPending(action: Action): Boolean {
        return action is AsyncThunkPending<*> && action.typePrefix == typePrefix
    }
    
    /**
     * Checks if an action matches the fulfilled type.
     */
    public fun matchFulfilled(action: Action): Boolean {
        return action is AsyncThunkFulfilled<*, *> && action.typePrefix == typePrefix
    }
    
    /**
     * Checks if an action matches the rejected type.
     */
    public fun matchRejected(action: Action): Boolean {
        return action is AsyncThunkRejected<*> && action.typePrefix == typePrefix
    }
    
    private fun generateRequestId(): String {
        return "${typePrefix}-${requestIdCounter++}-${currentTimeMillis()}"
    }
}

/**
 * Options for createAsyncThunk.
 */
public class AsyncThunkOptions<Arg, Result, S : State>(
    /** Condition to check before executing - if returns false, thunk is skipped */
    public val condition: ((Arg, GetState<S>) -> Boolean)? = null,
    /** Extra argument available in ThunkAPI */
    public val extra: Any? = null,
    /** ID generator for request IDs */
    public val idGenerator: (() -> String)? = null
)

// ============================================
// Factory Function
// ============================================

/**
 * Creates an async thunk action creator.
 * 
 * @param typePrefix A string that will be used to generate action type constants
 * @param options Optional configuration
 * @param payloadCreator The async function that performs the actual work
 */
public fun <Arg, Result, S : State> createAsyncThunk(
    typePrefix: String,
    options: AsyncThunkOptions<Arg, Result, S> = AsyncThunkOptions(),
    payloadCreator: suspend (arg: Arg, thunkApi: ThunkAPI<S>) -> Result
): AsyncThunk<Arg, Result, S> {
    return AsyncThunk(
        typePrefix = typePrefix,
        payloadCreator = payloadCreator,
        options = options
    )
}

/**
 * Creates an async thunk with no argument.
 */
public fun <Result, S : State> createAsyncThunk(
    typePrefix: String,
    payloadCreator: suspend (thunkApi: ThunkAPI<S>) -> Result
): AsyncThunk<Unit, Result, S> {
    return AsyncThunk(
        typePrefix = typePrefix,
        payloadCreator = { _, api -> payloadCreator(api) },
        options = AsyncThunkOptions()
    )
}

// Helper to get current time (shared with middleware)
internal expect fun currentTimeMillis(): Long
