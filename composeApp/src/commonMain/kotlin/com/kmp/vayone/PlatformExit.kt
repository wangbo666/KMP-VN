package com.kmp.vayone

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.COARSE_LOCATION

expect fun exitApp()

expect fun currentTimeMillis(): Long

expect fun convertToMD5(t: String): String

expect fun mobileType(): String


class MyViewModel(val permissionsController: PermissionsController) {
    suspend fun requestLocation() {
        try {
            permissionsController.providePermission(Permission.COARSE_LOCATION)
            // 权限通过，可以继续逻辑
        } catch (e: DeniedAlwaysException) {
            // 永久拒绝
        } catch (e: DeniedException) {
            // 暂时拒绝
        }
    }
}

