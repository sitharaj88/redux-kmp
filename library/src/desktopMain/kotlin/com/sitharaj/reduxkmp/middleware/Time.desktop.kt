package com.sitharaj.reduxkmp.middleware

/**
 * Desktop (JVM) implementation using System.currentTimeMillis()
 */
internal actual fun currentTimeMillis(): Long = System.currentTimeMillis()
