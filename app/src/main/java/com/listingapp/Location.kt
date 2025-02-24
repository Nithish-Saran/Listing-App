package com.listingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

/**
 * A composable function that requests location permission from the user.
 *
 * @param onGranted A callback function that is triggered when the location permission is granted.
 */
@Composable
fun Location(onGranted: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    var hasLocationPermission by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Permission launcher for requesting location permission dynamically
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                onGranted() // Callback triggered when permission is granted
            } else {
                val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (!showRationale) {
                    // User has permanently denied the permission and needs to enable it manually
                    showSettingsDialog = true
                }
            }
        }
    )

    // Check for location permission on first composition
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(permission)
        }
    }
}

/**
 * A composable function that listens for location provider changes and updates the UI accordingly.
 *
 * @param context The application context used for registering the broadcast receiver.
 * @param onLocationChanged A callback function triggered when location settings change.
 */
@Composable
fun LocationReceiver(
    context: Context,
    onLocationChanged: () -> Unit
) {
    // BroadcastReceiver to listen for location provider status changes
    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    onLocationChanged() // Callback triggered when location provider status changes
                }
            }
        }
    }

    // Register the BroadcastReceiver when the composable is first composed
    DisposableEffect(Unit) {
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, filter)

        // Unregister the receiver when the composable leaves the composition
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}



