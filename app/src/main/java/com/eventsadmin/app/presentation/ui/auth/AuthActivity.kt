package com.eventsadmin.app.presentation.ui.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.eventsadmin.app.R
import com.eventsadmin.app.common.constansts.AppConstants
import com.eventsadmin.app.databinding.ActivityAuthBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.otpless.main.OtplessManager
import com.otpless.main.OtplessView

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var sendOtpButton: MaterialButton
    private lateinit var phoneNumberEditText: EditText
    private lateinit var otplessView: OtplessView
    private lateinit var otpLayout: ConstraintLayout
    private lateinit var otpEditText: EditText
    private lateinit var goBackButton: MaterialTextView
    private lateinit var progressMessageTextView: TextView
    private val progressDialog: Dialog by lazy {
        Dialog(this).apply {
            setContentView(R.layout.progress_bar)
            setCancelable(false)
            progressMessageTextView = findViewById(R.id.progress_text)
        }
    }
    private val authViewModel: AuthViewModel by viewModels()
    private var phoneNumber: String = ""
    private val TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otplessView = OtplessManager.getInstance().getOtplessView(this)
        otplessView.initHeadless(AppConstants.OTP_LESS_APP_ID)
        otplessView.setHeadlessCallback {
            authViewModel.onOTPLessCallback(it)
        }

        phoneNumberEditText = binding.phoneNumberEditText
        sendOtpButton = binding.sendOtpButton
        otpLayout = binding.otpLayout
        otpEditText = binding.otpEditText
        goBackButton = binding.goBackButton

        authViewModel.authEvents.observe(this) { event ->
            when (event) {
                is AuthEvents.ShowOtpLayout -> showOtpLayout()
                is AuthEvents.ShowProgressBar -> showProgressBar(event.message)
                is AuthEvents.OtpAutoRead -> setOtp(event.otp)
                is AuthEvents.HideProgressBar -> hideProgressBar()
                is AuthEvents.ShowToast -> showToast(event.message)
            }
        }

        sendOtpButton.setOnClickListener {
            phoneNumber = phoneNumberEditText.text.toString().trim()

            if (phoneNumber.isEmpty() || !isValidPhoneNumber(phoneNumber)) {
                showToast("Please enter a valid phone number")
                return@setOnClickListener
            }
            showProgressBar("Sending OTP")
            authViewModel.onAction(AuthActions.SendOTP(otplessView, phoneNumber))
        }

        goBackButton.setOnClickListener {
            hideOtpLayout()
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length == 10
    }

    private fun hideProgressBar() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun showProgressBar(message: String) {
        if (!progressDialog.isShowing) {
            progressMessageTextView.text = message
            progressDialog.show()
        }
    }

    private fun setOtp(otp: String) {
        otpEditText.setText(otp)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::otplessView.isInitialized) {
            otplessView.onNewIntent(intent)
        }
    }

    private fun hideOtpLayout() {
        val height = otpLayout.height.toFloat()
        ObjectAnimator.ofFloat(otpLayout, "translationY", height).apply {
            duration = 300
            start()
        }.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                otpLayout.isVisible = false
            }
        })
    }

    private fun showOtpLayout() {
        otpLayout.isVisible = true
        otpLayout.post {
            val height = otpLayout.height.toFloat()
            otpLayout.translationY = height
            ObjectAnimator.ofFloat(otpLayout, "translationY", 0f).apply {
                duration = 300
                start()
            }
        }
    }
}
