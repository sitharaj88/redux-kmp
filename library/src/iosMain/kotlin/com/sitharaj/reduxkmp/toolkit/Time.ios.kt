package com.sitharaj.reduxkmp.toolkit

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

/**
 * iOS implementation using NSDate
 */
internal actual fun currentTimeMillis(): Long = 
    (NSDate().timeIntervalSince1970 * 1000).toLong()
