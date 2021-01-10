package com.sagoss.kiosklock

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context


class DeviceAdminReceiver : DeviceAdminReceiver() {
    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context.applicationContext, DeviceAdminReceiver::class.java)
        }
    }
}