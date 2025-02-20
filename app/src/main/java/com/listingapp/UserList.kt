package com.listingapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.listingapp.ui.theme.ListingAppTheme

@Composable
fun UserList(
    app: ListApp,
    topBarState: MutableState<AppBarViewState>,
    onReturn: () -> Unit
) {
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    val viewModel: AppViewModel = viewModel()
    var isLocationAvailable by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

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
            modifier = Modifier
                .fillMaxWidth(),
            onQueryChange = {searchQuery = it}
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(100) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReturn() }
                        .height((100..250).random().dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        //Text(text = item, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    LaunchedEffect(isLocationAvailable) {
        if (isLocationAvailable) {
            viewModel.getWeather(
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
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(Color.Black,),
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