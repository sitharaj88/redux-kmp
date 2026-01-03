import React from 'react';
import { NavLink } from 'react-router-dom';
import { ChevronRight, Github, Layers, Code, Zap } from 'lucide-react';

export const Home: React.FC = () => {
    return (
        <div className="px-4 md:px-12 py-12">
            {/* Hero Section */}
            <section className="min-h-[60vh] flex flex-col justify-center animate-fade-in text-center items-center">
                <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[var(--accent)]/10 text-[var(--accent)] w-fit mb-6 border border-[var(--accent)]/20">
                    <span className="relative flex h-2 w-2">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[var(--accent)] opacity-75"></span>
                        <span className="relative inline-flex rounded-full h-2 w-2 bg-[var(--accent)]"></span>
                    </span>
                    <span className="text-xs font-bold uppercase tracking-wider">v1.0.0 Now Available</span>
                </div>

                <h1 className="text-5xl md:text-7xl font-extrabold mb-6 leading-tight max-w-4xl">
                    State Management for <br />
                    <span className="text-transparent bg-clip-text bg-gradient-to-r from-[var(--accent)] to-purple-400 animate-gradient">
                        Kotlin Multiplatform
                    </span>
                </h1>

                <p className="text-xl text-[var(--text-secondary)] max-w-2xl mb-10 leading-relaxed">
                    Predictable state container for KMP apps. Typesafe, Multiplatform, and Compose-ready.
                    Write comprehensive business logic once, run everywhere.
                </p>

                <div className="flex flex-wrap gap-4 justify-center">
                    <NavLink
                        to="/docs/intro"
                        className="btn-primary flex items-center gap-2"
                    >
                        Get Started <ChevronRight size={18} />
                    </NavLink>
                    <a
                        href="https://github.com/sitharaj88/redux-kmp"
                        target="_blank"
                        className="btn-secondary flex items-center gap-2"
                    >
                        <Github size={18} /> View on GitHub
                    </a>
                </div>

                {/* Features Cloud */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-20 text-left max-w-5xl w-full">
                    <FeatureCard
                        icon={Layers}
                        title="Cross Platform"
                        description="Share your Store, Reducers, and Middleware across Android, iOS, Desktop, and Web (JS/Wasm)."
                    />
                    <FeatureCard
                        icon={Code}
                        title="Type Safe"
                        description="Leverage Kotlin's strong type system. Catch state errors at compile time."
                    />
                    <FeatureCard
                        icon={Zap}
                        title="Async Thunks"
                        description="Integrated Coroutines support for async actions. Handle loading/success/error states declaratively."
                    />
                </div>
            </section>
        </div>
    );
};

const FeatureCard = ({ icon: Icon, title, description }: { icon: any; title: string; description: string }) => (
    <div className="card p-6 h-full flex flex-col items-start gap-4 animate-fade-in">
        <div className="p-3 rounded-xl bg-[var(--accent)]/10 text-[var(--accent)]">
            <Icon size={24} strokeWidth={1.5} />
        </div>
        <div>
            <h3 className="text-xl font-bold mb-2 text-[var(--text-primary)]">{title}</h3>
            <p className="text-[var(--text-secondary)] leading-relaxed">{description}</p>
        </div>
    </div>
);
