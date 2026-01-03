package com.sitharaj.reduxkmp.middleware

/**
 * WASM JS implementation using JS Date.now() via external declaration.
 */
@JsName("Date")
private external object JsDate {
    fun now(): Double
}

internal actual fun currentTimeMillis(): Long = JsDate.now().toLong()
