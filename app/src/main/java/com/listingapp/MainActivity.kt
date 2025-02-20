package com.listingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.listingapp.ui.theme.ListingAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            ListingAppTheme {
                val navController = rememberNavController()
                val topBarState = remember {
                    mutableStateOf(
                        AppBarViewState(
                            left = AppBarState.Left.TitleOnly("Listing App"),
                            right = AppBarState.Right.None
                        )
                    )
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppTopBar(
                            state = topBarState.value,
                            navController = navController
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        navController = navController,
                        startDestination = Screen.UserList.route
                    ) {
                        composable(
                            route = Screen.UserList.route,
                        ) {
                            UserList(
                                app = ListApp(),
                                topBarState = topBarState
                            ){
                                navController.navigate(Screen.UserDetails.route)
                            }
                        }

                        composable(
                            route = Screen.UserDetails.route,
                        ) {
                            UserDetails(
                                app = ListApp(),
                                topBarState = topBarState,
                                onReturn = {
                                    navController.popBackStack()
                                }
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