package com.ashish.tailnodeassignment.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun <T> T.logd(tag: String = "TAG") {
    Log.d(tag, "$tag :: $this")
}

fun Fragment.showToast(msg: String) {
    Toast.makeText(this.requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Activity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}


fun isMyServiceRunning(serviceClass: Class<*>, mActivity: Activity): Boolean {
    val manager: ActivityManager =
        mActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            Log.i("Service status", "Running")
            return true
        }
    }
    Log.i("Service status", "Not running")
    return false
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager: LocationManager? =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun showAlertLocation(context: Context, title: String, message: String, btnText: String) {
    val alertDialog = AlertDialog.Builder(context).create()
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.setButton(btnText) { dialog, which ->
        dialog.dismiss()
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
    alertDialog.show()
}

fun getAddress(activity: Context, latitude: Double, longitude: Double): String {
    return try {
        val geocoder = Geocoder(activity, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        addresses[0].getAddressLine(0).toString()
    } catch (e: Exception) {
        Log.d("TAG", "getAddress: " + e.message)
        ""
    }
}

fun showGpsNotEnabledDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("GPS Required")
        .setMessage("GPS REQUIRED FOR TRACKING")
        .setPositiveButton("Enable") { dialogInterface: DialogInterface, i: Int ->
            // Open app's settings.
            val intent = Intent().apply {
                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            }
            context.startActivity(intent)
        }
        .setCancelable(false)
        .setNegativeButton(android.R.string.cancel) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
            showGpsNotEnabledDialog(context)
        }
        .show()
}


fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())
    return currentDate
}

fun writeToFile(sBody: String) {
    try {
        val root = File(Environment.getExternalStorageDirectory(), "TailNode")
        if (!root.exists()) {
            root.mkdirs()
        }
        val file = File(root, "TailNodeFile.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        file.appendText(sBody + "\n")
        file.absolutePath.logd()
    } catch (e: IOException) {
        e.printStackTrace()
    }

}
