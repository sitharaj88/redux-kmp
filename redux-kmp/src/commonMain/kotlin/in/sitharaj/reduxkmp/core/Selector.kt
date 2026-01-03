package `in`.sitharaj.reduxkmp.core

/**
 * Memoized Selectors - Reselect-like selector memoization for Redux KMP.
 * 
 * Selectors are functions that extract and derive data from the Redux state.
 * Memoized selectors only recompute when their inputs change, improving performance.
 * 
 * ## Usage
 * 
 * ```kotlin
 * // Simple selector
 * val selectCount = selector<AppState, Int> { state -> state.counter }
 * 
 * // Derived selector with memoization
 * val selectDoubleCount = createSelector(
 *     selectCount
 * ) { count -> count * 2 }
 * 
 * // Multiple input selectors
 * val selectTotal = createSelector(
 *     { state: AppState -> state.items },
 *     { state: AppState -> state.taxRate }
 * ) { items, taxRate ->
 *     items.sumOf { it.price } * (1 + taxRate)
 * }
 * ```
 */

/**
 * A selector function that extracts data from state.
 */
public typealias Selector<S, R> = (state: S) -> R

/**
 * Creates a simple (non-memoized) selector.
 */
public fun <S : State, R> selector(
    select: (S) -> R
): Selector<S, R> = select

/**
 * A memoized selector that caches its result and only recomputes when inputs change.
 */
public class MemoizedSelector<S : State, R> internal constructor(
    private val compute: (S) -> R,
    private val equalityCheck: (R, R) -> Boolean = { a, b -> a == b }
) {
    
    private var lastState: S? = null
    private var lastResult: R? = null
    private var hasResult: Boolean = false
    
    /**
     * Selects and returns the computed result for the given state.
     * Results are memoized based on input state reference equality.
     */
    public fun select(state: S): R {
        // If state hasn't changed, return cached result
        if (hasResult && lastState === state) {
            @Suppress("UNCHECKED_CAST")
            return lastResult as R
        }
        
        val result = compute(state)
        
        // Check if result is equal to previous (for reference equality optimization)
        if (hasResult && lastResult != null && equalityCheck(lastResult!!, result)) {
            lastState = state
            @Suppress("UNCHECKED_CAST")
            return lastResult as R
        }
        
        lastState = state
        lastResult = result
        hasResult = true
        return result
    }
    
    /**
     * Operator invoke for convenience - delegates to select().
     */
    public operator fun invoke(state: S): R = select(state)
    
    /**
     * Clears the memoization cache.
     */
    public fun clearCache() {
        lastState = null
        lastResult = null
        hasResult = false
    }
    
    /**
     * Returns the last computed result without recomputing.
     */
    public fun resultOrNull(): R? = if (hasResult) lastResult else null
}

// ============================================
// createSelector overloads (1-5 input selectors)
// ============================================

/**
 * Creates a memoized selector with 1 input selector.
 * 
 * ```kotlin
 * val selectDoubleCount = createSelector(
 *     selectCount
 * ) { count -> count * 2 }
 * ```
 */
public fun <S : State, R1, Result> createSelector(
    selector1: Selector<S, R1>,
    resultFunc: (R1) -> Result
): MemoizedSelector<S, Result> {
    var lastInput1: R1? = null
    var hasLastInput = false
    var lastResult: Result? = null
    
    return MemoizedSelector({ state ->
        val input1 = selector1(state)
        
        if (hasLastInput && lastInput1 == input1) {
            @Suppress("UNCHECKED_CAST")
            lastResult as Result
        } else {
            lastInput1 = input1
            hasLastInput = true
            resultFunc(input1).also { lastResult = it }
        }
    })
}

/**
 * Creates a memoized selector with 2 input selectors.
 */
public fun <S : State, R1, R2, Result> createSelector(
    selector1: Selector<S, R1>,
    selector2: Selector<S, R2>,
    resultFunc: (R1, R2) -> Result
): MemoizedSelector<S, Result> {
    var lastInput1: R1? = null
    var lastInput2: R2? = null
    var hasLastInput = false
    var lastResult: Result? = null
    
    return MemoizedSelector({ state ->
        val input1 = selector1(state)
        val input2 = selector2(state)
        
        if (hasLastInput && lastInput1 == input1 && lastInput2 == input2) {
            @Suppress("UNCHECKED_CAST")
            lastResult as Result
        } else {
            lastInput1 = input1
            lastInput2 = input2
            hasLastInput = true
            resultFunc(input1, input2).also { lastResult = it }
        }
    })
}

/**
 * Creates a memoized selector with 3 input selectors.
 */
public fun <S : State, R1, R2, R3, Result> createSelector(
    selector1: Selector<S, R1>,
    selector2: Selector<S, R2>,
    selector3: Selector<S, R3>,
    resultFunc: (R1, R2, R3) -> Result
): MemoizedSelector<S, Result> {
    var lastInput1: R1? = null
    var lastInput2: R2? = null
    var lastInput3: R3? = null
    var hasLastInput = false
    var lastResult: Result? = null
    
    return MemoizedSelector({ state ->
        val input1 = selector1(state)
        val input2 = selector2(state)
        val input3 = selector3(state)
        
        if (hasLastInput && lastInput1 == input1 && lastInput2 == input2 && lastInput3 == input3) {
            @Suppress("UNCHECKED_CAST")
            lastResult as Result
        } else {
            lastInput1 = input1
            lastInput2 = input2
            lastInput3 = input3
            hasLastInput = true
            resultFunc(input1, input2, input3).also { lastResult = it }
        }
    })
}

/**
 * Creates a memoized selector with 4 input selectors.
 */
public fun <S : State, R1, R2, R3, R4, Result> createSelector(
    selector1: Selector<S, R1>,
    selector2: Selector<S, R2>,
    selector3: Selector<S, R3>,
    selector4: Selector<S, R4>,
    resultFunc: (R1, R2, R3, R4) -> Result
): MemoizedSelector<S, Result> {
    var lastInput1: R1? = null
    var lastInput2: R2? = null
    var lastInput3: R3? = null
    var lastInput4: R4? = null
    var hasLastInput = false
    var lastResult: Result? = null
    
    return MemoizedSelector({ state ->
        val input1 = selector1(state)
        val input2 = selector2(state)
        val input3 = selector3(state)
        val input4 = selector4(state)
        
        if (hasLastInput && 
            lastInput1 == input1 && lastInput2 == input2 && 
            lastInput3 == input3 && lastInput4 == input4) {
            @Suppress("UNCHECKED_CAST")
            lastResult as Result
        } else {
            lastInput1 = input1
            lastInput2 = input2
            lastInput3 = input3
            lastInput4 = input4
            hasLastInput = true
            resultFunc(input1, input2, input3, input4).also { lastResult = it }
        }
    })
}

/**
 * Creates a memoized selector with 5 input selectors.
 */
public fun <S : State, R1, R2, R3, R4, R5, Result> createSelector(
    selector1: Selector<S, R1>,
    selector2: Selector<S, R2>,
    selector3: Selector<S, R3>,
    selector4: Selector<S, R4>,
    selector5: Selector<S, R5>,
    resultFunc: (R1, R2, R3, R4, R5) -> Result
): MemoizedSelector<S, Result> {
    var lastInput1: R1? = null
    var lastInput2: R2? = null
    var lastInput3: R3? = null
    var lastInput4: R4? = null
    var lastInput5: R5? = null
    var hasLastInput = false
    var lastResult: Result? = null
    
    return MemoizedSelector({ state ->
        val input1 = selector1(state)
        val input2 = selector2(state)
        val input3 = selector3(state)
        val input4 = selector4(state)
        val input5 = selector5(state)
        
        if (hasLastInput && 
            lastInput1 == input1 && lastInput2 == input2 && 
            lastInput3 == input3 && lastInput4 == input4 && lastInput5 == input5) {
            @Suppress("UNCHECKED_CAST")
            lastResult as Result
        } else {
            lastInput1 = input1
            lastInput2 = input2
            lastInput3 = input3
            lastInput4 = input4
            lastInput5 = input5
            hasLastInput = true
            resultFunc(input1, input2, input3, input4, input5).also { lastResult = it }
        }
    })
}

// ============================================
// Structured Selectors - createStructuredSelector
// ============================================

/**
 * Result class for structured selectors, providing type-safe access to multiple selections.
 */
public data class StructuredSelection<T1, T2>(
    val first: T1,
    val second: T2
)

/**
 * Creates a structured selector that returns multiple values as a data class.
 * 
 * ```kotlin
 * val selectUserData = createStructuredSelector(
 *     selectUserName,
 *     selectUserEmail
 * )
 * 
 * val (name, email) = selectUserData(state)
 * ```
 */
public fun <S : State, R1, R2> createStructuredSelector(
    selector1: Selector<S, R1>,
    selector2: Selector<S, R2>
): MemoizedSelector<S, StructuredSelection<R1, R2>> {
    return createSelector(selector1, selector2) { r1, r2 ->
        StructuredSelection(r1, r2)
    }
}
