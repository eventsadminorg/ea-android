package com.eventsadmin.app.presentation.ui.auth

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eventsadmin.app.common.constansts.AppConstants
import com.google.android.gms.auth.api.Auth
import com.otpless.dto.HeadlessRequest
import com.otpless.dto.HeadlessResponse
import com.otpless.main.OtplessView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val _authEvents = MutableLiveData<AuthEvents>()
    val authEvents: LiveData<AuthEvents> get() = _authEvents

    fun onAction(action: AuthActions) {
        when (action) {
            is AuthActions.SendOTP -> {
                startPhoneNumberVerification(action.otplessView, action.phoneNumber)
            }
        }
    }

    fun onOTPLessCallback(response: HeadlessResponse) {
        if (response.response != null && response.statusCode == 200) {
            when (response.responseType) {
                AppConstants.OTP_INITIATE -> {
                    _authEvents.value = AuthEvents.HideProgressBar
                    _authEvents.value = AuthEvents.ShowOtpLayout
                }
                AppConstants.OTP_VERIFY -> {
                    _authEvents.value = AuthEvents.HideProgressBar
                    _authEvents.value = AuthEvents.ShowToast("Verified")
                }
                AppConstants.OTP_AUTO_READ -> {
                    val otp = response.response!!.optString("otp")
                    _authEvents.value = AuthEvents.OtpAutoRead(otp)
                    _authEvents.value = AuthEvents.ShowProgressBar("Verifying OTP")
                }
                AppConstants.OTP_ONE_TAP -> {
                    val responseWithToken = response.response?.optString("token")
                    _authEvents.value = AuthEvents.HideProgressBar
                    _authEvents.value = AuthEvents.ShowToast("Verified")
                    _authEvents.value = AuthEvents.ShowToast("Token $responseWithToken")
                }
            }
            val successResponse = response.response
        } else {
            val error = response.response?.optString("errorMessage")
        }
    }

    private fun startPhoneNumberVerification(otplessView: OtplessView, phoneNumber: String) {
        val request = HeadlessRequest()
        request.setPhoneNumber("91", phoneNumber)
        otplessView.startHeadless(request, this::onOTPLessCallback)
    }

}

sealed class AuthActions {
    data class SendOTP(val otplessView: OtplessView, val phoneNumber: String) : AuthActions()
}

sealed class AuthEvents {
    data class ShowToast(val message: String) : AuthEvents()
    data class ShowProgressBar(val message: String) : AuthEvents()
    data object HideProgressBar : AuthEvents()
    data object ShowOtpLayout : AuthEvents()
    data class OtpAutoRead(val otp: String) : AuthEvents()
}