import React from 'react';
import { CodeBlock } from '../../components/CodeBlock';

export const ReduxToolkit: React.FC = () => {
    const sliceCode = `val counterSlice = createSlice(
    name = "counter",
    initialState = 0,
    reducers = {
        "increment" { state, _ -> state + 1 }
        "decrement" { state, _ -> state - 1 }
    }
)`;

    const thunkCode = `val fetchUser = createAsyncThunk<User, String>(
    type = "users/fetch",
    thunk = { userId ->
        api.getUser(userId) // Suspend call
    }
)

// In Reducer (extraReducers)
builder.addCase(fetchUser.fulfilled) { state, action ->
    state.copy(users = state.users + action.payload)
}`;

    const adapterCode = `val usersAdapter = createEntityAdapter<User, String>(
    selectId = { it.id }
)

val usersSlice = createSlice(
    name = "users",
    initialState = usersAdapter.getInitialState(),
    reducers = {
        "addUser" { state, action ->
            usersAdapter.addOne(state, action.payload)
        }
    }
)`;

    const selectorCode = `val selectCount = { state: AppState -> state.counter }
val selectDoubleCount = createSelector(selectCount) { count ->
    count * 2
}`;

    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">Redux Toolkit KMP</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8">
                A set of tools to simplify common Redux use cases, ported to Kotlin Multiplatform.
            </p>

            <section id="create-slice" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">createSlice</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    Automatically generates action creators and action types based on your reducers.
                </p>
                <CodeBlock code={sliceCode} language="kotlin" title="Slice Definition" />
            </section>

            <section id="async-thunk" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">createAsyncThunk</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    Abstractions for async logic using Coroutines. Handles pending, fulfilled, and rejected states automatically.
                </p>
                <CodeBlock code={thunkCode} language="kotlin" title="Async Thunk" />
            </section>

            <section id="entity-adapter" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">createEntityAdapter</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    Standardized way to store collections of data (normalization). Provides CRUD reducers like `addOne`, `setAll`, `updateOne`.
                </p>
                <CodeBlock code={adapterCode} language="kotlin" title="Entity Adapter" />
            </section>

            <section id="selectors" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">createSelector</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    Memoized selectors for derived state. Recomputes only when input selectors change.
                </p>
                <CodeBlock code={selectorCode} language="kotlin" title="Memoized Selector" />
            </section>
        </div>
    );
};
