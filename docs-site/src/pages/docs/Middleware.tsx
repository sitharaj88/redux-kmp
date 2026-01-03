import React from 'react';
import { CodeBlock } from '../../components/CodeBlock';

export const Middleware: React.FC = () => {
    const thunkSetup = `val store = createStore(...) {
    addMiddleware(ThunkMiddleware())
}`;

    const loggingSetup = `addMiddleware(LoggingMiddleware(tag = "ReduxApp"))`;

    const listenerSetup = `val listenerMiddleware = createListenerMiddleware<AppState>()

// Add a listener
listenerMiddleware.addListener<UserLoggedIn> { action, api ->
    // Run side effects
    api.dispatch(FetchUserData(action.userId))
}

val store = createStore(...) {
    addMiddleware(listenerMiddleware.middleware)
}`;

    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">Middleware</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8">
                Extend Redux with custom functionality. Redux KMP comes with essential middleware built-in.
            </p>

            <section id="thunk" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">Thunk Middleware</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    The standard way to handle async logic in Redux. Included in the library but must be added to the store manually.
                </p>
                <CodeBlock code={thunkSetup} language="kotlin" title="Setup" />
            </section>

            <section id="logging" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">Logging Middleware</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    Logs every action dispatched and the resulting state change. Useful for debugging.
                </p>
                <CodeBlock code={loggingSetup} language="kotlin" title="Setup" />
            </section>

            <section id="listener" className="mb-12">
                <h2 className="text-2xl font-bold mb-4">Listener Middleware</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    A lightweight alternative to Redux Saga or Observables. Listen for specific actions and run side effects (logic) in response. Perfect for analytics, navigation, or complex flows.
                </p>
                <CodeBlock code={listenerSetup} language="kotlin" title="Listener Setup" />
            </section>
        </div>
    );
};
