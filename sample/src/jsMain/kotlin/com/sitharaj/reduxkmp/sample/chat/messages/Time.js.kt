package com.sitharaj.reduxkmp.sample.chat.messages

import kotlin.js.Date

/**
 * JS implementation of currentTimeMillis
 */
actual fun currentTimeMillis(): Long = Date.now().toLong()
