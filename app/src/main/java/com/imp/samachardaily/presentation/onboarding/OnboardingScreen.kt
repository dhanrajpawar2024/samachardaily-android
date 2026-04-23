package com.imp.samachardaily.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imp.samachardaily.LocalGoogleSignInLauncher
import com.imp.samachardaily.R
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.common.toLanguageDisplayName
import com.imp.samachardaily.presentation.common.LoadingState
import com.imp.samachardaily.ui.theme.SamacharDailyTheme

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate out when onboarding is done
    LaunchedEffect(uiState.step) {
        // Handled in skipLogin / completeLogin — caller navigates
    }

    AnimatedContent(
        targetState = uiState.step,
        label = "onboarding_step",
        modifier = Modifier.fillMaxSize()
    ) { step ->
        when (step) {
            OnboardingStep.LANGUAGE -> LanguageSelectionStep(
                selectedLanguages = uiState.selectedLanguages,
                onToggle = viewModel::toggleLanguage,
                onNext = viewModel::proceedFromLanguage,
                enabled = uiState.selectedLanguages.isNotEmpty()
            )

            OnboardingStep.CATEGORIES -> CategorySelectionStep(
                categories = uiState.availableCategories,
                selectedIds = uiState.selectedCategoryIds,
                isLoading = uiState.isLoading,
                onToggle = viewModel::toggleCategory,
                onNext = viewModel::proceedFromCategories
            )

            OnboardingStep.LOGIN -> LoginStep(
                isLoading = uiState.isLoading,
                error = uiState.error,
                onSignInWithGoogle = { idToken ->
                    viewModel.loginWithGoogle(idToken) { onOnboardingComplete() }
                },
                onSkip = {
                    viewModel.skipLogin { onOnboardingComplete() }
                }
            )
        }
    }
}

@Composable
private fun LanguageSelectionStep(
    selectedLanguages: Set<String>,
    onToggle: (String) -> Unit,
    onNext: () -> Unit,
    enabled: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            text = stringResource(R.string.onboarding_language_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.onboarding_language_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Constants.SUPPORTED_LANGUAGES.forEach { lang ->
            val selected = lang in selectedLanguages
            OutlinedButton(
                onClick = { onToggle(lang) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                border = BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = lang.toLanguageDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = enabled
        ) {
            Text(stringResource(R.string.action_next))
        }
        Spacer(Modifier.height(24.dp))
    } // Column
    } // Box
}

@Composable
private fun CategorySelectionStep(
    categories: List<com.imp.samachardaily.domain.model.Category>,
    selectedIds: Set<String>,
    isLoading: Boolean,
    onToggle: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.onboarding_categories_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.onboarding_categories_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        if (isLoading) {
            LoadingState(Modifier.weight(1f))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val selected = category.id in selectedIds
                    FilterChip(
                        selected = selected,
                        onClick = { onToggle(category.id) },
                        label = { Text(category.name, modifier = Modifier.fillMaxWidth()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(stringResource(R.string.action_next))
        }
    }
}

@Composable
private fun LoginStep(
    isLoading: Boolean,
    error: String?,
    onSignInWithGoogle: (idToken: String) -> Unit,
    onSkip: () -> Unit
) {
    val googleSignInLauncher = LocalGoogleSignInLauncher.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.onboarding_login_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.onboarding_login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            OutlinedButton(
                onClick = { googleSignInLauncher { idToken -> onSignInWithGoogle(idToken) } },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.action_sign_in_google))
            }
            Spacer(Modifier.height(12.dp))
            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.action_skip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    SamacharDailyTheme {
        LanguageSelectionStep(
            selectedLanguages = setOf("en"),
            onToggle = {},
            onNext = {},
            enabled = true
        )
    }
}

