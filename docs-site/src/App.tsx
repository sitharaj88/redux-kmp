
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/Layout';
import { Home } from './pages/Home';
import { Introduction } from './pages/docs/Introduction';
import { Installation } from './pages/docs/Installation';
import { QuickStart } from './pages/docs/QuickStart';
import { ComposeIntegration } from './pages/docs/ComposeIntegration';
import { APIReference } from './pages/docs/APIReference';
import { CoreConcepts } from './pages/docs/CoreConcepts';
import { ReduxToolkit } from './pages/docs/ReduxToolkit';
import { Middleware } from './pages/docs/Middleware';
import { IOSIntegration } from './pages/docs/IOSIntegration';

// Placeholder for remaining missing pages (if any)
const PlaceholderDocs = () => {
    return (
        <div className="px-4 py-8 max-w-4xl animate-fade-in">
            <h1 className="text-3xl font-bold mb-4">Coming Soon</h1>
            <p className="text-[var(--text-secondary)]">
                Detailed documentation for this section is currently being written.
                Check back shortly!
            </p>
        </div>
    );
};

export default function App() {
    return (
        <BrowserRouter basename={import.meta.env.BASE_URL}>
            <Routes>
                <Route path="/" element={<Layout />}>
                    <Route index element={<Home />} />

                    {/* Docs Routes */}
                    <Route path="docs">
                        <Route index element={<Navigate to="intro" replace />} />
                        <Route path="intro" element={<Introduction />} />
                        <Route path="installation" element={<Installation />} />
                        <Route path="quick-start" element={<QuickStart />} />

                        {/* Core Concepts */}
                        <Route path="core/store" element={<CoreConcepts />} />
                        <Route path="core/actions" element={<CoreConcepts />} />

                        {/* Toolkit */}
                        <Route path="toolkit/create-slice" element={<ReduxToolkit />} />
                        <Route path="toolkit/async-thunk" element={<ReduxToolkit />} />
                        <Route path="toolkit/entity-adapter" element={<ReduxToolkit />} />
                        <Route path="toolkit/selectors" element={<ReduxToolkit />} />

                        {/* Middleware */}
                        <Route path="middleware/thunk" element={<Middleware />} />
                        <Route path="middleware/listener" element={<Middleware />} />
                        <Route path="middleware/logging" element={<Middleware />} />

                        {/* Platforms */}
                        <Route path="platforms/compose" element={<ComposeIntegration />} />
                        <Route path="platforms/ios" element={<IOSIntegration />} />

                        <Route path="api" element={<APIReference />} />

                        {/* Catch-all */}
                        <Route path="*" element={<PlaceholderDocs />} />
                    </Route>
                </Route>
            </Routes>
        </BrowserRouter>
    );
}
