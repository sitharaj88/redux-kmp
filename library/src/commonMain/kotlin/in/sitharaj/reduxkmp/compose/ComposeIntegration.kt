package `in`.sitharaj.reduxkmp.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import `in`.sitharaj.reduxkmp.core.Store
import `in`.sitharaj.reduxkmp.core.State as ReduxState
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Collects the entire state from the Redux store as a Compose State.
 * 
 * The composable will recompose whenever the state changes.
 * 
 * Example:
 * ```kotlin
 * @Composable
 * fun CounterScreen(store: Store<CounterState>) {
 *     val state = store.collectAsState()
 *     Text("Count: ${state.value.count}")
 * }
 * ```
 */
@Composable
public fun <S : ReduxState> Store<S>.collectAsState(
    context: CoroutineContext = EmptyCoroutineContext
): androidx.compose.runtime.State<S> {
    return state.collectAsState(context)
}

/**
 * Selects and collects a specific part of the state using a selector function.
 * 
 * The composable will only recompose when the selected value changes,
 * providing better performance than collecting the entire state.
 * 
 * Example:
 * ```kotlin
 * @Composable
 * fun CountDisplay(store: Store<AppState>) {
 *     val count = store.selectState { it.counter.count }
 *     Text("Count: ${count.value}")
 * }
 * ```
 */
@Composable
public fun <S : ReduxState, T> Store<S>.selectState(
    context: CoroutineContext = EmptyCoroutineContext,
    selector: (S) -> T
): androidx.compose.runtime.State<T> {
    val flow = remember(this, selector) {
        state.map(selector)
    }
    return flow.collectAsState(selector(currentState), context)
}

/**
 * Provides a Store instance to the composable tree via CompositionLocal.
 * 
 * This allows child composables to access the store without prop drilling.
 * 
 * Example:
 * ```kotlin
 * @Composable
 * fun App() {
 *     val store = remember { createAppStore() }
 *     ProvideStore(store) {
 *         CounterScreen()
 *     }
 * }
 * 
 * @Composable
 * fun CounterScreen() {
 *     val store = useStore<AppState>()
 *     val state = store.collectAsState()
 *     // ...
 * }
 * ```
 */
@Composable
public fun <S : ReduxState> ProvideStore(
    store: Store<S>,
    content: @Composable () -> Unit
) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalStore provides store,
        content = content
    )
}

/**
 * CompositionLocal for accessing the Redux store.
 */
private val LocalStore = androidx.compose.runtime.compositionLocalOf<Store<*>?> { null }

/**
 * Retrieves the Store from the composition.
 * 
 * Must be called within a ProvideStore composable.
 * 
 * Example:
 * ```kotlin
 * @Composable
 * fun MyComponent() {
 *     val store = useStore<AppState>()
 *     val state = store.collectAsState()
 *     // ...
 * }
 * ```
 */
@Composable
public fun <S : ReduxState> useStore(): Store<S> {
    @Suppress("UNCHECKED_CAST")
    return LocalStore.current as? Store<S>
        ?: throw IllegalStateException("Store not provided. Make sure to wrap your composables with ProvideStore.")
}

/**
 * Retrieves the Store from the composition, or null if not provided.
 * 
 * Example:
 * ```kotlin
 * @Composable
 * fun MyComponent() {
 *     val store = useStoreOrNull<AppState>()
 *     if (store != null) {
 *         val state = store.collectAsState()
 *         // ...
 *     }
 * }
 * ```
 */
@Composable
public fun <S : ReduxState> useStoreOrNull(): Store<S>? {
    @Suppress("UNCHECKED_CAST")
    return LocalStore.current as? Store<S>
}
