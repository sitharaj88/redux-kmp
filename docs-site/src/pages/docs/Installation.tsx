import React from 'react';
import { CodeBlock } from '../../components/CodeBlock';

export const Installation: React.FC = () => {
    const gradleKts = `// commonMain resources
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Redux KMP Core
                implementation("in.sitharaj.reduxkmp:redux-kmp:1.0.0")

                // Kotlin Coroutines (Required)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            }
        }
    }
}`;

    const versionCatalog = `[versions]
redux-kmp = "1.0.0"

[libraries]
redux-kmp = { module = "in.sitharaj.reduxkmp:redux-kmp", version.ref = "redux-kmp" }`;

    const usageCatalog = `// build.gradle.kts
dependencies {
    implementation(libs.redux.kmp)
}`;

    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">Installation</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8 leading-relaxed">
                Add Redux KMP to your Kotlin Multiplatform project.
            </p>

            <h2 className="text-2xl font-bold mb-4 mt-8">Using Gradle (Kotlin DSL)</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                Add the dependency to your <code>commonMain</code> source set.
            </p>
            <CodeBlock code={gradleKts} language="kotlin" title="build.gradle.kts" />

            <h2 className="text-2xl font-bold mb-4 mt-8">Using Version Catalog</h2>
            <p className="text-[var(--text-secondary)] mb-4">
                Recommended approach for modern Gradle projects.
            </p>
            <CodeBlock code={versionCatalog} language="toml" title="libs.versions.toml" />
            <CodeBlock code={usageCatalog} language="kotlin" title="build.gradle.kts" />

            <div className="bg-[var(--bg-secondary)] border-l-4 border-yellow-500 p-4 rounded-r-lg mt-8">
                <p className="text-yellow-500 font-bold mb-1">Snapshot Builds</p>
                <p className="text-[var(--text-secondary)] text-sm">
                    If you are using a local build or snapshot, ensure `mavenLocal()` is included in your repositories block in `settings.gradle.kts`.
                </p>
            </div>
        </div>
    );
};
