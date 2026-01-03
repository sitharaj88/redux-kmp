package com.sitharaj.reduxkmp.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Extension function to subscribe to a Flow from Swift/iOS.
 * This simplifies Flow collection in iOS by providing a cancellable subscription.
 */
public fun <T> Flow<T>.subscribe(
    scope: CoroutineScope,
    onEach: (T) -> Unit
): Job {
    return scope.launch {
        collect { value ->
            onEach(value)
        }
    }
}
