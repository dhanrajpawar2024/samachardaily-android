package com.imp.samachardaily.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.imp.samachardaily.LocalGoogleSignInLauncher
import com.imp.samachardaily.R
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.common.toLanguageDisplayName
import com.imp.samachardaily.ui.theme.SamacharDailyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBookmarksClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val googleSignInLauncher = LocalGoogleSignInLauncher.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            title = { Text("Sign out?") },
            text = { Text("You'll need to sign in again to access bookmarks and personalised feed.") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; viewModel.logout() }) {
                    Text(stringResource(R.string.profile_sign_out), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_profile)) },
                actions = {
                    if (uiState.isLoggedIn) {
                        IconButton(onClick = viewModel::refreshProfile) {
                            Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.profile_refresh))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isAccountLoading || uiState.authActionInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // ── Avatar + Name Card ────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        if (!uiState.userAvatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = uiState.userAvatarUrl,
                                contentDescription = "Profile picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Name & email
                    Column(modifier = Modifier.weight(1f)) {
                        if (uiState.isLoggedIn) {
                            Text(
                                text = uiState.userName ?: "User",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (!uiState.userEmail.isNullOrBlank()) {
                                Text(
                                    text = uiState.userEmail!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            AssistChip(
                                onClick = { showLogoutDialog = true },
                                label = { Text(stringResource(R.string.profile_sign_out)) },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.Logout, null, Modifier.size(16.dp))
                                }
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.profile_guest),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = stringResource(R.string.profile_sign_in_prompt),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    googleSignInLauncher { idToken -> viewModel.signInWithGoogle(idToken) }
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.AccountCircle, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.action_sign_in_google),
                                    style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            if (!uiState.errorMessage.isNullOrBlank()) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // ── Language Preferences ──────────────────────
            SectionHeader(title = "My Languages")
            Text(
                text = "Select languages to see news from all of them in your feed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(Constants.SUPPORTED_LANGUAGES) { lang ->
                    val selected = lang in uiState.selectedLanguages
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.toggleLanguage(lang) },
                        label = { Text(lang.toLanguageDisplayName()) },
                        leadingIcon = if (selected) {
                            { Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ── Content ───────────────────────────────────
            SectionHeader(title = "Content")

            // Bookmarks
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_bookmarks)) },
                supportingContent = { Text(stringResource(R.string.profile_bookmarks_hint)) },
                leadingContent = {
                    Icon(
                        Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = if (uiState.isLoggedIn) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.outline
                    )
                },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                modifier = Modifier.clickable(enabled = uiState.isLoggedIn, onClick = onBookmarksClick),
                overlineContent = if (!uiState.isLoggedIn) {
                    { Text(stringResource(R.string.profile_sign_in_required),
                        color = MaterialTheme.colorScheme.error) }
                } else null
            )

            // Topics/Categories count
            if (uiState.selectedCategoryIds.isNotEmpty()) {
                ListItem(
                    headlineContent = { Text("My Topics") },
                    supportingContent = {
                        Text(stringResource(R.string.profile_topics_selected, uiState.selectedCategoryIds.size))
                    },
                    leadingContent = {
                        Icon(Icons.Filled.Interests, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ── Followed Sources ──────────────────────────
            SectionHeader(title = "Followed Sources")
            if (uiState.followedSources.isEmpty()) {
                Text(
                    text = "Follow sources from any article to see them highlighted here",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    items(uiState.followedSources.toList()) { source ->
                        InputChip(
                            selected = true,
                            onClick = { viewModel.toggleFollowSource(source) },
                            label = { Text(source) },
                            trailingIcon = {
                                Icon(Icons.Filled.Close, contentDescription = "Unfollow", Modifier.size(14.dp))
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ── Settings ──────────────────────────────────
            SectionHeader(title = "Settings")

            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_dark_mode)) },
                supportingContent = { Text(if (uiState.isDarkMode) "On" else "Off") },
                leadingContent = {
                    Icon(Icons.Filled.DarkMode, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                },
                trailingContent = {
                    Switch(checked = uiState.isDarkMode, onCheckedChange = viewModel::setDarkMode)
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_notifications)) },
                supportingContent = { Text(if (uiState.notificationsEnabled) "Enabled" else "Disabled") },
                leadingContent = {
                    Icon(Icons.Filled.Notifications, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                },
                trailingContent = {
                    Switch(checked = uiState.notificationsEnabled,
                        onCheckedChange = viewModel::setNotificationsEnabled)
                }
            )

            // Text Size Slider
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.FormatSize, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                    Text("Article Text Size", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = when {
                            uiState.textSize <= 0.85f -> "Small"
                            uiState.textSize <= 1.0f -> "Normal"
                            uiState.textSize <= 1.15f -> "Large"
                            else -> "Extra Large"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = uiState.textSize,
                    onValueChange = viewModel::setTextSize,
                    valueRange = 0.8f..1.3f,
                    steps = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("A", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("A", style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ── App Info ──────────────────────────────────
            SectionHeader(title = "About")

            ListItem(
                headlineContent = { Text("App Version") },
                supportingContent = { Text("1.0.0 — SamacharDaily") },
                leadingContent = {
                    Icon(Icons.Filled.Info, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )

            ListItem(
                headlineContent = { Text("News Sources") },
                supportingContent = { Text("NDTV, AajTak, The Hindu, Dainik Bhaskar, Telugu360 + more") },
                leadingContent = {
                    Icon(Icons.Filled.Language, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
    SamacharDailyTheme {
        Box(Modifier.fillMaxSize()) { Text("Profile Screen", Modifier.padding(16.dp)) }
    }
}
