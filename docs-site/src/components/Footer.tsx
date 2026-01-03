import React from 'react';
import { Linkedin, Globe, Coffee, Heart, Github } from 'lucide-react';

export const Footer: React.FC = () => {
    return (
        <footer className="w-full border-t border-[var(--border)] bg-[var(--bg-secondary)] mt-20 relative overflow-hidden">
            {/* Top Gradient Line */}
            <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-[var(--accent)] to-transparent opacity-50"></div>

            <div className="max-w-7xl mx-auto px-4 py-8 md:py-12">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8 md:gap-12 items-center">

                    {/* Brand / Name */}
                    <div className="text-center md:text-left">
                        <h3 className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[var(--accent)] to-[var(--accent-light)] mb-2">
                            Sitharaj
                        </h3>
                        <p className="text-[var(--text-secondary)] text-sm leading-relaxed max-w-xs mx-auto md:mx-0">
                            Crafting high-quality Kotlin Multiplatform components for the modern developer.
                        </p>
                    </div>

                    {/* Social Links */}
                    <div className="flex flex-col items-center gap-4">
                        <div className="flex justify-center gap-4">
                            <SocialLink href="https://linkedin.com/in/sitharaj08" icon={Linkedin} label="LinkedIn" />
                            <SocialLink href="https://sitharaj.in" icon={Globe} label="Website" />
                            <SocialLink href="https://github.com/sitharaj88" icon={Github} label="Github" />
                        </div>

                        {/* Real "Buy Me A Coffee" Button */}
                        <a
                            href="https://buymeacoffee.com/sitharaj88"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="flex items-center gap-2 bg-[#FFDD00] text-black px-4 py-2 rounded-full font-bold font-[Poppins,sans-serif] text-sm transition-transform hover:scale-105 hover:shadow-lg hover:shadow-yellow-500/20"
                        >
                            <Coffee size={18} strokeWidth={2.5} />
                            <span>Buy me a coffee</span>
                        </a>
                    </div>

                    {/* Fun / Stack */}
                    <div className="text-center md:text-right">
                        <p className="text-[var(--text-muted)] text-sm mb-2 flex items-center justify-center md:justify-end gap-1">
                            Made with <Heart size={14} className="text-red-500 animate-pulse" /> using
                        </p>
                        <div className="flex items-center justify-center md:justify-end gap-3 text-sm font-mono text-[var(--accent-light)]">
                            <span>Kotlin</span>
                            <span className="text-[var(--text-muted)]">•</span>
                            <span>React</span>
                            <span className="text-[var(--text-muted)]">•</span>
                            <span>Vite</span>
                        </div>
                    </div>
                </div>

                <div className="mt-12 pt-8 border-t border-[var(--border)] text-center text-[var(--text-muted)] text-xs">
                    &copy; {new Date().getFullYear()} Sitharaj. Open Source under Apache 2.0.
                </div>
            </div>
        </footer>
    );
};

const SocialLink = ({ href, icon: Icon, label }: { href: string; icon: any; label: string }) => (
    <a
        href={href}
        target="_blank"
        rel="noopener noreferrer"
        className="p-3 rounded-full transition-all duration-300 group relative bg-[var(--bg-tertiary)] text-[var(--text-secondary)] hover:text-[var(--accent)] hover:bg-[var(--bg-primary)] hover:scale-110 border border-transparent hover:border-[var(--border)]"
        aria-label={label}
    >
        <Icon size={20} strokeWidth={2} />
        <span className="absolute -bottom-8 left-1/2 -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity text-xs whitespace-nowrap bg-[var(--bg-tertiary)] px-2 py-1 rounded text-[var(--text-primary)] border border-[var(--border)] pointer-events-none z-10">
            {label}
        </span>
    </a>
);
