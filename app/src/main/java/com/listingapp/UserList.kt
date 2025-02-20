package com.listingapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.listingapp.db.entity.UserEntity
import com.listingapp.ui.theme.ListingAppTheme
import com.valentinilk.shimmer.shimmer

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

    val loadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            (lastVisibleItem != null && lastVisibleItem.index >= totalItems - 5)
        }
    }

    Location {
        lat, lon ->
        latitude = lat
        longitude = lon
        isLocationAvailable = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.onSurface
            )
    ) {
        SearchBar(
            query = searchQuery,
            modifier = Modifier.fillMaxWidth(),
            onQueryChange = { newQuery ->
                searchQuery = newQuery
                viewModel.searchUsers(newQuery)
            }
        )

        when (val state = userDataViewState) {
            is UserDataState.UserListState.Loading -> {
                ListLoading()
            }
            is UserDataState.UserListState.NoData -> {

            }
            is UserDataState.UserListState.Success -> {
                LazyVerticalStaggeredGrid(
                    state = listState,
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalItemSpacing = 8.dp,
                    flingBehavior = ScrollableDefaults.flingBehavior(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()
                    items(state.data) { usersData ->
                        UserCard(usersData) {
                            onReturn(it)
                        }
                    }
                }
            }

            UserDataState.UserListState.Error -> {

            }
        }

    }
    LaunchedEffect(loadMore.value) {
        if (userDataViewState is UserDataState.UserListState.Success) {
            if (loadMore.value || (userDataViewState as UserDataState.UserListState.Success).data.isEmpty()) {
                viewModel.addUser()
            }
        }
    }


    LaunchedEffect(key1 = Unit) {
        viewModel.addUser()
        viewModel.getAllUser()
    }

    LaunchedEffect(isLocationAvailable) {
        if (isLocationAvailable) {
            viewModel.getLocalWeather(
                lat = latitude, lon = longitude, app = app, topAppBarState = topBarState
            )
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
    val cardHeight = remember { (100..250).random().dp }
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
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.pictureMedium)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(cardHeight * 0.5f)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop,
                placeholder =  painterResource(R.drawable.photo),
                error = painterResource(R.drawable.photo)
            )

            Text(
                text = "${data.firstName} ${data.lastName}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
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

@Preview(showBackground = true, showSystemUi = true,)
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