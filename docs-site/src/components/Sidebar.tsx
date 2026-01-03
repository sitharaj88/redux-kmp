import React from 'react';
import { NavLink } from 'react-router-dom';
import {
    BookOpen,
    Layers,
    Zap,
    Smartphone,
    Terminal,
    Cpu
} from 'lucide-react';

export const Sidebar: React.FC<{ isOpen: boolean; onClose: () => void }> = ({ isOpen, onClose }) => {
    const linkClass = ({ isActive }: { isActive: boolean }) => `
    flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200
    ${isActive
            ? 'bg-[var(--accent)]/10 text-[var(--accent)] border-l-2 border-[var(--accent)]'
            : 'text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-[var(--bg-tertiary)]'}
  `;

    return (
        <aside className={`
      fixed lg:sticky top-16 left-0 h-[calc(100vh-4rem)] w-72 bg-[var(--bg-primary)] border-r border-[var(--border)] 
      overflow-y-auto z-40 transition-transform duration-300 ease-in-out
      ${isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
    `}>
            <div className="p-4 space-y-8 pb-20">

                {/* Getting Started */}
                <div>
                    <h3 className="flex items-center gap-2 text-xs font-bold text-[var(--text-muted)] uppercase tracking-wider mb-3 px-3">
                        <BookOpen size={14} /> Getting Started
                    </h3>
                    <div className="space-y-1">
                        <NavLink to="/docs/intro" className={linkClass} onClick={onClose}>Introduction</NavLink>
                        <NavLink to="/docs/installation" className={linkClass} onClick={onClose}>Installation</NavLink>
                        <NavLink to="/docs/quick-start" className={linkClass} onClick={onClose}>Quick Start</NavLink>
                    </div>
                </div>

                {/* Fundamentals */}
                <div>
                    <h3 className="flex items-center gap-2 text-xs font-bold text-[var(--text-muted)] uppercase tracking-wider mb-3 px-3">
                        <Layers size={14} /> Core Concepts
                    </h3>
                    <div className="space-y-1">
                        <NavLink to="/docs/core/store" className={linkClass} onClick={onClose}>Store & State</NavLink>
                        <NavLink to="/docs/core/actions" className={linkClass} onClick={onClose}>Actions & Reducers</NavLink>
                    </div>
                </div>

                {/* Toolkit */}
                <div>
                    <h3 className="flex items-center gap-2 text-xs font-bold text-[var(--text-muted)] uppercase tracking-wider mb-3 px-3">
                        <Cpu size={14} /> Redux Toolkit KMP
                    </h3>
                    <div className="space-y-1">
                        <NavLink to="/docs/toolkit/create-slice" className={linkClass} onClick={onClose}>createSlice</NavLink>
                        <NavLink to="/docs/toolkit/async-thunk" className={linkClass} onClick={onClose}>createAsyncThunk</NavLink>
                        <NavLink to="/docs/toolkit/entity-adapter" className={linkClass} onClick={onClose}>createEntityAdapter</NavLink>
                        <NavLink to="/docs/toolkit/selectors" className={linkClass} onClick={onClose}>createSelector</NavLink>
                    </div>
                </div>

                {/* Middleware */}
                <div>
                    <h3 className="flex items-center gap-2 text-xs font-bold text-[var(--text-muted)] uppercase tracking-wider mb-3 px-3">
                        <Zap size={14} /> Middleware
                    </h3>
                    <div className="space-y-1">
                        <NavLink to="/docs/middleware/thunk" className={linkClass} onClick={onClose}>Thunk Middleware</NavLink>
                        <NavLink to="/docs/middleware/listener" className={linkClass} onClick={onClose}>Listener Middleware</NavLink>
                        <NavLink to="/docs/middleware/logging" className={linkClass} onClick={onClose}>Logging</NavLink>
                    </div>
                </div>

                {/* Platform Integration */}
                <div>
                    <h3 className="flex items-center gap-2 text-xs font-bold text-[var(--text-muted)] uppercase tracking-wider mb-3 px-3">
                        <Smartphone size={14} /> Platforms
                    </h3>
                    <div className="space-y-1">
                        <NavLink to="/docs/platforms/compose" className={linkClass} onClick={onClose}>Jetpack Compose</NavLink>
                        <NavLink to="/docs/platforms/ios" className={linkClass} onClick={onClose}>iOS (SwiftUI/UIKit)</NavLink>
                    </div>
                </div>

                {/* API */}
                <div>
                    <h3 className="flex items-center gap-2 text-xs font-bold text-[var(--text-muted)] uppercase tracking-wider mb-3 px-3">
                        <Terminal size={14} /> Reference
                    </h3>
                    <div className="space-y-1">
                        <NavLink to="/docs/api" className={linkClass} onClick={onClose}>Full API Index</NavLink>
                    </div>
                </div>

            </div>
        </aside>
    );
};
