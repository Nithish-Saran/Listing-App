package com.listingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.listingapp.ui.theme.ListingAppTheme
import com.listingapp.viewstate.AppBarState
import com.listingapp.viewstate.AppBarViewState
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity is the entry point of the application.
 * It sets up Jetpack Compose UI, navigation, and manages the app's top bar.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Customize the status bar color
        this.setStatusBarColor(R.color.status_bar)

        setContent {
            ListingAppTheme {
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryAsState()

                // Check if the current screen is the SplashScreen
                val isSplashScreen =
                    currentDestination?.destination?.route == Screen.SplashScreen.route

                // Manage the state of the Top Bar
                val topBarState = remember {
                    mutableStateOf(
                        AppBarViewState(
                            left = AppBarState.Left.TitleOnly("Listing App"),
                            right = AppBarState.Right.None
                        )
                    )
                }

                // Scaffold provides structure with a top bar and content area
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (!isSplashScreen) {
                            AppTopBar(
                                state = topBarState.value,
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        navController = navController,
                        startDestination = Screen.SplashScreen.route
                    ) {
                        // Navigation destination: Splash Screen
                        composable(Screen.SplashScreen.route) {
                            SplashScreen {
                                navController.navigate(Screen.UserList.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            }
                        }

                        // Navigation destination: User List Screen
                        composable(
                            route = Screen.UserList.route,
                        ) {
                            UserList(
                                app = ListApp(),
                                topBarState = topBarState
                            ) {
                                navController.navigate(Screen.UserDetails.createRoute(it))
                            }
                        }

                        // Navigation destination: User Details Screen
                        composable(
                            route = Screen.UserDetails.route,
                            arguments = Screen.UserDetails.navArguments
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            UserDetails(
                                id = userId,
                                app = ListApp(),
                                topBarState = topBarState,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListingAppTheme {

    }
}