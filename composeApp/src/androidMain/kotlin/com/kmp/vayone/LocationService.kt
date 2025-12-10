package com.kmp.vayone

import android.content.Context
import android.location.LocationManager
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun getLastKnownLocation(): Pair<Double, Double>? = withContext(Dispatchers.IO) {
    if (!XXPermissions.isGrantedPermissions(
            MainActivity.instance,
            listOf(PermissionLists.getAccessCoarseLocationPermission())
        )
    ) {
        return@withContext null
    }

    val locationManager =
        MainActivity.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    try {
        // 优先使用网络定位（模糊定位）
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        location?.let {
            Pair(
                it.longitude,
                it.latitude
            )
        }
    } catch (e: SecurityException) {
        null
    }
}
