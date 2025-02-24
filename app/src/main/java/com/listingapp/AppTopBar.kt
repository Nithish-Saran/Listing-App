package com.listingapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.listingapp.ui.theme.ListingAppTheme
import com.listingapp.viewstate.AppBarState
import com.listingapp.viewstate.AppBarViewState

/**
 * A composable function that represents a customizable top app bar.
 *
 * @param state The current state of the AppBar, defining its left and right content.
 * @param navController The NavController for handling navigation actions.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    state: AppBarViewState,
    navController: NavController
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {},
        navigationIcon = { LeftAppBarContent(state.left, navController) },
        actions = { RightAppBarContent(state.right) }
    )
}

@Composable
fun LeftAppBarContent(left: AppBarState.Left, navController: NavController) {
    when (left) {
        is AppBarState.Left.BackWith1Title -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = left.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        is AppBarState.Left.TitleOnly -> {
            Text(
                text = left.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun RightAppBarContent(right: AppBarState.Right) {
    when (right) {
        is AppBarState.Right.Weather -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${right.degree ?: ""}\u00B0 ${right.city}",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = right.status ?: "Unknown",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                }

                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(weatherIcon(right.image!!))
                )
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        AppBarState.Right.None -> {} // No action
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopBarPreview() {
    ListingAppTheme {
        val nav = rememberNavController()
        AppTopBar(
            state = AppBarViewState(
                left = AppBarState.Left.TitleOnly("Listing App"),
                right = AppBarState.Right.Weather(
                    degree = 31,
                    city = "Coimbatore",
                    status = "Cloud",
                    image = ""
                )
            ),
            navController = nav
        )
    }
}