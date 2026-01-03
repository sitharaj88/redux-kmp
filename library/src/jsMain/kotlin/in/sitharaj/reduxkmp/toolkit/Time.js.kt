package `in`.sitharaj.reduxkmp.toolkit

import kotlin.js.Date

/**
 * JavaScript implementation using Date.now()
 */
internal actual fun currentTimeMillis(): Long = Date.now().toLong()
