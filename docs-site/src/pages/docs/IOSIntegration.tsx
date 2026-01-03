import React from 'react';
import { CodeBlock } from '../../components/CodeBlock';

export const IOSIntegration: React.FC = () => {
    const composeCode = `// MainViewController.kt (iosMain)
fun MainViewController() = ComposeUIViewController {
    App() // Your shared Compose App
}`;

    const swiftCode = `// iOS App (Swift)
import SwiftUI
import Shared

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}`;

    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">iOS Integration</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8">
                Redux KMP works seamlessly on iOS, either through Compose Multiplatform or by exposing the Store to native Swift code.
            </p>

            <section className="mb-12">
                <h2 className="text-2xl font-bold mb-4">Option 1: Compose Multiplatform (Recommended)</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    If you share your UI with Compose, the integration matches the Android/Desktop approach completely. The `Store` guides the UI state, and Compose renders it.
                </p>
                <CodeBlock code={composeCode} language="kotlin" title="Kotlin (iosMain)" />
                <CodeBlock code={swiftCode} language="swift" title="Swift" />
            </section>

            <section className="mb-12">
                <h2 className="text-2xl font-bold mb-4">Option 2: Native SwiftUI</h2>
                <p className="text-[var(--text-secondary)] mb-4">
                    You can consume the Kotlin `StateFlow` in Swift. Use a wrapper or library like `SKIE` or `KMP-Native-Coroutines` to observe the flow efficiently in SwiftUI Views.
                </p>
            </section>
        </div>
    );
};
