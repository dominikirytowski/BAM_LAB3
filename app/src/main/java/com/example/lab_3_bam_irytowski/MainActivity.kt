package com.example.lab_3_bam_irytowski

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Objects

class MainActivity : ComponentActivity() {

    private val networkReceiver: NetworkReceiver = NetworkReceiver()
    private val READ_CONTACTS_PERMISSION_REQUEST_CODE = 1
    private val READ_CALENDAR_PERMISSION_REQUEST_CODE = 2

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    @SuppressLint("Range", "Recycle")
    fun getContacts() {
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (cursor!!.moveToNext()) {
            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val displayName =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            Log.d("Contact entry", "Contact $contactId $displayName")
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                getContacts()
            } else {
                Toast.makeText(this, "No permission to read contacts!", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == READ_CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You have permission to read calendar!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No permission to read calendar!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivity: MainActivity = this;

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FirstButton()
                Button(onClick = {
                    ActivityCompat.requestPermissions(
                        mainActivity,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        READ_CONTACTS_PERMISSION_REQUEST_CODE
                    )
                }) {
                    Text("Give me contacts!")
                }
                Button(onClick = {
                    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
                    val msg: String = if (Objects.isNull(bluetoothAdapter)){
                        "This device does not support bluetooth"
                    } else {
                        if (bluetoothAdapter!!.isEnabled) "Yes" else "No"
                    }
                    Toast.makeText(mainActivity, "Is bluetooth enabled: $msg", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Give me bluetooth status!")
                }
                Button(onClick = {
                    ActivityCompat.requestPermissions(
                        mainActivity,
                        arrayOf(Manifest.permission.READ_CALENDAR),
                        READ_CALENDAR_PERMISSION_REQUEST_CODE
                    )

                }) {
                    Text("Give me calendar!")
                }
            }
        }

        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun FirstButton() {
    Button(onClick = {
        val url = URL("https://jsonplaceholder.typicode.com/posts")
        GlobalScope.launch {
            with(withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpURLConnection) {
                requestMethod = "GET"
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        Log.d("ACT", line)
                    }
                }
            }
        }
    }) {
        Text("Click me!")
    }
}

