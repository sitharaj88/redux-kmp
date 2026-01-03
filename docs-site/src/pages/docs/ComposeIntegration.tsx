import React from 'react';
import { CodeBlock } from '../../components/CodeBlock';

export const ComposeIntegration: React.FC = () => {
    const rootSetup = `// App.kt
import \`in\`.sitharaj.reduxkmp.sample.chat.ChatStoreProvider

@Composable
fun App() {
    // Obtain the store singleton
    val store = remember { ChatStoreProvider.getStore() }
    
    // Initialize if needed
    LaunchedEffect(Unit) {
        ChatStoreProvider.initialize(store)
    }

    // Pass store down or use Provider pattern (if available)
    ChatScreen(store = store)
}`;

    const usageCode = `// ChatScreen.kt
import androidx.compose.runtime.collectAsState

@Composable
fun ChatScreen(store: Store<ChatState>) {
    // 1. Subscribe to State Flow
    val state by store.state.collectAsState()
    
    // 2. Derive data (Selectors)
    val unreadCount = selectUnreadCount(state)
    
    // 3. Dispatch Actions
    Button(
        onClick = { store.dispatch(SendMessage(text)) }
    ) {
        Text("Send")
    }
}`;

    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">Jetpack Compose Integration</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8">
                Redux KMP exposes state as a standard <code>StateFlow</code>, making it trivial to use with Compose.
            </p>

            <h2 className="text-2xl font-bold mb-4 mt-8">Root Setup</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                Initialize the store at the root of your Compose hierarchy.
            </p>
            <CodeBlock code={rootSetup} language="kotlin" title="App.kt" />

            <h2 className="text-2xl font-bold mb-4 mt-8">Basic Usage</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                Use the standard <code>collectAsState()</code> extension function from Compose Runtime.
            </p>
            <CodeBlock code={usageCode} language="kotlin" title="Composing State" />

            <h2 className="text-2xl font-bold mb-4 mt-8">Provider Pattern</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                For larger apps, we recommend using a dependency injection framework (like Koin) or a CompositionLocal to provide the Store to your composable tree, avoiding prop drilling.
            </p>
        </div>
    );
};
