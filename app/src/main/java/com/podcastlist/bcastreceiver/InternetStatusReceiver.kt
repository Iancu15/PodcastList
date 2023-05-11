package com.podcastlist.bcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import com.podcastlist.MainActivityViewModel

class InternetStatusReceiver(val viewModel: MainActivityViewModel) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("InternetStatusReceiver", "Internet is available")
                viewModel.isInternetAvailable.value = true
            }

            override fun onLost(network: Network) {
                Log.d("InternetStatusReceiver", "Internet is unavailable")
                viewModel.isInternetAvailable.value = false
            }
        })
    }

}