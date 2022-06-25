package com.ashish.tailnodeassignment

import android.Manifest.permission.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.ashish.tailnodeassignment.databinding.ActivityMainBinding
import com.ashish.tailnodeassignment.utils.GPSUtil
import com.ashish.tailnodeassignment.utils.PrefHelper
import com.ashish.tailnodeassignment.utils.isMyServiceRunning
import com.ashish.tailnodeassignment.utils.showToast
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted


const val LOCATION = 1000

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefHelper: PrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefHelper = PrefHelper(this)

        prefHelper.apply {
            binding.name.text = "$name\n$mobile_no"
        }

        LocationService.location.observe(this) {
            binding.tv.text = "Latitude : ${it.latitude} Longitude : ${it.longitude}"
        }

        binding.startLocationUpdateBtn.setOnClickListener {
            GPSUtil(this).turnOnGps()
            checkPermissions()
            if (SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                requestManageStoragePermission()
            }
        }

        binding.stopLocationUpdateBtn.setOnClickListener {
            stopService(Intent(this, LocationService::class.java))
            showToast("Location updates stopped successfully.")
        }

    }

    private fun requestManageStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(LOCATION)
    private fun checkPermissions() {
        if (EasyPermissions.hasPermissions(
                this,
                ACCESS_FINE_LOCATION,
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE,
            )
        ) {
            // Already have permission, do the thing
            initStartService()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                host = this,
                rationale = "Please provide permissions to contiue.",
                requestCode = LOCATION,
                perms = arrayOf(
                    ACCESS_FINE_LOCATION,
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    private fun initStartService() {
        if (!isMyServiceRunning(LocationService::class.java, this)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, LocationService::class.java))
            } else {
                startService(Intent(this, LocationService::class.java))
            }
            showToast("Location updates started successfully.")

        } else {
            showToast("Location updates are already going on.")
        }
    }

}