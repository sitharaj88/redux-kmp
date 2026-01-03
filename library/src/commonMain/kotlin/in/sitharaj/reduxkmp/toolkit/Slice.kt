package `in`.sitharaj.reduxkmp.toolkit

import `in`.sitharaj.reduxkmp.core.Action
import `in`.sitharaj.reduxkmp.core.Reducer
import `in`.sitharaj.reduxkmp.core.State
import kotlin.jvm.JvmName

/**
 * Slice - createSlice equivalent for Redux KMP.
 * 
 * A Slice combines the reducer, actions, and initial state for a feature 
 * into a single, cohesive unit, similar to Redux Toolkit's `createSlice`.
 * 
 * ## Usage
 * 
 * ```kotlin
 * // Create a slice
 * val counterSlice = createSlice<CounterState>(
 *     name = "counter",
 *     initialState = CounterState()
 * ) {
 *     // Define reducers with auto-generated action creators
 *     reduce("increment") { state, _ ->
 *         state.copy(count = state.count + 1)
 *     }
 *     
 *     reduce("decrement") { state, _ ->
 *         state.copy(count = state.count - 1)
 *     }
 *     
 *     reduce<Int>("addAmount") { state, payload ->
 *         state.copy(count = state.count + payload)
 *     }
 *     
 *     // Handle extra reducers (for async thunks)
 *     extraReducers {
 *         addCase(fetchUser.pending) { state, _ ->
 *             state.copy(loading = true)
 *         }
 *         addCase(fetchUser.fulfilled) { state, action ->
 *             state.copy(loading = false, user = action.payload)
 *         }
 *     }
 * }
 * 
 * // Use the slice
 * val reducer = counterSlice.reducer
 * val actions = counterSlice.actions
 * 
 * // Dispatch actions
 * store.dispatch(actions.increment())
 * store.dispatch(actions.addAmount(5))
 * ```
 */

// ============================================
// Slice Action Types
// ============================================

/**
 * Base interface for slice-generated actions.
 */
public interface SliceAction : Action {
    public val type: String
}

/**
 * A slice action with a payload.
 */
public data class PayloadAction<P>(
    override val type: String,
    val payload: P
) : SliceAction

/**
 * A slice action without a payload.
 */
public data class EmptyAction(
    override val type: String
) : SliceAction

// ============================================
// Action Creator
// ============================================

/**
 * An action creator that generates actions with the slice's type prefix.
 */
public class ActionCreator<P>(
    public val type: String
) {
    /**
     * Creates an action with the given payload.
     */
    public operator fun invoke(payload: P): PayloadAction<P> {
        return PayloadAction(type = type, payload = payload)
    }
}

/**
 * An action creator for actions without payloads.
 */
public class EmptyActionCreator(
    public val type: String
) {
    /**
     * Creates an action without payload.
     */
    public operator fun invoke(): EmptyAction {
        return EmptyAction(type = type)
    }
}

// ============================================
// Slice
// ============================================

/**
 * A slice containing the reducer, actions, and name for a feature.
 */
public class Slice<S : State> internal constructor(
    /** The name of the slice */
    public val name: String,
    /** The initial state */
    public val initialState: S,
    /** The combined reducer */
    public val reducer: Reducer<S>,
    /** Map of action names to action creators */
    internal val actionCreatorsMap: Map<String, Any>,
    /** The action types */
    public val actionTypes: Set<String>
) {
    /**
     * Dynamic access to action creators.
     */
    public val actions: SliceActions<S> = SliceActions(this)
    
    /**
     * Gets an action creator by name.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <P> getActionCreator(name: String): ActionCreator<P>? {
        return actionCreatorsMap[name] as? ActionCreator<P>
    }
    
    /**
     * Gets an empty action creator by name.
     */
    public fun getEmptyActionCreator(name: String): EmptyActionCreator? {
        return actionCreatorsMap[name] as? EmptyActionCreator
    }
}

/**
 * Dynamic access to slice action creators.
 */
public class SliceActions<S : State>(
    private val slice: Slice<S>
) {
    /**
     * Invokes an action creator by name with no payload.
     */
    public fun invoke(name: String): EmptyAction {
        val creator = slice.getEmptyActionCreator(name)
            ?: throw IllegalArgumentException("No action creator found for: $name")
        return creator()
    }
    
    /**
     * Invokes an action creator by name with a payload.
     */
    public fun <P> invoke(name: String, payload: P): PayloadAction<P> {
        @Suppress("UNCHECKED_CAST")
        val creator = slice.getActionCreator<P>(name)
            ?: throw IllegalArgumentException("No action creator found for: $name")
        return creator(payload)
    }
}

// ============================================
// Slice Builder
// ============================================

/**
 * Builder for creating a slice.
 */
@DslMarker
public annotation class SliceDsl

@SliceDsl
public class SliceBuilder<S : State>(
    @PublishedApi
    internal val name: String,
    @PublishedApi
    internal val initialState: S
) {
    @PublishedApi
    internal val reducers: MutableList<CaseReducer<S, *>> = mutableListOf()
    @PublishedApi
    internal val extraReducers: MutableList<CaseReducer<S, *>> = mutableListOf()
    @PublishedApi
    internal val actionCreators: MutableMap<String, Any> = mutableMapOf()
    @PublishedApi
    internal val actionTypes: MutableSet<String> = mutableSetOf()
    
    /**
     * Defines a reducer for an action without payload.
     */
    public fun reduce(
        actionName: String,
        reducer: (state: S, action: EmptyAction) -> S
    ) {
        val type = "$name/$actionName"
        actionTypes.add(type)
        
        // Create action creator
        actionCreators[actionName] = EmptyActionCreator(type)
        
        // Add reducer
        reducers.add(CaseReducer<S, EmptyAction>(
            matcher = { action -> action is EmptyAction && action.type == type },
            reducer = reducer
        ))
    }
    
    /**
     * Defines a reducer for an action with payload.
     */
    @JvmName("reduceWithPayload")
    public inline fun <reified P> reduce(
        actionName: String,
        crossinline reducer: (state: S, payload: P) -> S
    ) {
        val type = "$name/$actionName"
        actionTypes.add(type)
        
        // Create action creator
        actionCreators[actionName] = ActionCreator<P>(type)
        
        // Add reducer
        reducers.add(CaseReducer<S, PayloadAction<P>>(
            matcher = { action -> 
                action is PayloadAction<*> && action.type == type 
            },
            reducer = { state, action ->
                @Suppress("UNCHECKED_CAST")
                reducer(state, action.payload)
            }
        ))
    }
    
    /**
     * Defines extra reducers for handling external actions (like async thunks).
     */
    public fun extraReducers(block: ExtraReducersBuilder<S>.() -> Unit) {
        val builder = ExtraReducersBuilder<S>()
        builder.block()
        extraReducers.addAll(builder.cases)
    }
    
    /**
     * Builds the slice.
     */
    internal fun build(): Slice<S> {
        val allReducers = reducers + extraReducers
        
        val combinedReducer: Reducer<S> = { state, action ->
            var result = state
            for (caseReducer in allReducers) {
                if (caseReducer.matches(action)) {
                    result = caseReducer.reduce(result, action)
                }
            }
            result
        }
        
        return Slice(
            name = name,
            initialState = initialState,
            reducer = combinedReducer,
            actionCreatorsMap = actionCreators.toMap(),
            actionTypes = actionTypes.toSet()
        )
    }
}

/**
 * Builder for extra reducers.
 */
@SliceDsl
public class ExtraReducersBuilder<S : State> {
    @PublishedApi
    internal val cases: MutableList<CaseReducer<S, *>> = mutableListOf()
    
    /**
     * Adds a case for handling a specific action type.
     */
    public inline fun <reified A : Action> addCase(
        crossinline reducer: (state: S, action: A) -> S
    ) {
        cases.add(CaseReducer<S, A>(
            matcher = { action -> action is A },
            reducer = { state, action ->
                @Suppress("UNCHECKED_CAST")
                reducer(state, action as A)
            }
        ))
    }
    
    /**
     * Adds a case for handling async thunk pending actions.
     */
    public fun <Arg, Result, State : `in`.sitharaj.reduxkmp.core.State> addCase(
        asyncThunk: AsyncThunk<Arg, Result, State>,
        lifecycle: AsyncThunkLifecycle,
        reducer: (state: S, action: Action) -> S
    ) {
        val matcher: (Action) -> Boolean = when (lifecycle) {
            AsyncThunkLifecycle.PENDING -> asyncThunk::matchPending
            AsyncThunkLifecycle.FULFILLED -> asyncThunk::matchFulfilled
            AsyncThunkLifecycle.REJECTED -> asyncThunk::matchRejected
        }
        
        cases.add(CaseReducer<S, Action>(
            matcher = matcher,
            reducer = reducer
        ))
    }
    
    /**
     * Adds a matcher for custom action matching.
     */
    public fun addMatcher(
        matcher: (Action) -> Boolean,
        reducer: (state: S, action: Action) -> S
    ) {
        cases.add(CaseReducer<S, Action>(
            matcher = matcher,
            reducer = reducer
        ))
    }
    
    /**
     * Adds a default case for unmatched actions.
     */
    public fun addDefaultCase(
        reducer: (state: S, action: Action) -> S
    ) {
        cases.add(CaseReducer<S, Action>(
            matcher = { true },
            reducer = reducer
        ))
    }
}

/**
 * Lifecycle phases for async thunks.
 */
public enum class AsyncThunkLifecycle {
    PENDING,
    FULFILLED,
    REJECTED
}

/**
 * Internal class for case reducers.
 */
@PublishedApi
internal class CaseReducer<S, A : Action>(
    private val matcher: (Action) -> Boolean,
    private val reducer: (S, A) -> S
) {
    fun matches(action: Action): Boolean = matcher(action)
    
    @Suppress("UNCHECKED_CAST")
    fun reduce(state: S, action: Action): S = reducer(state, action as A)
}

// ============================================
// Factory Function
// ============================================

/**
 * Creates a slice with the given name, initial state, and reducers.
 * 
 * @param name The name of the slice (used as action type prefix)
 * @param initialState The initial state for this slice
 * @param block Builder block for defining reducers
 */
public fun <S : State> createSlice(
    name: String,
    initialState: S,
    block: SliceBuilder<S>.() -> Unit
): Slice<S> {
    val builder = SliceBuilder(name, initialState)
    builder.block()
    return builder.build()
}
