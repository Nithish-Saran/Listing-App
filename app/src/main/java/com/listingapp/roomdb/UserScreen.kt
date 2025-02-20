package com.listingapp.roomdb

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun UserScreen(viewModel: UserViewModel, modifier: PaddingValues) {
    val user by viewModel.userData.collectAsState()

    user?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(it.pictureLarge),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "${it.title} ${it.firstName} ${it.lastName}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Age: ${it.age}", fontSize = 18.sp)
            Text(text = "Email: ${it.email}", fontSize = 18.sp)
            Text(text = "Phone: ${it.phone}", fontSize = 18.sp)
            Text(text = "Cell: ${it.cell}", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            //Text(text = "Location: ${it.street}, ${it.city}, ${it.state}, ${it.country}", fontSize = 16.sp)
            //Text(text = "Timezone: ${it.timezone}", fontSize = 16.sp)
            Text(text = "Coordinates: ${it.latitude}, ${it.longitude}", fontSize = 16.sp)
            Log.d("APP", "User: $it")
        }
    } ?: run {
        CircularProgressIndicator()
    }
}
