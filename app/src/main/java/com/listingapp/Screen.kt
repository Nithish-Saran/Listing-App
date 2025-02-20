package com.listingapp

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val label: String? = null,
    val navArguments: List<NamedNavArgument> = emptyList(),
) {
    data object UserList : Screen(
        route = "userList"
    )
    data object SplashScreen : Screen(
        route = "splash"
    )

    data object UserDetails : Screen(
        route = "userDetails/{userId}",
        label = "User Details",
        navArguments = listOf(
            navArgument("userId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(userId: String) = "userDetails/$userId"
    }
}
