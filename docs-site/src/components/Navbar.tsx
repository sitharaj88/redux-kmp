import React from 'react';
import { NavLink } from 'react-router-dom';
import {
    Box,
    Github,
    Menu,
    X,
    Moon,
    Sun
} from 'lucide-react';

interface NavbarProps {
    isDark: boolean;
    toggleTheme: () => void;
    isMobileMenuOpen: boolean;
    toggleMobileMenu: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
    isDark,
    toggleTheme,
    isMobileMenuOpen,
    toggleMobileMenu
}) => {
    return (
        <nav className="fixed top-0 w-full z-50 glass border-b border-[var(--border)]">
            <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
                <NavLink to="/" className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-[var(--accent)] to-[var(--accent-light)] flex items-center justify-center text-white">
                        <Box size={20} strokeWidth={2.5} />
                    </div>
                    <span className="font-bold text-xl tracking-tight">Redux <span className="text-[var(--accent)]">KMP</span></span>
                </NavLink>

                <div className="flex items-center gap-4">
                    {/* Desktop Navigation Links */}
                    <div className="hidden md:flex items-center gap-6">
                        <NavLink
                            to="/docs/intro"
                            className={({ isActive }) => `text-sm font-medium transition-colors ${isActive ? 'text-[var(--text-primary)]' : 'text-[var(--text-secondary)] hover:text-[var(--text-primary)]'}`}
                        >
                            Documentation
                        </NavLink>
                        <a href="https://github.com/sitharaj88/redux-kmp" target="_blank" className="text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-colors flex items-center gap-2 text-sm font-medium">
                            <Github size={18} /> GitHub
                        </a>
                        <div className="h-6 w-px bg-[var(--border)]"></div>
                    </div>

                    {/* Theme Toggle (Visible on Mobile & Desktop) */}
                    <button
                        onClick={toggleTheme}
                        className="theme-toggle"
                        aria-label="Toggle theme"
                    >
                        <div className="theme-toggle-knob text-white">
                            {isDark ? <Moon size={14} /> : <Sun size={14} />}
                        </div>
                    </button>

                    {/* Mobile Menu Button */}
                    <button
                        className="md:hidden p-2 text-[var(--text-primary)] hover:bg-[var(--bg-tertiary)] rounded-lg transition-colors"
                        onClick={toggleMobileMenu}
                        aria-label="Toggle menu"
                    >
                        {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                    </button>
                </div>
            </div>
        </nav>
    );
};
