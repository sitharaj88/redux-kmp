import React, { useState } from 'react';
import { Check, Copy } from 'lucide-react';

interface CodeBlockProps {
    code: string;
    language?: string;
    title?: string;
}

export const CodeBlock: React.FC<CodeBlockProps> = ({ code, language = 'kotlin', title }) => {
    const [copied, setCopied] = useState(false);

    const handleCopy = async () => {
        await navigator.clipboard.writeText(code);
        setCopied(true);
        setTimeout(() => setCopied(false), 2000);
    };

    // Simple syntax highlighter for Kotlin-like syntax
    const highlightCode = (text: string) => {
        if (!text) return text;

        // Regex for tokens
        // 1. Comments (// ...)
        // 2. Strings ("...")
        // 3. Keywords
        // 4. Types (Capitalized words)
        // 5. Functions (word followed by () - actually hard to split safely with simple regex, skipping for stability)

        const KEYWORDS = "val|var|fun|class|interface|object|return|if|else|when|data|sealed|import|package|override|private|public|internal|const|companion|true|false|null|suspend";
        const regex = new RegExp(`(\\/\\/.*$)|(".*?")|(\\b(?:${KEYWORDS})\\b)|(\\b[A-Z]\\w*\\b)`, 'gm');

        const parts = text.split(regex);

        return parts.filter(part => part !== undefined).map((part, index) => {
            if (!part) return null;

            if (part.startsWith('//')) {
                return <span key={index} className="token-comment">{part}</span>;
            }
            if (part.startsWith('"') && part.endsWith('"')) {
                return <span key={index} className="token-string">{part}</span>;
            }
            if (new RegExp(`^(${KEYWORDS})$`).test(part)) {
                return <span key={index} className="token-keyword">{part}</span>;
            }
            if (/^[A-Z]\w*$/.test(part) && part.length > 1) { // Length > 1 to avoid 'I' or 'T' generics often being types but maybe noisy? Keep it.
                return <span key={index} className="token-type">{part}</span>;
            }

            return part; // Plain text (whitespace, symbols, identifiers)
        });
    };

    return (
        <div className="rounded-lg overflow-hidden border border-[var(--border)] bg-[var(--code-bg)] my-6 shadow-sm transition-all hover:shadow-md">
            {title && (
                <div className="flex items-center justify-between px-4 py-2 border-b border-[var(--border)] bg-[var(--bg-tertiary)]/50 backdrop-blur-sm">
                    <span className="text-xs font-mono text-[var(--text-secondary)] font-medium">
                        {title}
                    </span>
                    <span className="text-xs text-[var(--accent)] font-bold uppercase tracking-wider opacity-60">
                        {language}
                    </span>
                </div>
            )}

            <div className="relative group">
                <button
                    onClick={handleCopy}
                    className="absolute top-3 right-3 p-2 rounded-lg bg-[var(--bg-tertiary)] text-[var(--text-secondary)] 
                             opacity-0 group-hover:opacity-100 transition-all hover:text-[var(--text-primary)] hover:bg-[var(--accent)] hover:text-white"
                    title="Copy code"
                >
                    {copied ? <Check size={16} /> : <Copy size={16} />}
                </button>

                <div className="p-4 overflow-x-auto">
                    <pre>
                        <code>
                            {highlightCode(code)}
                        </code>
                    </pre>
                </div>
            </div>
        </div>
    );
};
