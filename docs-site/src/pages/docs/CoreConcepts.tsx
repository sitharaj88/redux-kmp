import React from 'react';
import { CodeBlock } from '../../components/CodeBlock';

export const CoreConcepts: React.FC = () => {
    const actionCode = `// 1. Define Actions (Sealed Class recommended)
sealed interface CounterAction {
    data object Increment : CounterAction
    data object Decrement : CounterAction
    data class Add(val amount: Int) : CounterAction
}`;

    const reducerCode = `// 2. Define Reducer
fun counterReducer(state: CounterState, action: Any): CounterState {
    return when (action) {
        is CounterAction.Increment -> state.copy(value = state.value + 1)
        is CounterAction.Decrement -> state.copy(value = state.value - 1)
        is CounterAction.Add -> state.copy(value = state.value + action.amount)
        else -> state
    }
}`;

    const storeCode = `// 3. Create Store
val store = createStore(
    reducer = ::counterReducer,
    initialState = CounterState(0)
)`;

    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">Core Concepts</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8">
                Understanding the fundamental building blocks of Redux KMP: Store, State, Actions, and Reducers.
            </p>

            <section id="store" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">The Store</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    The Store is the single source of truth for your application state. It holds the current state tree and exposes methods to dispatch actions and subscribe to changes.
                </p>
                <CodeBlock code={storeCode} language="kotlin" title="Store Creation" />
            </section>

            <section id="actions" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">Actions</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    Actions are plain objects (usually Kotlin data classes or objects) that represent an intention to change the state. They are the only way to get data into the store.
                </p>
                <CodeBlock code={actionCode} language="kotlin" title="Action Definition" />
            </section>

            <section id="reducers" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">Reducers</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    Reducers are pure functions that take the current state and an action, and return a new state. They must never mutate the state directly.
                </p>
                <CodeBlock code={reducerCode} language="kotlin" title="Reducer Function" />
            </section>
        </div>
    );
};
