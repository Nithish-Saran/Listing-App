package com.listingapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
import com.listingapp.db.entity.UserEntity
import com.listingapp.ui.theme.ListingAppTheme
import com.listingapp.viewmodel.AppViewModel
import com.listingapp.viewstate.AppBarViewState
import com.listingapp.viewstate.UserDataState
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

/**
 * A composable function that displays a list of users.
 *
 * It handles:
 * - Fetching user data with pagination.
 * - Searching users.
 * - Handling network connectivity.
 * - Retrieving and updating location for weather data.
 *
 * @param app The [ListApp] instance for application-level operations.
 * @param topBarState Mutable state for managing the app bar UI state.
 * @param onReturn Callback function invoked when a user is selected.
 */

@Composable
fun UserList(
    app: ListApp,
    topBarState: MutableState<AppBarViewState>,
    onReturn: (String) -> Unit
) {
    // Location-related states
    var locationPermissionGranted by remember { mutableStateOf(false) }

    //To remember that the location alert dialog has been shown,
    var isLocationEnabled by remember { mutableStateOf(false) }

    // viewModel and data
    val viewModel: AppViewModel = hiltViewModel()
    val userDataViewState by viewModel.userDataState.collectAsState()

    // UI states
    var searchQuery by remember { mutableStateOf("") }
    var previousQuery by remember { mutableStateOf(searchQuery) }
    var noNetworkDialog by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    /**
     * Updates the location by fetching latitude and longitude.
     * If location services are disabled, it fetches local weather data.
     */
    fun updateLocation() {
        if (isLocationEnabled(context)) {
            fetchLocation(context, fusedLocationClient) { lat, lon ->
                viewModel.getWeather(
                    app = app,
                    lat = lat,
                    lon = lon,
                    topBarState = topBarState,
                )
            }
        } else {
            viewModel.getLocalWeather(app, topBarState)
        }
    }

    // search query debounce handling
    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(500)  // debounce to prevent the excessive API calls
            .collectLatest { query ->
                if (query.isNotEmpty()) {
                    viewModel.searchUsers(query)
                } else if (previousQuery.isNotEmpty()) {
                    viewModel.fetchUsers()
                }
                previousQuery = query
            }
    }

    // Connectivity manager and network callback
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback = rememberUpdatedState(
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                noNetworkDialog = false
                showLoading = false // Hide loading when internet is back
                viewModel.addNewUsers() // Reload user data automatically
            }
        }
    )

    // Location receiver listen the location change and update
    LocationReceiver(context) {
        if (context.isInternetAvailable()) {
            updateLocation()
        }
    }

    // Unregister network callback when the composable is disposed
    DisposableEffect(Unit) {
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback.value)
        onDispose {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback.value)
            } catch (e: Exception) {
                Log.d("NetworkCallback", "Failed to unregister network callback: ${e.message}")
            }
        }
    }

    // Dialog for no internet connection
    if (noNetworkDialog) {
        NoNetworkDialog {
            noNetworkDialog = false
            showLoading = true
        }
    }

    // show loading state
    if (showLoading) ListLoading()

    //Request location permission
    if (context.isInternetAvailable()) Location { locationPermissionGranted = true }

    // Check location permission and location is enabled
    if (locationPermissionGranted && !isLocationEnabled) {
        if (!isLocationEnabled(context)) {
            showEnableLocationDialog(context) {
                isLocationEnabled = true
                updateLocation()
            }
        }
        updateLocation()
        isLocationEnabled = true
    } else {
        viewModel.getLocalWeather(app, topBarState)
    }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSurface)
    ) {
        SearchBar(
            query = searchQuery,
            modifier = Modifier.fillMaxWidth(),
            onQueryChange = { newQuery -> searchQuery = newQuery }
        )

        when (userDataViewState) {
            is UserDataState.UserListState.Loading -> ListLoading()
            is UserDataState.UserListState.NoNetwork -> {
                NoData()
                noNetworkDialog = true
            }

            is UserDataState.UserListState.NoData -> NoData()
            is UserDataState.UserListState.Success -> {
                UserGridView(
                    users = viewModel.usersList,
                    onReturn = onReturn,
                    viewModel = viewModel
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback.value)

        viewModel.fetchUsers(context.isInternetAvailable())
        if (context.isInternetAvailable()) updateLocation()
        else viewModel.getLocalWeather(app, topBarState)
    }
}

/**
 * Composable function to display a staggered grid of user cards.
 */
@Composable
private fun UserGridView(
    users: List<UserEntity>,
    onReturn: (String) -> Unit,
    viewModel: AppViewModel
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalItemSpacing = 8.dp,
        flingBehavior = ScrollableDefaults.flingBehavior(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(users) { index, it ->
            UserCard(it) { userid ->
                onReturn(userid)
            }
            if (index == users.size - 5) {
                viewModel.loadMoreItems()
            }
        }
    }
}

/**
 * Composable function that displays a search bar.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search...",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        }
    }
}

/**
 * Composable function that displays a card.
 */
@Composable
private fun UserCard(data: UserEntity, onclick: (String) -> Unit) {
    val cardHeight = remember { (100..300).random().dp }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onclick(data.uuid) }
            .height(cardHeight),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.pictureMedium)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(cardHeight * 0.5f)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = { Image(
                    painter = painterResource(R.drawable.placeholder),
                    contentDescription = "Placeholder Image",
                    modifier = Modifier
                        .size(cardHeight * 0.5f)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                ) }
            )

            Text(
                text = "${data.firstName} ${data.lastName}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Composable function that show loading.
 */
@Composable
private fun ListLoading() {
    Spacer(modifier = Modifier.height(12.dp))

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(20) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(32.dp)
                    .shimmer()
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
            repeat(6) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(128.dp)
                        .shimmer()
                        .background(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

    }
}

/**
 * Composable function that displays a no data.
 */
@Composable
private fun NoData() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.no_user),
            contentDescription = "no user",
            modifier = Modifier
                .size(128.dp)
        )
        Text(
            text = stringResource(R.string.no_user),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Displays an alert dialog when there is no internet connection.
 */
@Composable
private fun NoNetworkDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.no_internet),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(R.string.no_data),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall
            )
        },
        containerColor = MaterialTheme.colorScheme.onPrimary,
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(
                    text = stringResource(R.string.ok),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserListPreview() {
    ListingAppTheme {
        UserList(
            app = ListApp(),
            topBarState = remember { mutableStateOf(AppBarViewState.getTitle("Listing App")) },
            onReturn = {},
        )
    }
}