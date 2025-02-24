package com.listingapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.listingapp.ui.theme.ListingAppTheme
import com.listingapp.viewmodel.AppViewModel
import com.listingapp.viewstate.AppBarViewState

/**
 * Composable function to display detailed user information, including profile picture,
 * personal details, and weather information based on the user's location.
 *
 * @param app The application instance for managing app-level operations.
 * @param id The unique identifier of the user whose details are displayed.
 * @param topBarState The mutable state for managing the app bar's UI.
 */

@Composable
fun UserDetails(
    app: ListApp,
    id: String,
    topBarState: MutableState<AppBarViewState>,
) {
    //viewModel related state
    val viewModel: AppViewModel = hiltViewModel()

    // user id related state
    val user by viewModel.getUserById(id).collectAsState(initial = null)

    // weather data related state
    var status by remember { mutableStateOf("Unknown") }
    var degree by remember { mutableStateOf(0) }
    var image by remember { mutableStateOf("") }

    val context = LocalContext.current

    // UI for user details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                user?.let { user ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(user.pictureMedium)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(R.string.user_profile_picture),
                        modifier = Modifier
                            .size(300.dp)
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(10))
                            .background(Color.Gray.copy(alpha = 0.2f)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.image),
                        error = painterResource(R.drawable.image)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        InfoRow(
                            stringResource(R.string.label_name),
                            "${user.firstName} ${user.lastName}"
                        )
                        InfoRow(stringResource(R.string.label_email), user.email)
                        InfoRow(stringResource(R.string.label_age), user.age.toString())
                        InfoRow(stringResource(R.string.label_phone), user.phone)
                        InfoRow(
                            stringResource(R.string.label_location),
                            "${user.country}, ${user.state}"
                        )
                    }
                }

                // Weather Info Row
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        //modifier = Modifier.weight(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(
                                R.string.temperature_format,
                                degree,
                                user?.city ?: ""
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = status,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(
                            weatherIcon(image)
                        )
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getUserWeatherData(
            app,
            user?.latitude ?: 0.0,
            user?.longitude ?: 0.0
        ) { temp, icon, desc ->
            degree = temp
            image = icon
            status = desc
        }
        topBarState.value =
            AppBarViewState.getTitleWithBack(context.getString(R.string.user_details_title))
    }
}

// UI view
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(0.15f)
        )
        Text(
            text = ":",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserDetailPreview() {
    ListingAppTheme {
        UserDetails(
            app = ListApp(),
            id = "1",
            topBarState = remember { mutableStateOf(AppBarViewState.getTitleWithBack("Listing App")) }
        )
    }
}
