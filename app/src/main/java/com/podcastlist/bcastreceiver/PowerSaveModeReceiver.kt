package com.podcastlist.bcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.podcastlist.MainActivityViewModel
import com.podcastlist.service.NotificationService

private const val TAG = "PowerSaveModeReceiver"

class PowerSaveModeReceiver(val viewModel: MainActivityViewModel) : BroadcastReceiver() {
    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
            val pm: PowerManager = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm.isPowerSaveMode) {
                Log.d(TAG, "Power save mode is on")
                viewModel.isPowerSaveModeOn.value = true
            } else {
                Log.d(TAG, "Power save mode is off")
                viewModel.isPowerSaveModeOn.value = false
            }
        }
    }

}