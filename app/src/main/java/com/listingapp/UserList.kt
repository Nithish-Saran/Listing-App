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
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.listingapp.db.entity.UserEntity
import com.listingapp.ui.theme.ListingAppTheme
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
fun UserList(
    app: ListApp,
    topBarState: MutableState<AppBarViewState>,
    onReturn: (String) -> Unit
) {
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    val viewModel: AppViewModel = hiltViewModel()
    var isLocationAvailable by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val userDataViewState by viewModel.userDataState.collectAsState()
    val listState = LazyStaggeredGridState()
    var debouncedQuery by remember { mutableStateOf("") }
    var previousQuery by remember { mutableStateOf(searchQuery) }
    var offset by remember { mutableIntStateOf(0) }
    var limit by remember { mutableIntStateOf(10) }

    var showDialog by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // For example, assume pageSize is fixed:
    val pageSize = 25
    var currentOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(500)
            .collectLatest { query ->
                if (query.isNotEmpty()) {
                    debouncedQuery = query
                    viewModel.searchUsers(debouncedQuery)
                } else if (previousQuery.isNotEmpty()) {
                    // Transitioned from a non-empty query to empty:
                    viewModel.getAllUser()
                    //currentOffset = 0
                    //viewModel.getAllUsers(currentOffset, pageSize)
                }
                previousQuery = query
            }
    }

    // Track last visible item dynamically for pagination
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.layoutInfo }
//            .collectLatest { layoutInfo ->
//                val totalItems = layoutInfo.totalItemsCount
//                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//
//                // Trigger pagination when near the end
//                if (lastVisibleItem >= totalItems - 5 && totalItems > 0) {
//                    offset = limit
//                    limit += 10
//                    viewModel.getAllUsers(offset, limit)
//                }
//            }
//    }

    // Track last visible item for pagination
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.layoutInfo }
//            .collectLatest { layoutInfo ->
//                val totalItems = layoutInfo.totalItemsCount
//                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//                // Check if we are near the end
//                if (lastVisibleItem >= totalItems - 5 && totalItems > 0) {
//                    // Use the total count from viewModel
//                    if (totalItems < viewModel.totalUserCount) {
//                        currentOffset += pageSize
//                        viewModel.getAllUsers(currentOffset, pageSize)
//                    }
//                }
//            }
//    }

    // Connectivity manager and network callback
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCallback = rememberUpdatedState(
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                showLoading = false // Hide loading when internet is back
                viewModel.getAllUser() // Reload user data automatically
                //currentOffset = 0
                //viewModel.getAllUsers(currentOffset, pageSize)
                if (isLocationAvailable) {  // when internet is back and location is turned on it automatically updates the weather
                    viewModel.getWeather(
                        app = app,
                        lat = latitude,
                        lon = longitude
                    ) { temp, icon, desc, city ->
                        topBarState.value = AppBarViewState.getLocalWeather(
                            title = "Listing App",
                            degree = temp,
                            city = city,
                            status = desc,
                            image = icon
                        )
                    }
                } else {
                    topBarState.value = AppBarViewState.getTitle("Listing App")
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                //showDialog = true // Show dialog when internet is lost
            }
        }
    )

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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "No Internet Connection",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Please turn on Mobile Data or WiFi to continue.",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
           },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    showLoading = true // Start showing loading indicator
                }) {
                    Text(
                        text = "OK",
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
    if (showLoading) ListLoading()

    Location { lat, lon ->
        latitude = lat
        longitude = lon
        isLocationAvailable = true
    }

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

        when (val state = userDataViewState) {
            is UserDataState.UserListState.Loading -> ListLoading()
            is UserDataState.UserListState.NoData -> NoData()
            is UserDataState.UserListState.Success -> {
                LazyVerticalStaggeredGrid(
                    //state = listState,
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalItemSpacing = 8.dp,
                    flingBehavior = ScrollableDefaults.flingBehavior(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data) { user ->
                        UserCard(user) { user_id ->
                            onReturn(user_id)
                        }
                    }
                }
            }
        }
    }

    // Register network callback
    LaunchedEffect(Unit) {
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback.value)

        if (!context.isInternetAvailable()) {
            viewModel.getAllUser()
        } else if (!viewModel.apiCalled) {      // This condition is to make Api call at once while the user opens the app, very first
            viewModel.apiCalled = true
            viewModel.addUser()
            //viewModel.getAllUsers(currentOffset, pageSize)
        }
    }

    // Monitor userDataViewState and internet connectivity to decide dialog display.
    LaunchedEffect(userDataViewState) {
        // Show dialog only if there's no data AND no internet.
        showDialog = userDataViewState is UserDataState.UserListState.NoData
                && !context.isInternetAvailable()
    }

    // Update the top bar status
    LaunchedEffect(isLocationAvailable) {
        if (isLocationAvailable) {
            viewModel.getWeather(
                app = app,
                lat = latitude,
                lon = longitude
            ) { temp, icon, desc, city ->
                topBarState.value = AppBarViewState.getLocalWeather(
                    title = "Listing App",
                    degree = temp,
                    city = city,
                    status = desc,
                    image = icon
                )
            }
        } else {
            topBarState.value = AppBarViewState.getTitle("Listing App")
        }
    }
}


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

@Composable
private fun UserCard(data: UserEntity, onclick: (String) -> Unit) {
    val cardHeight = remember { (150..250).random().dp }
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
                error = { painterResource(R.drawable.photo) }
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
            text = "No user found",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserListPreview() {
    ListingAppTheme {
        UserList(
            app = ListApp(),
            topBarState = remember { mutableStateOf(AppBarViewState.getTitle("Listing App")) },
            onReturn = {}
        )
    }
}