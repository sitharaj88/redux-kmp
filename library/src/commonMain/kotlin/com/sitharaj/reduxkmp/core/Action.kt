package com.sitharaj.reduxkmp.core

/**
 * Base interface for all actions in the Redux store.
 * 
 * Actions are payloads of information that send data from your application to your store.
 * They are the only source of information for the store.
 * 
 * Example:
 * ```kotlin
 * sealed interface CounterAction : Action {
 *     object Increment : CounterAction
 *     object Decrement : CounterAction
 *     data class Add(val value: Int) : CounterAction
 * }
 * ```
 */
public interface Action

/**
 * Base interface for all state objects in the Redux store.
 * 
 * State should be immutable. Use data classes with val properties for easy copying.
 * 
 * Example:
 * ```kotlin
 * data class CounterState(
 *     val count: Int = 0,
 *     val isLoading: Boolean = false
 * ) : State
 * ```
 */
public interface State
