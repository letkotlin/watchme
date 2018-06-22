package com.watchme.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.ApiErrorCode
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.OptionalBoolean
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger
import com.watchme.R
import com.watchme.base.view.BaseActivity

class LoginActivity : BaseActivity() {
    private var callback: SessionCallback? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_login
    }

    override fun inject() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("WATCHME", "LoginActivity onCreate()")
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
            requestMe()
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            if (exception != null) {
                Logger.e(exception)
            }
        }
    }

    protected fun requestMe() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                val message = "failed to get user info. msg=" + errorResult!!
                Logger.d(message)
                val result = errorResult.errorCode
                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {

                } else {

                }
            }

            override fun onSessionClosed(errorResult: ErrorResult) {

            }

            override fun onSuccess(result: MeV2Response) {
                Log.i("WATCHME", "LoginActivity onSuccess MeV2Response = " + result.toString())
                if (result.id != null) {
                    landingHome()
                }
            }
        })
    }

    private fun landingHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

}
