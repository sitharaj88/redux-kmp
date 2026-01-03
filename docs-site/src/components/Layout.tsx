import React, { useState, useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Navbar } from './Navbar';
import { Sidebar } from './Sidebar';
import { Footer } from './Footer';

export const Layout: React.FC = () => {
    // Initialize theme from localStorage, default to Light (false) if not set
    const [isDark, setIsDark] = useState(() => {
        try {
            const saved = localStorage.getItem('theme');
            return saved === 'dark';
        } catch (e) {
            return false;
        }
    });

    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const location = useLocation();
    const isDocs = location.pathname.startsWith('/docs');

    // Theme effect & Persistence
    useEffect(() => {
        const root = document.documentElement;
        if (isDark) {
            root.classList.remove('light');
            localStorage.setItem('theme', 'dark');
        } else {
            root.classList.add('light');
            localStorage.setItem('theme', 'light');
        }
    }, [isDark]);

    return (
        <div className="min-h-screen flex flex-col bg-[var(--bg-primary)] text-[var(--text-primary)] transition-colors duration-300">
            <Navbar
                isDark={isDark}
                toggleTheme={() => setIsDark(!isDark)}
                isMobileMenuOpen={isMobileMenuOpen}
                toggleMobileMenu={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            />

            {/* Main Container - Constrained Width */}
            <div className="flex-1 flex pt-16 max-w-7xl mx-auto w-full relative">
                {(isDocs || isMobileMenuOpen) && (
                    <Sidebar
                        isOpen={isMobileMenuOpen}
                        onClose={() => setIsMobileMenuOpen(false)}
                    />
                )}

                <main className={`flex-1 w-full min-w-0 ${isDocs ? 'md:pl-8' : ''}`}>
                    <Outlet />
                </main>
            </div>

            {/* Footer - Full Width */}
            <Footer />
        </div>
    );
};
