package com.sitharaj.reduxkmp.sample.chat.messages

import kotlin.js.Date

/**
 * WASM implementation of currentTimeMillis
 */
internal actual fun currentTimeMillis(): Long = Date.now().toLong()
