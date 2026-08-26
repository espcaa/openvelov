package fish.alice.openvelov.ui.screens.onboarding

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import fish.alice.openvelov.utils.LOCATION_PERMISSIONS
import fish.alice.openvelov.utils.NEARBY_PERMISSIONS
import fish.alice.openvelov.utils.allGranted
import fish.alice.openvelov.utils.hasPermission


fun openPermissionSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Composable
fun rememberPermissionState(
    context: Context,
    permissions: Array<String>,
): MutableState<Boolean> {
    val granted = remember { mutableStateOf(allGranted(context, permissions)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                granted.value = allGranted(context, permissions)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return granted
}

private fun shouldGoToSettings(
    activity: Activity?,
    permissions: Array<String>,
    hasAsked: Boolean,
): Boolean {
    if (activity == null || !hasAsked) return false
    return permissions
        .filterNot { hasPermission(activity, it) }
        .none { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
}

@Composable
fun PermissionScreen(
    onNext: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var locationGranted by rememberPermissionState(context, LOCATION_PERMISSIONS)
    var nearbyGranted by rememberPermissionState(context, NEARBY_PERMISSIONS)

    var locationAsked by remember { mutableStateOf(false) }
    var nearbyAsked by remember { mutableStateOf(false) }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results -> locationGranted = results.values.all { it } }

    val nearbyLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results -> nearbyGranted = results.values.all { it } }

    val locationLabel = when {
        locationGranted -> "Location granted"
        shouldGoToSettings(activity, LOCATION_PERMISSIONS, locationAsked) -> "Open settings for location"
        else -> "Grant location"
    }
    val nearbyLabel = when {
        nearbyGranted -> "Nearby devices granted"
        shouldGoToSettings(activity, NEARBY_PERMISSIONS, nearbyAsked) -> "Open settings for nearby devices"
        else -> "Grant nearby devices"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !locationGranted,
            onClick = {
                if (shouldGoToSettings(activity, LOCATION_PERMISSIONS, locationAsked)) {
                    openPermissionSettings(context)
                } else {
                    locationAsked = true
                    locationLauncher.launch(LOCATION_PERMISSIONS)
                }
            },
        ) { Text(locationLabel) }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !nearbyGranted,
            onClick = {
                if (shouldGoToSettings(activity, NEARBY_PERMISSIONS, nearbyAsked)) {
                    openPermissionSettings(context)
                } else {
                    nearbyAsked = true
                    nearbyLauncher.launch(NEARBY_PERMISSIONS)
                }
            },
        ) { Text(nearbyLabel) }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = locationGranted && nearbyGranted,
            onClick = onNext,
        ) { Text("Continue") }
    }
}
