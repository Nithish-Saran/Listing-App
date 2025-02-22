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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.listingapp.ui.theme.ListingAppTheme

@Composable
fun UserDetails(
    app: ListApp,
    id: String,
    topBarState: MutableState<AppBarViewState>,
) {
    val viewModel: AppViewModel = hiltViewModel()
    val user by viewModel.getUserById(id).collectAsState(initial = null)
    var status by remember { mutableStateOf("Unknown") }
    var degree by remember { mutableStateOf(0) }
    var image by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(Unit) {
        latitude = user?.latitude?: 0.0
        longitude = user?.longitude ?: 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
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
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.pictureMedium)
                            .crossfade(true)
                            .build(),
                        contentDescription = "User Profile Picture",
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
                        horizontalAlignment = Alignment.Start
                    ) {
                        InfoRow("Name", "${user.firstName} ${user.lastName}")
                        InfoRow("Email", user.email)
                        InfoRow("Age", user.age.toString())
                        InfoRow("Phone", user.phone)
                        InfoRow("Location", "${user.country}, ${user.state}")
                    }
                }

                // Weather Info Row
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
                            text = "${degree}\u00B0 ${user?.city}",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Text(
                            text =  status,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(weatherIcon(image)))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(latitude, longitude) {
        viewModel.getUserWeatherData(app, latitude, longitude) { temp, icon, desc ->
            degree = temp
            image = icon
            degree = temp
            status = desc
        }
        topBarState.value = AppBarViewState.getTitileWithBack("User Details")
    }
}


@Composable
fun InfoRow(label: String, value: String) {
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


@Preview(showBackground = true, showSystemUi = true,)
@Composable
fun UserDetailPreview() {
    ListingAppTheme {
        UserDetails(
            app = ListApp(),
            id = "1",
            topBarState = remember { mutableStateOf(AppBarViewState.getTitileWithBack("Listing App")) }
        )
    }
}
