package com.sagoss.kiosklock.startup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sagoss.kiosklock.MainActivity


class StartupAtBootReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(i)
        }
    }
}