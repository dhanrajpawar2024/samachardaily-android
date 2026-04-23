package com.imp.samachardaily

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Composition Local that exposes the Google Sign-In launcher from [MainActivity]
 * to any Composable in the tree without prop-drilling.
 *
 * Usage:
 *   val launcher = LocalGoogleSignInLauncher.current
 *   launcher { idToken -> /* handle token */ }
 */
val LocalGoogleSignInLauncher = staticCompositionLocalOf<(onResult: (String) -> Unit) -> Unit> {
    error("LocalGoogleSignInLauncher not provided — wrap with CompositionLocalProvider in MainActivity")
}

