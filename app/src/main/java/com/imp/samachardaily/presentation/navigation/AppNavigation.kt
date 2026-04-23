package com.imp.samachardaily.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.imp.samachardaily.R
import com.imp.samachardaily.presentation.article.ArticleDetailScreen
import com.imp.samachardaily.presentation.bookmarks.BookmarksScreen
import com.imp.samachardaily.presentation.home.HomeScreen
import com.imp.samachardaily.presentation.onboarding.OnboardingScreen
import com.imp.samachardaily.presentation.profile.ProfileScreen
import com.imp.samachardaily.presentation.search.SearchScreen
import com.imp.samachardaily.presentation.video.ShortVideoScreen

data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, R.string.nav_home, Icons.Filled.Home),
        BottomNavItem(Screen.Search.route, R.string.nav_search, Icons.Filled.Search),
        BottomNavItem(Screen.ShortVideo.route, R.string.nav_video, Icons.Filled.PlayArrow),
        BottomNavItem(Screen.Profile.route, R.string.nav_profile, Icons.Filled.Person)
    )

    val bottomNavRoutes = bottomNavItems.map { it.route }.toSet()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelRes)) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onArticleClick = { articleId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onArticleClick = { articleId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                    }
                )
            }

            composable(Screen.ShortVideo.route) {
                ShortVideoScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBookmarksClick = { navController.navigate(Screen.Bookmarks.route) }
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    onBack = { navController.navigateUp() },
                    onArticleClick = { articleId ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                    }
                )
            }

            composable(
                route = Screen.ArticleDetail.route,
                arguments = listOf(navArgument("articleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getString("articleId") ?: return@composable
                ArticleDetailScreen(
                    articleId = articleId,
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}

