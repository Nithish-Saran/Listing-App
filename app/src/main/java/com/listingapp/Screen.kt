package com.listingapp

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList(),
) {
    /**
     * Represents the User List screen.
     * This screen does not require any navigation arguments.
     */
    data object UserList : Screen(
        route = "userList"
    )

    /**
     * Represents the Splash screen.
     * This screen serves as the entry point and does not take any arguments.
     */
    data object SplashScreen : Screen(
        route = "splash"
    )

    /**
     * Represents the User Details screen.
     * This screen requires a `userId` as a navigation argument.
     *
     * @property route The navigation route, which includes a placeholder for `userId`.
     * @property navArguments A list of required navigation arguments, which includes `userId` of type `String`.
     */
    data object UserDetails : Screen(
        route = "userDetails/{userId}",
        navArguments = listOf(
            navArgument("userId") { type = NavType.StringType }
        )
    ) {
        /**
         * Helper function to create a route string for navigation.
         * Ensures `userId` is properly inserted into the navigation route.
         *
         * @param userId The unique identifier for the user.
         * @return A properly formatted route string including the `userId`.
         */
        fun createRoute(userId: String) = "userDetails/$userId"
    }
}

