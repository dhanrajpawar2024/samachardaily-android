package com.imp.samachardaily

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import com.imp.samachardaily.core.network.TokenProvider
import com.imp.samachardaily.presentation.navigation.AppNavigation
import com.imp.samachardaily.presentation.navigation.Screen
import com.imp.samachardaily.ui.theme.SamacharDailyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesDataStore: UserPreferencesDataStore

    @Inject
    lateinit var tokenProvider: TokenProvider

    @Suppress("DEPRECATION")
    private lateinit var googleSignInClient: GoogleSignInClient

    // Callback invoked from OnboardingScreen / ProfileScreen via LocalGoogleSignIn
    var onGoogleSignInResult: ((idToken: String) -> Unit)? = null

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleGoogleSignInResult(result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupGoogleSignIn()
        enableEdgeToEdge()

        // Restore persisted tokens into in-memory TokenProvider BEFORE any API call fires.
        // runBlocking here is intentional — we must have the token ready before setContent
        // renders HomeScreen which immediately triggers feed requests.
        runBlocking(Dispatchers.IO) {
            val accessToken  = userPreferencesDataStore.authToken.first()
            val refreshToken = userPreferencesDataStore.refreshToken.first()
            if (accessToken  != null) tokenProvider.setToken(accessToken)
            if (refreshToken != null) tokenProvider.setRefreshToken(refreshToken)
        }

        setContent {
            SamacharDailyTheme {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    val isOnboardingCompleted by userPreferencesDataStore
                        .isOnboardingCompleted
                        .collectAsStateWithLifecycle(initialValue = false)

                    val authToken by userPreferencesDataStore
                        .authToken
                        .collectAsStateWithLifecycle(initialValue = null)

                    val hasSession = !authToken.isNullOrBlank()
                    val startDestination = if (isOnboardingCompleted && hasSession) {
                        Screen.Home.route
                    } else {
                        Screen.Onboarding.route
                    }

                    // Handle FCM deep-link
                    val articleId = intent?.getStringExtra("article_id")
                    LaunchedEffect(articleId) {
                        if (articleId != null) {
                            navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                        }
                    }

                    // Expose sign-in launcher to the Compose tree
                    CompositionLocalProvider(LocalGoogleSignInLauncher provides ::launchGoogleSignIn) {
                        AppNavigation(
                            navController      = navController,
                            startDestination   = startDestination
                        )
                    }
                }
            }
        }
    }

    // ── Google Sign-In ──────────────────────────────────────

    @Suppress("DEPRECATION")
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun launchGoogleSignIn(onResult: (idToken: String) -> Unit) {
        onGoogleSignInResult = onResult
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                onGoogleSignInResult?.invoke(idToken)
            } else {
                Log.e("GoogleSignIn", "idToken is null")
                Toast.makeText(this, "Google sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.statusCode}")
            Toast.makeText(this, "Google sign-in failed (${e.statusCode})", Toast.LENGTH_SHORT).show()
        } finally {
            onGoogleSignInResult = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Re-handle FCM deep-link when activity is already running
        intent.getStringExtra("article_id")?.let { id ->
            CoroutineScope(Dispatchers.Main).launch {
                // navController not directly accessible here — use a shared flow in future
                Log.d("MainActivity", "Deep-link article: $id")
            }
        }
    }
}

// LocalGoogleSignInLauncher is defined in CompositionLocals.kt
