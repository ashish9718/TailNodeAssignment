package com.ashish.tailnodeassignment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ashish.tailnodeassignment.databinding.ActivitySignUpBinding
import com.ashish.tailnodeassignment.utils.PrefHelper
import com.ashish.tailnodeassignment.utils.showToast

class SignUpActivity : AppCompatActivity() {
    private lateinit var prefHelper: PrefHelper
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefHelper = PrefHelper(this)

        if (prefHelper.isFirstTimeLaunch) {
            prefHelper.isFirstTimeLaunch = false
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.button.setOnClickListener {
            when {
                binding.nameEt.text.isEmpty() -> showToast("Please enter name.")
                binding.noEt.text.isEmpty() -> showToast("Please enter mobile number.")
                else -> {
                    prefHelper.name = binding.nameEt.text.toString().trim()
                    prefHelper.mobile_no = binding.noEt.text.toString().trim()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }


    }
}