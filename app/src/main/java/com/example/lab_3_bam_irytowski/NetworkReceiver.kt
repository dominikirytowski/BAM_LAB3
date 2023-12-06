package com.example.lab_3_bam_irytowski

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Receiver", "is invoked!")
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        Log.d("Connection info", "Is connected: ${networkInfo?.isConnected}")
        Log.d("Connection info", "Type: ${networkInfo?.type} ${ConnectivityManager.TYPE_WIFI} ")
    }
}