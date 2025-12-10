package com.kmp.vayone

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.*
import platform.Foundation.NSError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.cinterop.useContents
import platform.darwin.NSObject

// 全局单例 CLLocationManager
private val locationManager = CLLocationManager()

private var locationDelegate: LocationDelegate? = null

@OptIn(ExperimentalForeignApi::class)
class LocationDelegate(
    val continuation: kotlinx.coroutines.CancellableContinuation<Pair<Double, Double>?>
) : NSObject(), CLLocationManagerDelegateProtocol {

    private var resumed = false

    private fun resumeOnce(value: Pair<Double, Double>?) {
        if (!resumed) {
            resumed = true
            continuation.resume(value)
            locationDelegate = null
        }
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation
        val coord = location?.coordinate()
        resumeOnce(coord?.let { Pair(it.useContents { latitude }, it.useContents { longitude }) })
        manager.stopUpdatingLocation()
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        resumeOnce(null)
        manager.stopUpdatingLocation()
    }
}


actual suspend fun getLastKnownLocation(): Pair<Double, Double>? =
    suspendCancellableCoroutine { continuation ->
        val status = CLLocationManager.authorizationStatus()
        val hasLocationPermission = (status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                status == kCLAuthorizationStatusAuthorizedAlways)

        if (!hasLocationPermission) {
            // 无权限直接返回 null
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        // 设置 delegate 并启动定位
        locationDelegate = LocationDelegate(continuation)
        locationManager.delegate = locationDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyKilometer
        locationManager.requestLocation()

        continuation.invokeOnCancellation {
            locationManager.stopUpdatingLocation()
            locationDelegate = null
        }
    }
