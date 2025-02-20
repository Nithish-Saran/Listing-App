package com.listingapp

import androidx.navigation.NamedNavArgument

sealed class Screen(
    val route: String,
    val label: String? = null,
    val navArguments: List<NamedNavArgument> = emptyList(),
) {
    data object UserList : Screen(
        route = "userList",
        label = "User List"
    )
    data object UserDetails : Screen(
        route = "userDetails",
        label = "User Details"
    )
}