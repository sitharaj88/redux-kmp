# Redux KMP 🔄

A **Kotlin Multiplatform Redux library** with modern Redux Toolkit features, type-safe DSLs, and Compose Multiplatform integration.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-1.9.3-4285F4?style=flat&logo=jetpack-compose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## ✨ Features

| Feature | Description |
|---------|-------------|
| **Kotlin DSL** | Type-safe reducers with `reducer { on<Action> { } }` syntax |
| **StateFlow** | Reactive state powered by Kotlin Coroutines |
| **Memoized Selectors** | `createSelector()` with automatic caching |
| **Async Thunks** | `createAsyncThunk()` with pending/fulfilled/rejected lifecycle |
| **Entity Adapter** | `createEntityAdapter()` for normalized CRUD operations |
| **Listener Middleware** | Side-effect handling with `addListener()` |
| **Slice Pattern** | `createSlice()` to combine reducer + actions |
| **Compose Integration** | `collectAsState()` for Compose Multiplatform |

## 📱 Supported Platforms

| Platform | Status |
|----------|--------|
| Android | ✅ |
| iOS | ✅ |
| Desktop (JVM) | ✅ |
| Web (JS) | ✅ |
| Web (WASM) | ⚠️ Beta |

## 🚀 Quick Start

### 1. Add Dependency

```kotlin
// build.gradle.kts
dependencies {
    implementation("in.sitharaj.reduxkmp:redux-kmp:1.0.0")
}
```

### 2. Define State & Actions

```kotlin
data class CounterState(
    val count: Int = 0,
    val loading: Boolean = false
) : State

sealed interface CounterAction : Action {
    data object Increment : CounterAction
    data object Decrement : CounterAction
    data class SetCount(val value: Int) : CounterAction
}
```

### 3. Create Reducer

```kotlin
val counterReducer = reducer<CounterState> {
    on<CounterAction.Increment> { state, _ ->
        state.copy(count = state.count + 1)
    }
    on<CounterAction.Decrement> { state, _ ->
        state.copy(count = state.count - 1)
    }
    on<CounterAction.SetCount> { state, action ->
        state.copy(count = action.value)
    }
}
```

### 4. Create Store

```kotlin
val store = createStore(
    initialState = CounterState(),
    reducer = counterReducer,
    scope = CoroutineScope(Dispatchers.Main)
) {
    addMiddleware(ThunkMiddleware())
    addMiddleware(LoggingMiddleware(tag = "Counter"))
}
```

### 5. Dispatch Actions

```kotlin
store.dispatch(CounterAction.Increment)

// Observe state
store.state.collect { state ->
    println("Count: ${state.count}")
}
```

### 6. Use with Compose

```kotlin
@Composable
fun CounterScreen() {
    val state by store.state.collectAsState()
    
    Column {
        Text("Count: ${state.count}")
        Button(onClick = { store.dispatch(CounterAction.Increment) }) {
            Text("Increment")
        }
    }
}
```

## 🛠 Redux Toolkit Features

### Memoized Selectors

```kotlin
val selectCount = selector<AppState, Int> { it.counter }

val selectDoubleCount = createSelector(selectCount) { count ->
    count * 2  // Only recomputes when count changes
}

// Multiple inputs
val selectTotal = createSelector(
    { state: CartState -> state.items },
    { state: CartState -> state.taxRate }
) { items, taxRate ->
    items.sumOf { it.price } * (1 + taxRate)
}
```

### Async Thunk

```kotlin
val fetchUser = createAsyncThunk<String, User, AppState>(
    typePrefix = "users/fetchById"
) { userId, thunkApi ->
    api.getUser(userId)
}

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
}
```

### Entity Adapter

```kotlin
val usersAdapter = createEntityAdapter<User> { it.id }

data class UsersState(
    val users: EntityState<User> = usersAdapter.getInitialState()
) : State

// CRUD operations
usersAdapter.addOne(state.users, newUser)
usersAdapter.updateOne(state.users, id) { it.copy(name = "New") }
usersAdapter.removeOne(state.users, id)

// Selectors
val allUsers = usersAdapter.selectAll(state.users)
val user = usersAdapter.selectById(state.users, "123")
```

### Listener Middleware

```kotlin
val listenerMiddleware = createListenerMiddleware<AppState>()

listenerMiddleware.addListener<UserAction.Login> { action, api ->
    api.dispatch(AnalyticsAction.Track("login"))
    
    api.fork {
        delay(1000)
        api.dispatch(NotificationAction.Show("Welcome!"))
    }
}
```

### Slice

```kotlin
val counterSlice = createSlice<CounterState>(
    name = "counter",
    initialState = CounterState()
) {
    reduce("increment") { state, _ ->
        state.copy(count = state.count + 1)
    }
    
    reduce<Int>("addAmount") { state, payload ->
        state.copy(count = state.count + payload)
    }
}

// Use
val reducer = counterSlice.reducer
store.dispatch(counterSlice.actions.invoke("increment"))
```

## 📁 Library Structure

```
redux-kmp/src/commonMain/kotlin/in/sitharaj/reduxkmp/
├── core/
│   ├── Action.kt            # Base action interface
│   ├── State.kt             # Base state interface
│   ├── Reducer.kt           # Reducer typealias
│   ├── ReducerDSL.kt        # reducer { on<T> { } } DSL
│   ├── Store.kt             # Redux store implementation
│   ├── StoreDSL.kt          # createStore { } DSL
│   └── Selector.kt          # createSelector, MemoizedSelector
├── middleware/
│   ├── Middleware.kt        # Base middleware interface
│   ├── ThunkMiddleware.kt   # Async action support
│   ├── LoggingMiddleware.kt # Debug logging
│   └── ListenerMiddleware.kt # Side effects
├── toolkit/
│   ├── AsyncThunk.kt        # createAsyncThunk
│   ├── EntityAdapter.kt     # createEntityAdapter
│   └── Slice.kt             # createSlice
├── compose/
│   └── ComposeIntegration.kt # Compose helpers
└── ReduxKmp.kt              # Main entry point
```

## 📦 Sample App

The sample app demonstrates all Redux features in a **Chat Application**:

- **EntityAdapter** for normalized messages/users
- **Selectors** for derived state (unread count, typing users)
- **AsyncThunk** for sending messages
- **ListenerMiddleware** for auto-reply simulation

```bash
# Run Desktop
./gradlew :sample:run

# Run Android
./gradlew :androidApp:installDebug

# Run Web
./gradlew :sample:jsBrowserDevelopmentRun
```

## 📚 Documentation

- **[Documentation Site](https://sitharaj88.github.io/redux-kmp)** - Full API docs and tutorials
- **[GPG Setup Guide](GPG_SETUP.md)** - Maven Central signing

```bash
# Run docs locally
cd docs-site
npm install
npm run dev
```

## 🧪 Testing

```bash
./gradlew :redux-kmp:check           # All tests
./gradlew :redux-kmp:desktopTest     # Desktop
./gradlew :redux-kmp:jsTest          # JavaScript
./gradlew :redux-kmp:iosSimulatorArm64Test  # iOS
```

## 📤 Publishing

```bash
# Publish to local Maven
./gradlew :redux-kmp:publishToMavenLocal

# Create signed bundle for Maven Central
./gradlew :redux-kmp:zipBundle
```

## 📄 License

```
Copyright 2024 Sitharaj Seenivasan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

## ☕ Support

<a href="https://buymeacoffee.com/sitharaj88" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" height="50">
</a>

## 🔗 Links

- **Documentation**: [sitharaj88.github.io/redux-kmp](https://sitharaj88.github.io/redux-kmp)
- **GitHub**: [github.com/sitharaj88/redux-kmp](https://github.com/sitharaj88/redux-kmp)
- **Author**: [Sitharaj Seenivasan](https://github.com/sitharaj88)
