package fish.alice.openvelov.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


val NEARBY_PERMISSIONS: Array<String> =
    arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
    )

val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

fun hasPermission(context: Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

fun allGranted(context: Context, permissions: Array<String>) =
    permissions.all { hasPermission(context, it) }

fun hasLocationPermissions(context: Context) = allGranted(context, LOCATION_PERMISSIONS)

fun hasNearbyPermissions(context: Context) = allGranted(context, NEARBY_PERMISSIONS)

fun hasAllPermissions(context: Context) =
    hasLocationPermissions(context) && hasNearbyPermissions(context)