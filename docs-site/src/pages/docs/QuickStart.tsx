import React from 'react';
import { CodeBlock } from '../../components/CodeBlock';

export const QuickStart: React.FC = () => {
    const stateCode = `data class CounterState(
    val value: Int = 0
)`;

    const sliceCode = `import com.sitharaj.reduxkmp.toolkit.createSlice

val counterSlice = createSlice(
    name = "counter",
    initialState = CounterState(),
    reducers = {
        "increment" { state, _ -> state.copy(value = state.value + 1) }
        "decrement" { state, _ -> state.copy(value = state.value - 1) }
        "incrementBy" { state, action -> 
            state.copy(value = state.value + (action.payload as Int)) 
        }
    }
)

// Export Actions
val increment = counterSlice.actions["increment"]
val decrement = counterSlice.actions["decrement"]
val incrementBy = counterSlice.actions["incrementBy"]`;

    const storeCode = `import com.sitharaj.reduxkmp.core.createStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

val store = createStore(
    reducer = counterSlice.reducer,
    initialState = counterSlice.initialState,
    scope = CoroutineScope(Dispatchers.Main)
)`;

    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">Quick Start</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8">
                Let's build a simple Counter application to demonstrate the core Redux KMP flow.
            </p>

            <h2 className="text-2xl font-bold mb-4 mt-8">1. Define State</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                State should be a plain immutable Kotlin data class.
            </p>
            <CodeBlock code={stateCode} language="kotlin" title="CounterState.kt" />

            <h2 className="text-2xl font-bold mb-4 mt-8">2. Create a Slice</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                Use <code>createSlice</code> to define your state, reducers, and actions in one place.
                It automatically generates action creators for you.
            </p>
            <CodeBlock code={sliceCode} language="kotlin" title="CounterSlice.kt" />

            <h2 className="text-2xl font-bold mb-4 mt-8">3. Create the Store</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                Initialize the store with your slice's reducer.
            </p>
            <CodeBlock code={storeCode} language="kotlin" title="Store.kt" />

            <h2 className="text-2xl font-bold mb-4 mt-8">Next Steps</h2>
            <p className="text-[var(--text-secondary)]">
                Now that you have a store, check out the <a href="/docs/platforms/compose" className="text-[var(--accent)] hover:underline">Compose Integration</a> guide to connect it to your UI.
            </p>
        </div>
    );
};
