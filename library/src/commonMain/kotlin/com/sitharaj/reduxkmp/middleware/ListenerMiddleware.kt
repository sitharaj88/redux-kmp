package com.sitharaj.reduxkmp.middleware

import com.sitharaj.reduxkmp.core.Action
import com.sitharaj.reduxkmp.core.Dispatcher
import com.sitharaj.reduxkmp.core.Middleware
import com.sitharaj.reduxkmp.core.State
import com.sitharaj.reduxkmp.core.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * Listener Middleware - Side effect middleware for Redux KMP.
 * 
 * Similar to Redux Toolkit's `createListenerMiddleware`, this allows you to
 * define "listeners" that respond to dispatched actions with custom logic.
 * 
 * ## Usage
 * 
 * ```kotlin
 * // Create the middleware
 * val listenerMiddleware = createListenerMiddleware<AppState>()
 * 
 * // Add listeners
 * listenerMiddleware.addListener(
 *     actionType = CounterAction.Increment::class
 * ) { action, api ->
 *     // This runs after the action is dispatched
 *     println("Counter incremented!")
 *     
 *     // Access state
 *     val count = api.getState().count
 *     
 *     // Dispatch more actions
 *     if (count > 10) {
 *         api.dispatch(ShowWarningAction("Count is high!"))
 *     }
 * }
 * 
 * // Use predicate-based matching
 * listenerMiddleware.addListener(
 *     predicate = { action -> action is AsyncThunkFulfilled<*, *> }
 * ) { action, api ->
 *     println("An async thunk completed!")
 * }
 * 
 * // Add to store
 * val store = createStore(...) {
 *     addMiddleware(listenerMiddleware.middleware)
 * }
 * ```
 */

// ============================================
// Listener API
// ============================================

/**
 * API provided to listener callbacks.
 */
public class ListenerAPI<S : State>(
    /** Dispatch an action */
    public val dispatch: Dispatcher,
    /** Get the current state */
    public val getState: () -> S,
    /** Get the state before this action was dispatched */
    public val getOriginalState: () -> S,
    /** The store's coroutine scope */
    public val scope: CoroutineScope,
    /** Cancel this listener's execution */
    public val cancel: () -> Unit,
    /** Signal to check if cancelled */
    public val signal: CancelSignal
) {
    /**
     * Delays execution for a specified duration (in milliseconds).
     */
    public suspend fun delay(millis: Long) {
        kotlinx.coroutines.delay(millis)
    }
    
    /**
     * Waits for a specific action to be dispatched.
     * Returns the action when it's dispatched.
     */
    public suspend fun take(
        predicate: (Action) -> Boolean,
        timeout: Long = Long.MAX_VALUE
    ): Action? {
        // This would require more complex implementation with channels
        // For now, just delay and return null
        delay(timeout)
        return null
    }
    
    /**
     * Forks a new listener that runs concurrently.
     */
    public fun fork(block: suspend () -> Unit): Job {
        return scope.launch {
            block()
        }
    }
}

/**
 * Signal for checking listener cancellation.
 */
public class CancelSignal {
    public var cancelled: Boolean = false
        private set
    
    public fun cancel() {
        cancelled = true
    }
    
    public fun throwIfCancelled() {
        if (cancelled) {
            throw kotlinx.coroutines.CancellationException("Listener cancelled")
        }
    }
}

// ============================================
// Listener Entry
// ============================================

/**
 * A registered listener with its matcher and effect.
 */
@PublishedApi
internal class ListenerEntry<S : State>(
    val id: String,
    val matcher: (Action) -> Boolean,
    val effect: suspend (action: Action, api: ListenerAPI<S>) -> Unit,
    val runBefore: Boolean = false
)

// ============================================
// Listener Middleware
// ============================================

/**
 * The listener middleware instance.
 */
public class ListenerMiddleware<S : State> {
    @PublishedApi
    internal val listeners: MutableList<ListenerEntry<S>> = mutableListOf()
    @PublishedApi
    internal var listenerIdCounter: Int = 0
    @PublishedApi
    internal val activeJobs: MutableMap<String, Job> = mutableMapOf()
    
    /**
     * The actual middleware to add to the store.
     */
    public val middleware: Middleware<S> = object : Middleware<S> {
        override fun apply(store: Store<S>, action: Action, next: Dispatcher) {
            val originalState = store.currentState
            
            // Run "before" listeners
            for (listener in listeners.filter { it.runBefore && it.matcher(action) }) {
                runListener(store, action, originalState, listener)
            }
            
            // Pass to next middleware/reducer
            next(action)
            
            // Run "after" listeners
            for (listener in listeners.filter { !it.runBefore && it.matcher(action) }) {
                runListener(store, action, originalState, listener)
            }
        }
    }
    
    private fun runListener(
        store: Store<S>,
        action: Action,
        originalState: S,
        listener: ListenerEntry<S>
    ) {
        val cancelSignal = CancelSignal()
        
        val job = store.scope.launch {
            val api = ListenerAPI(
                dispatch = store::dispatch,
                getState = store::getState,
                getOriginalState = { originalState },
                scope = store.scope,
                cancel = { cancelSignal.cancel() },
                signal = cancelSignal
            )
            
            try {
                listener.effect(action, api)
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Expected when listener is cancelled
            } catch (e: Throwable) {
                // Log error but don't crash
                println("[ListenerMiddleware] Error in listener ${listener.id}: ${e.message}")
            }
        }
        
        activeJobs[listener.id] = job
    }
    
    /**
     * Adds a listener that responds to actions matching a type.
     * 
     * @param actionType The action class to match
     * @param runBefore If true, runs before the reducer; otherwise after
     * @param effect The callback to run when the action matches
     * @return A function to unsubscribe this listener
     */
    public inline fun <reified A : Action> addListener(
        actionType: KClass<A> = A::class,
        runBefore: Boolean = false,
        crossinline effect: suspend (action: A, api: ListenerAPI<S>) -> Unit
    ): () -> Unit {
        val id = "listener-${listenerIdCounter++}"
        
        val entry = ListenerEntry<S>(
            id = id,
            matcher = { action -> action is A },
            effect = { action, api ->
                @Suppress("UNCHECKED_CAST")
                effect(action as A, api)
            },
            runBefore = runBefore
        )
        
        listeners.add(entry)
        
        return {
            listeners.removeAll { it.id == id }
            activeJobs[id]?.cancel()
            activeJobs.remove(id)
        }
    }
    
    /**
     * Adds a listener with a custom predicate matcher.
     * 
     * @param predicate Function to test if the listener should run
     * @param runBefore If true, runs before the reducer; otherwise after
     * @param effect The callback to run when the predicate matches
     * @return A function to unsubscribe this listener
     */
    public fun addListener(
        predicate: (Action) -> Boolean,
        runBefore: Boolean = false,
        effect: suspend (action: Action, api: ListenerAPI<S>) -> Unit
    ): () -> Unit {
        val id = "listener-${listenerIdCounter++}"
        
        val entry = ListenerEntry<S>(
            id = id,
            matcher = predicate,
            effect = effect,
            runBefore = runBefore
        )
        
        listeners.add(entry)
        
        return {
            listeners.removeAll { it.id == id }
            activeJobs[id]?.cancel()
            activeJobs.remove(id)
        }
    }
    
    /**
     * Clears all registered listeners.
     */
    public fun clearListeners() {
        listeners.clear()
        activeJobs.values.forEach { it.cancel() }
        activeJobs.clear()
    }
    
    /**
     * Returns the number of registered listeners.
     */
    public fun listenerCount(): Int = listeners.size
}

// ============================================
// Factory Function
// ============================================

/**
 * Creates a listener middleware instance.
 * 
 * ```kotlin
 * val listenerMiddleware = createListenerMiddleware<AppState>()
 * 
 * listenerMiddleware.addListener<CounterAction.Increment> { action, api ->
 *     println("Incremented to: ${api.getState().count}")
 * }
 * 
 * val store = createStore(...) {
 *     addMiddleware(listenerMiddleware.middleware)
 * }
 * ```
 */
public fun <S : State> createListenerMiddleware(): ListenerMiddleware<S> {
    return ListenerMiddleware()
}
