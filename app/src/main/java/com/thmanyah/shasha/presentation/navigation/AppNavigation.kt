package com.thmanyah.shasha.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thmanyah.shasha.R
import com.thmanyah.shasha.presentation.components.MiniPlayer
import com.thmanyah.shasha.presentation.components.TopBar
import com.thmanyah.shasha.presentation.home.HomeScreen
import com.thmanyah.shasha.presentation.home.HomeViewModel
import com.thmanyah.shasha.presentation.search.SearchScreen
import com.thmanyah.shasha.presentation.search.SearchViewModel

sealed class Screen(
    val route: String,
    @StringRes val labelRes: Int,
    val iconRes: Int,
    val badgeCount: Int = 0,
) {
    data object Home : Screen("home", R.string.nav_home, R.drawable.ic_home)
    data object Search : Screen("search", R.string.nav_search, R.drawable.ic_search)
    data object Library : Screen("library", R.string.nav_library, R.drawable.ic_library)
    data object Community : Screen("community", R.string.nav_community, R.drawable.ic_community, badgeCount = 3)
    data object Profile : Screen("profile", R.string.nav_profile, R.drawable.ic_profile)
}

private val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Community,
    Screen.Library,
    Screen.Profile,
)

@Composable
fun AppNavigation(
    onToggleLanguage: () -> Unit = {},
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(onToggleLanguage = onToggleLanguage)
        },
        bottomBar = {
            Column {
                MiniPlayer()

                // Subtle divider line above nav bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                )

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp,
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true
                        val label = stringResource(screen.labelRes)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (screen.badgeCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                                Text(
                                                    text = screen.badgeCount.toString(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                )
                                            }
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(id = screen.iconRes),
                                            contentDescription = label,
                                            modifier = Modifier.size(24.dp),
                                        )
                                    }
                                } else {
                                    Icon(
                                        painter = painterResource(id = screen.iconRes),
                                        contentDescription = label,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                indicatorColor = MaterialTheme.colorScheme.background,
                            ),
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(Screen.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(viewModel = viewModel)
            }
            composable(Screen.Search.route) {
                val viewModel = hiltViewModel<SearchViewModel>()
                SearchScreen(viewModel = viewModel)
            }
            composable(Screen.Library.route) {
                PlaceholderScreen(title = stringResource(R.string.nav_library))
            }
            composable(Screen.Community.route) {
                PlaceholderScreen(title = stringResource(R.string.nav_community))
            }
            composable(Screen.Profile.route) {
                PlaceholderScreen(title = stringResource(R.string.nav_profile))
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
