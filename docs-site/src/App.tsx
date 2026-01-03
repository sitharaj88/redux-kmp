import { useState, useEffect } from 'react'
import { Github, Package, BookOpen, Zap, Copy, Check, Menu, X, Terminal, Sun, Moon, ChevronDown, ExternalLink, Layers, Database, RefreshCw, ListFilter, Radio, FileCode, MessageSquare } from 'lucide-react'

function App() {
    const [copiedIndex, setCopiedIndex] = useState<number | null>(null)
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
    const [activeTab, setActiveTab] = useState<'store' | 'reducer' | 'thunk' | 'selector'>('store')
    const [toolkitTab, setToolkitTab] = useState<'selector' | 'thunk' | 'entity' | 'listener' | 'slice'>('selector')
    const [isDark, setIsDark] = useState(true)
    const [activeSection, setActiveSection] = useState('')

    useEffect(() => {
        const savedTheme = localStorage.getItem('theme')
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
        const shouldBeDark = savedTheme ? savedTheme === 'dark' : prefersDark
        setIsDark(shouldBeDark)
        document.documentElement.classList.toggle('light', !shouldBeDark)
    }, [])

    const toggleTheme = () => {
        const newIsDark = !isDark
        setIsDark(newIsDark)
        document.documentElement.classList.toggle('light', !newIsDark)
        localStorage.setItem('theme', newIsDark ? 'dark' : 'light')
    }

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        setActiveSection(entry.target.id)
                    }
                })
            },
            { threshold: 0.3 }
        )
        document.querySelectorAll('section[id]').forEach((section) => {
            observer.observe(section)
        })
        return () => observer.disconnect()
    }, [])

    const copyToClipboard = (text: string, index: number) => {
        navigator.clipboard.writeText(text)
        setCopiedIndex(index)
        setTimeout(() => setCopiedIndex(null), 2000)
    }

    const CodeBlock = ({ code, index, title, lang = 'kotlin' }: { code: string; index: number; title?: string; lang?: string }) => (
        <div className="card overflow-hidden">
            {title && (
                <div className="flex items-center justify-between px-4 py-3 border-b" style={{ borderColor: 'var(--border)', background: 'var(--bg-tertiary)' }}>
                    <div className="flex items-center gap-2">
                        <span className="text-xs font-mono px-2 py-1 rounded" style={{ background: 'var(--accent)', color: 'white' }}>{lang}</span>
                        <span className="font-medium text-sm" style={{ color: 'var(--text-secondary)' }}>{title}</span>
                    </div>
                    <button
                        onClick={() => copyToClipboard(code, index)}
                        className="flex items-center gap-2 transition text-sm hover:opacity-80"
                        style={{ color: 'var(--text-muted)' }}
                    >
                        {copiedIndex === index ? (
                            <><Check className="w-4 h-4 text-green-500" /> Copied!</>
                        ) : (
                            <><Copy className="w-4 h-4" /> Copy</>
                        )}
                    </button>
                </div>
            )}
            <pre className="p-4 text-sm overflow-x-auto" style={{ background: 'var(--bg-secondary)' }}>
                <code style={{ color: 'var(--accent-light)' }}>{code}</code>
            </pre>
        </div>
    )

    const NavLink = ({ href, children }: { href: string; children: React.ReactNode }) => (
        <a
            href={href}
            className={`text-sm font-medium transition-colors ${activeSection === href.slice(1) ? 'text-[var(--accent)]' : 'hover:text-[var(--accent)]'}`}
            style={{ color: activeSection === href.slice(1) ? 'var(--accent)' : 'var(--text-secondary)' }}
        >
            {children}
        </a>
    )

    return (
        <div className="min-h-screen transition-colors" style={{ background: 'var(--bg-primary)' }}>
            {/* Navigation */}
            <nav className="fixed top-0 left-0 right-0 z-50 glass border-b" style={{ borderColor: 'var(--border)' }}>
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        <a href="#" className="flex items-center gap-3 group">
                            <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-pink-600 rounded-xl flex items-center justify-center shadow-lg shadow-purple-500/25 group-hover:shadow-purple-500/40 transition-shadow">
                                <Layers className="w-5 h-5 text-white" />
                            </div>
                            <span className="font-bold text-xl" style={{ color: 'var(--text-primary)' }}>Redux KMP</span>
                        </a>

                        <div className="hidden lg:flex items-center gap-6">
                            <NavLink href="#getting-started">Getting Started</NavLink>
                            <NavLink href="#core-concepts">Core Concepts</NavLink>
                            <NavLink href="#toolkit">Toolkit</NavLink>
                            <NavLink href="#compose">Compose</NavLink>
                            <NavLink href="#sample-app">Sample App</NavLink>
                            <NavLink href="#api">API</NavLink>
                        </div>

                        <div className="flex items-center gap-4">
                            <button onClick={toggleTheme} className="theme-toggle" aria-label="Toggle theme">
                                <div className="theme-toggle-knob">
                                    {isDark ? <Moon className="w-4 h-4 text-white" /> : <Sun className="w-4 h-4 text-white" />}
                                </div>
                            </button>
                            <a href="https://github.com/sitharaj88/kmp-starter" target="_blank" rel="noopener noreferrer" className="hidden sm:flex items-center gap-2 btn-secondary text-sm">
                                <Github className="w-4 h-4" /> GitHub
                            </a>
                            <button className="lg:hidden p-2" onClick={() => setMobileMenuOpen(!mobileMenuOpen)} style={{ color: 'var(--text-primary)' }}>
                                {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
                            </button>
                        </div>
                    </div>
                </div>

                {mobileMenuOpen && (
                    <div className="lg:hidden border-t animate-fade-in" style={{ background: 'var(--bg-secondary)', borderColor: 'var(--border)' }}>
                        <div className="px-4 py-4 space-y-4">
                            {['Getting Started', 'Core Concepts', 'Toolkit', 'Compose', 'Sample App', 'API'].map((item) => (
                                <a key={item} href={`#${item.toLowerCase().replace(' ', '-')}`} className="block py-2 font-medium" style={{ color: 'var(--text-secondary)' }} onClick={() => setMobileMenuOpen(false)}>
                                    {item}
                                </a>
                            ))}
                        </div>
                    </div>
                )}
            </nav>

            {/* Hero Section */}
            <section className="pt-28 pb-20 px-4 relative overflow-hidden">
                <div className="absolute inset-0 overflow-hidden pointer-events-none">
                    <div className="absolute -top-40 -right-40 w-96 h-96 bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-full blur-3xl" />
                    <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-gradient-to-br from-blue-500/20 to-cyan-500/20 rounded-full blur-3xl" />
                </div>

                <div className="max-w-5xl mx-auto text-center relative z-10">
                    <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full mb-8 animate-fade-in" style={{ background: 'var(--bg-secondary)', border: '1px solid var(--border)' }}>
                        <Zap className="w-4 h-4" style={{ color: 'var(--accent)' }} />
                        <span className="text-sm font-medium" style={{ color: 'var(--text-secondary)' }}>Redux Toolkit for Kotlin Multiplatform</span>
                    </div>

                    <h1 className="text-5xl md:text-7xl font-extrabold mb-6 animate-fade-in bg-gradient-to-r from-purple-400 via-pink-400 to-red-400 bg-clip-text text-transparent animate-gradient">
                        Predictable State
                        <br />for KMP Apps
                    </h1>

                    <p className="text-lg md:text-xl mb-10 max-w-2xl mx-auto animate-fade-in" style={{ color: 'var(--text-secondary)', animationDelay: '0.1s' }}>
                        Type-safe Redux with Kotlin DSL, Compose integration, and all modern Redux Toolkit features:
                        Selectors, AsyncThunk, EntityAdapter, ListenerMiddleware, and Slices.
                    </p>

                    <div className="flex flex-col sm:flex-row gap-4 justify-center animate-fade-in" style={{ animationDelay: '0.2s' }}>
                        <a href="#getting-started" className="btn-primary inline-flex items-center justify-center gap-2 px-8 py-4 text-lg">
                            <BookOpen className="w-5 h-5" /> Get Started
                            <ChevronDown className="w-4 h-4 opacity-50" />
                        </a>
                        <a href="https://github.com/sitharaj88/kmp-starter" target="_blank" rel="noopener noreferrer" className="btn-secondary inline-flex items-center justify-center gap-2 px-8 py-4 text-lg">
                            <Github className="w-5 h-5" /> View on GitHub
                            <ExternalLink className="w-4 h-4 opacity-50" />
                        </a>
                    </div>
                </div>
            </section>

            {/* Feature Grid */}
            <section className="py-16 px-4">
                <div className="max-w-6xl mx-auto">
                    <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {[
                            { icon: <Layers className="w-6 h-6" />, title: 'Kotlin DSL', description: 'Type-safe reducers, middleware, and store configuration using idiomatic Kotlin DSLs.', color: 'from-purple-400 to-purple-600' },
                            { icon: <RefreshCw className="w-6 h-6" />, title: 'StateFlow Integration', description: 'Reactive state powered by Kotlin Coroutines and StateFlow for automatic updates.', color: 'from-blue-400 to-blue-600' },
                            { icon: <Database className="w-6 h-6" />, title: 'Entity Adapter', description: 'Normalized CRUD operations with selectAll, selectById, and automatic ID tracking.', color: 'from-green-400 to-green-600' },
                            { icon: <ListFilter className="w-6 h-6" />, title: 'Memoized Selectors', description: 'createSelector with automatic caching - recomputes only when inputs change.', color: 'from-yellow-400 to-orange-500' },
                            { icon: <Radio className="w-6 h-6" />, title: 'Listener Middleware', description: 'Side-effect handling with addListener, predicate matching, and async effects.', color: 'from-pink-400 to-pink-600' },
                            { icon: <FileCode className="w-6 h-6" />, title: 'Async Thunks', description: 'createAsyncThunk with pending/fulfilled/rejected lifecycle actions.', color: 'from-cyan-400 to-cyan-600' },
                        ].map((feature, i) => (
                            <div key={i} className="card p-6 group hover:border-[var(--accent)] transition-colors">
                                <div className={`w-12 h-12 bg-gradient-to-br ${feature.color} rounded-xl flex items-center justify-center mb-4 text-white`}>
                                    {feature.icon}
                                </div>
                                <h3 className="text-xl font-bold mb-2" style={{ color: 'var(--text-primary)' }}>{feature.title}</h3>
                                <p style={{ color: 'var(--text-secondary)' }}>{feature.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Getting Started */}
            <section id="getting-started" className="py-20 px-4" style={{ background: 'var(--bg-secondary)' }}>
                <div className="max-w-4xl mx-auto">
                    <div className="text-center mb-12">
                        <div className="inline-flex items-center gap-2 mb-4" style={{ color: 'var(--accent)' }}>
                            <Terminal className="w-5 h-5" />
                            <span className="text-sm font-semibold uppercase tracking-wider">Getting Started</span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-bold" style={{ color: 'var(--text-primary)' }}>Quick Start</h2>
                    </div>

                    <div className="space-y-6">
                        <CodeBlock title="1. Add Dependency" code={`// build.gradle.kts
dependencies {
    implementation("com.sitharaj:redux-kmp:1.0.0")
}`} index={0} />

                        <CodeBlock title="2. Define State" code={`// State must implement the State interface
data class CounterState(
    val count: Int = 0,
    val loading: Boolean = false
) : State`} index={1} />

                        <CodeBlock title="3. Define Actions" code={`sealed interface CounterAction : Action {
    data object Increment : CounterAction
    data object Decrement : CounterAction
    data class SetCount(val value: Int) : CounterAction
}`} index={2} />

                        <CodeBlock title="4. Create Reducer" code={`val counterReducer = reducer<CounterState> {
    on<CounterAction.Increment> { state, _ ->
        state.copy(count = state.count + 1)
    }
    on<CounterAction.Decrement> { state, _ ->
        state.copy(count = state.count - 1)
    }
    on<CounterAction.SetCount> { state, action ->
        state.copy(count = action.value)
    }
}`} index={3} />

                        <CodeBlock title="5. Create Store" code={`val store = createStore(
    initialState = CounterState(),
    reducer = counterReducer,
    scope = CoroutineScope(Dispatchers.Main)
) {
    addMiddleware(ThunkMiddleware())
    addMiddleware(LoggingMiddleware(tag = "Counter"))
}`} index={4} />

                        <CodeBlock title="6. Dispatch Actions" code={`// Dispatch sync actions
store.dispatch(CounterAction.Increment)

// Observe state changes
store.state.collect { state ->
    println("Count: \${state.count}")
}`} index={5} />
                    </div>
                </div>
            </section>

            {/* Core Concepts */}
            <section id="core-concepts" className="py-20 px-4">
                <div className="max-w-4xl mx-auto">
                    <div className="text-center mb-12">
                        <div className="inline-flex items-center gap-2 mb-4" style={{ color: 'var(--accent)' }}>
                            <Layers className="w-5 h-5" />
                            <span className="text-sm font-semibold uppercase tracking-wider">Core Concepts</span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-bold" style={{ color: 'var(--text-primary)' }}>Building Blocks</h2>
                    </div>

                    {/* Tabs */}
                    <div className="flex flex-wrap gap-2 mb-8">
                        {(['store', 'reducer', 'thunk', 'selector'] as const).map((tab) => (
                            <button
                                key={tab}
                                onClick={() => setActiveTab(tab)}
                                className={`px-6 py-3 rounded-xl font-semibold transition capitalize ${activeTab === tab ? 'btn-primary' : 'btn-secondary'}`}
                            >
                                {tab}
                            </button>
                        ))}
                    </div>

                    <div className="space-y-6">
                        {activeTab === 'store' && (
                            <>
                                <div className="card p-6">
                                    <h3 className="font-bold text-lg mb-4" style={{ color: 'var(--text-primary)' }}>Store</h3>
                                    <p style={{ color: 'var(--text-secondary)' }}>
                                        The Store holds the entire state tree of your application. It's created using <code style={{ color: 'var(--accent)' }}>createStore()</code> and provides:
                                    </p>
                                    <ul className="list-disc list-inside mt-4 space-y-2" style={{ color: 'var(--text-secondary)' }}>
                                        <li><code style={{ color: 'var(--accent)' }}>state</code> - StateFlow of current state</li>
                                        <li><code style={{ color: 'var(--accent)' }}>dispatch()</code> - Send actions to update state</li>
                                        <li><code style={{ color: 'var(--accent)' }}>currentState</code> - Get state synchronously</li>
                                    </ul>
                                </div>
                                <CodeBlock title="Store Configuration" code={`val store = createStore<AppState>(
    initialState = AppState(),
    reducer = rootReducer,
    scope = coroutineScope
) {
    // Add middleware in order
    addMiddleware(ThunkMiddleware())
    addMiddleware(LoggingMiddleware())
    
    // Optional: Add enhancers
    // addEnhancer { ... }
}`} index={10} />
                            </>
                        )}

                        {activeTab === 'reducer' && (
                            <>
                                <div className="card p-6">
                                    <h3 className="font-bold text-lg mb-4" style={{ color: 'var(--text-primary)' }}>Reducer DSL</h3>
                                    <p style={{ color: 'var(--text-secondary)' }}>
                                        Reducers specify how state changes in response to actions. The DSL provides type-safe action handling with <code style={{ color: 'var(--accent)' }}>on&lt;Action&gt;</code>.
                                    </p>
                                </div>
                                <CodeBlock title="Reducer Pattern" code={`val todoReducer = reducer<TodoState> {
    on<AddTodo> { state, action ->
        state.copy(
            todos = state.todos + action.todo
        )
    }
    
    on<ToggleTodo> { state, action ->
        state.copy(
            todos = state.todos.map { todo ->
                if (todo.id == action.id) 
                    todo.copy(completed = !todo.completed)
                else todo
            }
        )
    }
    
    on<RemoveTodo> { state, action ->
        state.copy(
            todos = state.todos.filter { it.id != action.id }
        )
    }
}`} index={11} />
                            </>
                        )}

                        {activeTab === 'thunk' && (
                            <>
                                <div className="card p-6">
                                    <h3 className="font-bold text-lg mb-4" style={{ color: 'var(--text-primary)' }}>Async Thunks</h3>
                                    <p style={{ color: 'var(--text-secondary)' }}>
                                        Thunks are async actions that can dispatch multiple actions and access state. Perfect for API calls and complex workflows.
                                    </p>
                                </div>
                                <CodeBlock title="ThunkAction" code={`class FetchUserThunk(val userId: String) : ThunkAction<AppState> {
    override suspend fun execute(
        dispatch: Dispatcher,
        getState: GetState<AppState>
    ) {
        dispatch(UserAction.SetLoading(true))
        
        try {
            val user = api.fetchUser(userId)
            dispatch(UserAction.SetUser(user))
        } catch (e: Exception) {
            dispatch(UserAction.SetError(e.message))
        } finally {
            dispatch(UserAction.SetLoading(false))
        }
    }
}

// Dispatch thunk
store.dispatch(FetchUserThunk("123"))`} index={12} />
                            </>
                        )}

                        {activeTab === 'selector' && (
                            <>
                                <div className="card p-6">
                                    <h3 className="font-bold text-lg mb-4" style={{ color: 'var(--text-primary)' }}>Middleware</h3>
                                    <p style={{ color: 'var(--text-secondary)' }}>
                                        Middleware intercepts actions, enabling logging, crash reporting, async handling, and more.
                                    </p>
                                </div>
                                <CodeBlock title="Built-in Middleware" code={`// Thunk Middleware - async actions
addMiddleware(ThunkMiddleware())

// Logging Middleware - debug
addMiddleware(LoggingMiddleware(tag = "Store"))

// Listener Middleware - side effects
val listener = createListenerMiddleware<AppState>()
listener.addListener<UserAction.Login> { action, api ->
    // Handle side effects
    analytics.track("user_login")
}
addMiddleware(listener.middleware)`} index={13} />
                            </>
                        )}
                    </div>
                </div>
            </section>

            {/* Toolkit */}
            <section id="toolkit" className="py-20 px-4" style={{ background: 'var(--bg-secondary)' }}>
                <div className="max-w-4xl mx-auto">
                    <div className="text-center mb-12">
                        <div className="inline-flex items-center gap-2 mb-4" style={{ color: 'var(--accent)' }}>
                            <Package className="w-5 h-5" />
                            <span className="text-sm font-semibold uppercase tracking-wider">Redux Toolkit</span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-bold" style={{ color: 'var(--text-primary)' }}>Modern Redux Features</h2>
                        <p className="mt-4 max-w-2xl mx-auto" style={{ color: 'var(--text-secondary)' }}>
                            All the power of Redux Toolkit, reimagined for Kotlin Multiplatform.
                        </p>
                    </div>

                    <div className="flex flex-wrap gap-2 mb-8">
                        {(['selector', 'thunk', 'entity', 'listener', 'slice'] as const).map((tab) => (
                            <button
                                key={tab}
                                onClick={() => setToolkitTab(tab)}
                                className={`px-4 py-2 rounded-lg font-medium transition capitalize ${toolkitTab === tab ? 'btn-primary' : 'btn-secondary'}`}
                            >
                                {tab === 'thunk' ? 'AsyncThunk' : tab === 'entity' ? 'EntityAdapter' : tab === 'listener' ? 'Listener' : tab}
                            </button>
                        ))}
                    </div>

                    <div className="space-y-6">
                        {toolkitTab === 'selector' && (
                            <CodeBlock title="createSelector - Memoized Selectors" code={`// Simple selector
val selectCount = selector<AppState, Int> { it.counter }

// Derived selector with memoization
val selectDoubleCount = createSelector(selectCount) { count ->
    count * 2  // Only recomputes when count changes
}

// Multiple inputs
val selectTotalPrice = createSelector(
    { state: CartState -> state.items },
    { state: CartState -> state.taxRate }
) { items, taxRate ->
    items.sumOf { it.price } * (1 + taxRate)
}

// Use in Compose
@Composable
fun Counter() {
    val state by store.state.collectAsState()
    val doubled = selectDoubleCount(state)
    Text("Doubled: $doubled")
}`} index={20} />
                        )}

                        {toolkitTab === 'thunk' && (
                            <CodeBlock title="createAsyncThunk - Lifecycle Actions" code={`// Create async thunk with automatic lifecycle actions
val fetchUser = createAsyncThunk<String, User, AppState>(
    typePrefix = "users/fetchById"
) { userId, thunkApi ->
    // This runs async
    val response = api.getUser(userId)
    response.body() ?: throw Exception("Not found")
}

// Dispatches:
// - users/fetchById/pending  → when started
// - users/fetchById/fulfilled → on success
// - users/fetchById/rejected  → on error

// Handle in reducer
val userReducer = reducer<UserState> {
    on<AsyncThunkPending<String>> { state, _ ->
        state.copy(loading = true)
    }
    on<AsyncThunkFulfilled<String, User>> { state, action ->
        state.copy(loading = false, user = action.payload)
    }
    on<AsyncThunkRejected<String>> { state, action ->
        state.copy(loading = false, error = action.error.message)
    }
}`} index={21} />
                        )}

                        {toolkitTab === 'entity' && (
                            <CodeBlock title="createEntityAdapter - Normalized State" code={`// Create adapter with ID selector
val usersAdapter = createEntityAdapter<User> { it.id }

// State uses EntityState
data class UsersState(
    val users: EntityState<User> = usersAdapter.getInitialState(),
    val loading: Boolean = false
) : State

// CRUD operations
val newState = usersAdapter.addOne(state.users, newUser)
val updated = usersAdapter.updateOne(state.users, id) { 
    it.copy(name = "Updated") 
}
val removed = usersAdapter.removeOne(state.users, id)
val allSet = usersAdapter.setAll(state.users, userList)

// Built-in selectors
val allUsers = usersAdapter.selectAll(state.users)
val user = usersAdapter.selectById(state.users, "123")
val total = usersAdapter.selectTotal(state.users)
val ids = usersAdapter.selectIds(state.users)`} index={22} />
                        )}

                        {toolkitTab === 'listener' && (
                            <CodeBlock title="createListenerMiddleware - Side Effects" code={`val listenerMiddleware = createListenerMiddleware<AppState>()

// Type-safe listener
listenerMiddleware.addListener<UserAction.Login> { action, api ->
    // Access current state
    val user = api.getState().user
    
    // Dispatch actions
    api.dispatch(AnalyticsAction.Track("login"))
    
    // Async operations
    api.fork {
        delay(1000)
        api.dispatch(NotificationAction.Show("Welcome!"))
    }
}

// Predicate-based listener
listenerMiddleware.addListener(
    predicate = { action -> action is AsyncThunkFulfilled<*, *> }
) { action, api ->
    println("An async thunk completed!")
}

// Add to store
createStore(...) {
    addMiddleware(listenerMiddleware.middleware)
}`} index={23} />
                        )}

                        {toolkitTab === 'slice' && (
                            <CodeBlock title="createSlice - Combines Everything" code={`val counterSlice = createSlice<CounterState>(
    name = "counter",
    initialState = CounterState()
) {
    // Auto-generates action creators
    reduce("increment") { state, _ ->
        state.copy(count = state.count + 1)
    }
    
    reduce("decrement") { state, _ ->
        state.copy(count = state.count - 1)
    }
    
    // With payload
    reduce<Int>("addAmount") { state, payload ->
        state.copy(count = state.count + payload)
    }
    
    // Handle async thunks
    extraReducers {
        addCase<AsyncThunkPending<*>> { state, _ ->
            state.copy(loading = true)
        }
    }
}

// Use the slice
val reducer = counterSlice.reducer
val actions = counterSlice.actions

// Dispatch
store.dispatch(actions.invoke("increment"))
store.dispatch(actions.invoke("addAmount", 5))`} index={24} />
                        )}
                    </div>
                </div>
            </section>

            {/* Compose Integration */}
            <section id="compose" className="py-20 px-4">
                <div className="max-w-4xl mx-auto">
                    <div className="text-center mb-12">
                        <div className="inline-flex items-center gap-2 mb-4" style={{ color: 'var(--accent)' }}>
                            <RefreshCw className="w-5 h-5" />
                            <span className="text-sm font-semibold uppercase tracking-wider">Compose Multiplatform</span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-bold" style={{ color: 'var(--text-primary)' }}>Seamless UI Integration</h2>
                    </div>

                    <div className="space-y-6">
                        <CodeBlock title="collectAsState - Observe State" code={`@Composable
fun CounterScreen() {
    val store = remember { appStore }
    val state by store.state.collectAsState()
    
    Column {
        Text("Count: \${state.count}")
        
        Button(onClick = { 
            store.dispatch(CounterAction.Increment) 
        }) {
            Text("Increment")
        }
    }
}`} index={30} />

                        <CodeBlock title="selectState - Derived State" code={`@Composable
fun UserList() {
    val store = remember { appStore }
    val state by store.state.collectAsState()
    
    // Use selector for derived data
    val onlineUsers = remember(state) {
        selectOnlineUsers(state)
    }
    
    LazyColumn {
        items(onlineUsers) { user ->
            UserRow(user)
        }
    }
}`} index={31} />
                    </div>
                </div>
            </section>

            {/* Sample App */}
            <section id="sample-app" className="py-20 px-4" style={{ background: 'var(--bg-secondary)' }}>
                <div className="max-w-4xl mx-auto">
                    <div className="text-center mb-12">
                        <div className="inline-flex items-center gap-2 mb-4" style={{ color: 'var(--accent)' }}>
                            <MessageSquare className="w-5 h-5" />
                            <span className="text-sm font-semibold uppercase tracking-wider">Sample Application</span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-bold" style={{ color: 'var(--text-primary)' }}>Chat App Demo</h2>
                        <p className="mt-4 max-w-2xl mx-auto" style={{ color: 'var(--text-secondary)' }}>
                            The sample application demonstrates all Redux Toolkit features in a real chat app.
                        </p>
                    </div>

                    <div className="grid md:grid-cols-2 gap-6 mb-8">
                        {[
                            { title: 'EntityAdapter', desc: 'Messages and users stored in normalized EntityState' },
                            { title: 'Memoized Selectors', desc: 'selectAllMessages, selectUnreadCount, selectTypingUsers' },
                            { title: 'AsyncThunk', desc: 'SendMessage and FetchChatHistory with loading states' },
                            { title: 'ListenerMiddleware', desc: 'Auto-reply simulation and typing indicators' },
                        ].map((item, i) => (
                            <div key={i} className="card p-4">
                                <h4 className="font-bold" style={{ color: 'var(--accent)' }}>{item.title}</h4>
                                <p className="text-sm mt-1" style={{ color: 'var(--text-secondary)' }}>{item.desc}</p>
                            </div>
                        ))}
                    </div>

                    <CodeBlock title="Run the Sample App" code={`# Desktop
./gradlew :sample:run

# Android
./gradlew :androidApp:installDebug

# iOS
open iosApp/iosApp.xcodeproj

# Web
./gradlew :sample:jsBrowserDevelopmentRun`} index={40} lang="bash" />
                </div>
            </section>

            {/* API Reference */}
            <section id="api" className="py-20 px-4">
                <div className="max-w-4xl mx-auto">
                    <div className="text-center mb-12">
                        <div className="inline-flex items-center gap-2 mb-4" style={{ color: 'var(--accent)' }}>
                            <FileCode className="w-5 h-5" />
                            <span className="text-sm font-semibold uppercase tracking-wider">API Reference</span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-bold" style={{ color: 'var(--text-primary)' }}>Public API</h2>
                    </div>

                    <div className="space-y-4">
                        {[
                            { module: 'Core', items: ['State', 'Action', 'Reducer', 'Store', 'createStore()', 'reducer{}'] },
                            { module: 'Middleware', items: ['Middleware', 'ThunkMiddleware', 'ThunkAction', 'LoggingMiddleware', 'ListenerMiddleware', 'createListenerMiddleware()'] },
                            { module: 'Toolkit', items: ['createSelector()', 'MemoizedSelector', 'createAsyncThunk()', 'AsyncThunkPending/Fulfilled/Rejected', 'createEntityAdapter()', 'EntityState', 'createSlice()', 'Slice'] },
                            { module: 'Compose', items: ['collectAsState()', 'ProvideStore()', 'useStore()'] },
                        ].map((section, i) => (
                            <div key={i} className="card p-6">
                                <h3 className="font-bold text-lg mb-4" style={{ color: 'var(--accent)' }}>{section.module}</h3>
                                <div className="flex flex-wrap gap-2">
                                    {section.items.map((item, j) => (
                                        <span key={j} className="px-3 py-1 rounded-full text-sm font-mono" style={{ background: 'var(--bg-tertiary)', color: 'var(--text-secondary)' }}>
                                            {item}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* CTA */}
            <section className="py-24 px-4 relative overflow-hidden" style={{ background: 'var(--bg-secondary)' }}>
                <div className="absolute inset-0 overflow-hidden pointer-events-none">
                    <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[600px] h-[600px] bg-gradient-to-br from-purple-500/10 to-pink-500/10 rounded-full blur-3xl" />
                </div>

                <div className="max-w-4xl mx-auto text-center relative z-10">
                    <h2 className="text-3xl md:text-5xl font-bold mb-6" style={{ color: 'var(--text-primary)' }}>
                        Ready to Add Redux to Your KMP App?
                    </h2>
                    <p className="text-lg mb-10 max-w-2xl mx-auto" style={{ color: 'var(--text-secondary)' }}>
                        Get started with type-safe state management for Kotlin Multiplatform today.
                    </p>
                    <div className="flex flex-col sm:flex-row gap-4 justify-center">
                        <a href="https://github.com/sitharaj88/kmp-starter" target="_blank" rel="noopener noreferrer" className="btn-primary inline-flex items-center justify-center gap-3 px-10 py-5 text-lg">
                            <Github className="w-6 h-6" /> Get Started
                        </a>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="py-10 px-4 border-t" style={{ borderColor: 'var(--border)' }}>
                <div className="max-w-6xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
                    <div className="flex items-center gap-3">
                        <div className="w-8 h-8 bg-gradient-to-br from-purple-500 to-pink-600 rounded-lg flex items-center justify-center">
                            <Layers className="w-4 h-4 text-white" />
                        </div>
                        <span className="font-semibold" style={{ color: 'var(--text-primary)' }}>Redux KMP</span>
                    </div>
                    <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
                        Built with ❤️ by <a href="https://github.com/sitharaj88" className="underline hover:no-underline" style={{ color: 'var(--accent)' }}>Sitharaj</a>
                    </p>
                    <a href="https://github.com/sitharaj88/kmp-starter" target="_blank" rel="noopener noreferrer" className="transition hover:opacity-80" style={{ color: 'var(--text-muted)' }}>
                        <Github className="w-6 h-6" />
                    </a>
                </div>
            </footer>
        </div>
    )
}

export default App
