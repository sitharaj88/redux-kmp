import React from 'react';

export const APIReference: React.FC = () => {
    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">API Reference</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8">
                Complete reference for all Redux KMP classes and functions.
            </p>

            <div className="space-y-12">
                {/* Store Section */}
                <section>
                    <h2 className="text-2xl font-bold mb-4 flex items-center gap-2">
                        <span className="p-1 rounded bg-blue-500/10 text-blue-500 text-sm">Core</span>
                        Store
                    </h2>
                    <div className="overflow-x-auto rounded-lg border border-[var(--border)]">
                        <table className="w-full text-left text-sm">
                            <thead className="bg-[var(--bg-tertiary)] border-b border-[var(--border)]">
                                <tr>
                                    <th className="px-4 py-3 font-mono text-[var(--text-secondary)]">Name</th>
                                    <th className="px-4 py-3 font-mono text-[var(--text-secondary)]">Description</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-[var(--border)]">
                                <tr>
                                    <td className="px-4 py-3 font-mono text-blue-400">createStore</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Creates a new Redux Store with a reducer, initial state, and optional enhancer/middleware.
                                    </td>
                                </tr>
                                <tr>
                                    <td className="px-4 py-3 font-mono text-blue-400">Store</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        The main Store interface. Exposes <code>dispatch</code> and <code>state</code> (StateFlow).
                                    </td>
                                </tr>
                                <tr>
                                    <td className="px-4 py-3 font-mono text-blue-400">combineReducers</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Combines multiple reducer functions into a single reducing function.
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                {/* Toolkit Section */}
                <section>
                    <h2 className="text-2xl font-bold mb-4 flex items-center gap-2">
                        <span className="p-1 rounded bg-purple-500/10 text-purple-500 text-sm">Toolkit</span>
                        Redux Toolkit
                    </h2>
                    <div className="overflow-x-auto rounded-lg border border-[var(--border)]">
                        <table className="w-full text-left text-sm">
                            <thead className="bg-[var(--bg-tertiary)] border-b border-[var(--border)]">
                                <tr>
                                    <th className="px-4 py-3 font-mono text-[var(--text-secondary)]">Name</th>
                                    <th className="px-4 py-3 font-mono text-[var(--text-secondary)]">Description</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-[var(--border)]">
                                <tr>
                                    <td className="px-4 py-3 font-mono text-purple-400">createSlice</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Generates a slice of state, actions, and reducer from logic definitions.
                                    </td>
                                </tr>
                                <tr>
                                    <td className="px-4 py-3 font-mono text-purple-400">createAsyncThunk</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Handles async logic (Coroutines) with standard pending/fulfilled/rejected actions.
                                    </td>
                                </tr>
                                <tr>
                                    <td className="px-4 py-3 font-mono text-purple-400">createEntityAdapter</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Manages normalized state (IDs and entities) with CRUD selectors and reducers.
                                    </td>
                                </tr>
                                <tr>
                                    <td className="px-4 py-3 font-mono text-purple-400">createSelector</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Creates memoized selector functions for efficient state derivation.
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                {/* Middleware Section */}
                <section>
                    <h2 className="text-2xl font-bold mb-4 flex items-center gap-2">
                        <span className="p-1 rounded bg-green-500/10 text-green-500 text-sm">Middleware</span>
                        Middleware
                    </h2>
                    <div className="overflow-x-auto rounded-lg border border-[var(--border)]">
                        <table className="w-full text-left text-sm">
                            <thead className="bg-[var(--bg-tertiary)] border-b border-[var(--border)]">
                                <tr>
                                    <th className="px-4 py-3 font-mono text-[var(--text-secondary)]">Name</th>
                                    <th className="px-4 py-3 font-mono text-[var(--text-secondary)]">Description</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-[var(--border)]">
                                <tr>
                                    <td className="px-4 py-3 font-mono text-green-400">ThunkMiddleware</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Enables dispatching functions (Thunks) to intercept actions and perform side effects.
                                    </td>
                                </tr>
                                <tr>
                                    <td className="px-4 py-3 font-mono text-green-400">LoggingMiddleware</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Logs every action and state change to the console/platform log.
                                    </td>
                                </tr>
                                <tr>
                                    <td className="px-4 py-3 font-mono text-green-400">ListenerMiddleware</td>
                                    <td className="px-4 py-3 text-[var(--text-secondary)]">
                                        Lightweight "Saga-like" side effect manager. Listen for actions and run effects.
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    );
};
