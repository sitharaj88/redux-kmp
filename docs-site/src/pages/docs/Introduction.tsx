import React from 'react';

export const Introduction: React.FC = () => {
    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-4xl font-extrabold mb-6">Introduction</h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8 leading-relaxed">
                Redux KMP is a predictable state container for Kotlin Multiplatform applications,
                heavily inspired by the popular <strong>Redux Toolkit (RTK)</strong> for JavaScript.
            </p>

            <h2 className="text-2xl font-bold mb-4 mt-12">What is Redux?</h2>
            <p className="text-[var(--text-secondary)] mb-4 leading-relaxed">
                Redux is a pattern and library for managing and updating application state, using events called "actions".
                It serves as a centralized store for state that needs to be used across your entire application,
                with rules ensuring that the state can only be updated in a predictable fashion.
            </p>

            <h2 className="text-2xl font-bold mb-4 mt-12">Why Redux KMP?</h2>
            <ul className="list-disc pl-6 space-y-3 text-[var(--text-secondary)] mb-8">
                <li>
                    <strong className="text-[var(--text-primary)]">Write Once, Run Everywhere:</strong> Define your business logic, state, and side effects in common Kotlin code. Share it across Android, iOS, Desktop, and Web.
                </li>
                <li>
                    <strong className="text-[var(--text-primary)]">Type Safety:</strong> Unlike JS Redux, Redux KMP leverages Kotlin's type system to prevent common errors like accessing missing properties or dispatching invalid actions.
                </li>
                <li>
                    <strong className="text-[var(--text-primary)]">Coroutines Support:</strong> Native support for Kotlin Coroutines via Thunks and Middleware. Async logic is first-class.
                </li>
            </ul>

            <h2 className="text-2xl font-bold mb-4 mt-12">Architecture</h2>
            <div className="card p-6 bg-[var(--bg-secondary)] mb-8">
                <div className="flex justify-center">
                    {/* Simple SVG Diagram */}
                    <svg width="100%" height="200" viewBox="0 0 600 200" className="opacity-80">
                        <rect x="50" y="75" width="100" height="50" rx="4" fill="var(--accent)" fillOpacity="0.2" stroke="var(--accent)" strokeWidth="2" />
                        <text x="100" y="105" textAnchor="middle" fill="currentColor" fontWeight="bold">Action</text>

                        <path d="M150 100 L250 100" stroke="var(--text-muted)" strokeWidth="2" markerEnd="url(#arrow)" />

                        <rect x="250" y="75" width="100" height="50" rx="4" fill="var(--accent)" fillOpacity="0.2" stroke="var(--accent)" strokeWidth="2" />
                        <text x="300" y="105" textAnchor="middle" fill="currentColor" fontWeight="bold">Reducer</text>

                        <path d="M350 100 L450 100" stroke="var(--text-muted)" strokeWidth="2" markerEnd="url(#arrow)" />

                        <rect x="450" y="75" width="100" height="50" rx="4" fill="var(--accent)" fillOpacity="0.2" stroke="var(--accent)" strokeWidth="2" />
                        <text x="500" y="105" textAnchor="middle" fill="currentColor" fontWeight="bold">Store</text>

                        <path d="M500 125 L500 160 L100 160 L100 125" stroke="var(--text-muted)" strokeWidth="2" fill="none" strokeDasharray="5,5" />
                        <text x="300" y="175" textAnchor="middle" fill="currentColor" fontSize="12">UI Updates / Dispatch</text>

                        <defs>
                            <marker id="arrow" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto" markerUnits="strokeWidth">
                                <path d="M0,0 L0,6 L9,3 z" fill="var(--text-muted)" />
                            </marker>
                        </defs>
                    </svg>
                </div>
            </div>
        </div>
    );
};
