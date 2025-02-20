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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.listingapp.ui.theme.ListingAppTheme

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
        navigationIcon = {
            when (val left = state.left) {
                is AppBarState.Left.BackWith1Title -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        Text(
                            text = left.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }

                is AppBarState.Left.TitleOnly -> {
                    Text(
                        text = left.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
                }
            }
        },
        actions = {
            when (val right = state.right) {
                is AppBarState.Right.Weather -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentSize(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${right.degree ?: ""}\u00B0 ${right.city}",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                            )
                            Text(
                                text = right.status ?: "",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                            )
                        }

                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/${right.image ?: ""}.png",
                            contentDescription = "weather Icon",
                            modifier = Modifier
                                .padding(end = 16.dp, start = 8.dp)
                                .size(52.dp),
                        )
                    }
                }

                AppBarState.Right.None -> {}
            }
        }
    )
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