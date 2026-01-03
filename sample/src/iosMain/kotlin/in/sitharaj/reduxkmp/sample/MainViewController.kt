package `in`.sitharaj.reduxkmp.sample

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) { App() }
