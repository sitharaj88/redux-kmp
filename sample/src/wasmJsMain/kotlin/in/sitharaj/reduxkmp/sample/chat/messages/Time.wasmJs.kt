package `in`.sitharaj.reduxkmp.sample.chat.messages

import kotlin.js.Date

/**
 * WASM implementation of currentTimeMillis
 */
actual fun currentTimeMillis(): Long = Date.now().toLong()
