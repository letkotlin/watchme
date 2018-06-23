package com.watchme.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.ApiErrorCode
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.watchme.R
import com.watchme.base.view.BaseActivity
import com.watchme.constant.Const
import com.watchme.model.Manager


class LoginActivity : BaseActivity() {
    private var callback: SessionCallback? = null
    var user: MeV2Response? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_login
    }

    override fun inject() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Const.TAG, "LoginActivity onCreate()")
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
                Log.i(Const.TAG, "LoginActivity onSuccess MeV2Response = " + result.toString())
                user = result
                showPermission()
            }
        })
    }

    private fun landingHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showPermission() {
        TedRx2Permission.with(this)
                .setRationaleMessage(R.string.auth_rationale_msg)
                .setPermissions(Manifest.permission.READ_PHONE_STATE)
                .request()
                .subscribe { tedPermissionResult ->
                    if (tedPermissionResult.isGranted) {
                        getManagerDataByLoginTypeAndId(user?.id.toString() + "_" + getPhoneNumber())
                    } else {
                        Toast.makeText(this, R.string.auth_denied_msg, Toast.LENGTH_SHORT).show()
                        showPermission()
                    }
                }
    }

    private fun getPhoneNumber(): String {
        val tMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        Log.i(Const.TAG, "LoginActivity getPhoneNumber() tMgr.line1Number = " + tMgr.line1Number)
        return tMgr.line1Number
    }

    private fun getManagerDataByLoginTypeAndId(loginTypeAndId: String) {

        // 로그인 타입과 아이디는 중첩 검색을 지원하지 않기 때문에 등록 시 생성한 loginTypeAndId의 값을 조회합니다.
        FirebaseDatabase.getInstance().getReference("manager")
                .orderByChild("loginTypeAndId")
                .equalTo(loginTypeAndId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // TODO 해당 아이디에 해당하는 값이 여러명 검색된다면??
                        for (snapshot in dataSnapshot.children) {
                            val manager = snapshot.getValue(Manager::class.java)
                            Log.i(Const.TAG, "LoginActivity getManagerDataByLoginTypeAndId() manager = " + manager!!.toString())
                            var ma: Manager = Manager("KAKAO", user!!.nickname, getPhoneNumber(), "", user!!.id.toString(), "KAKAO_" + user?.id)
                            insertManager(ma)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("MainActivity", databaseError.toString())
                    }
                })
    }

    fun insertManager(manager: Manager) {
        FirebaseDatabase.getInstance().getReference("manager")
                .orderByKey()
                .limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        while (dataSnapshot.children.iterator().hasNext()) {
                            FirebaseDatabase.getInstance().getReference("manager").child((dataSnapshot.key!!.toInt() + 1).toString()).setValue(manager)
                        }
//                        for (DataSnapshot snapshot : p0.getChildren()) {

//                        }
//                        FirebaseDatabase.getInstance().getReference("manager").child(String.valueOf(Integer.valueOf(snapshot.getKey()) + 1)).setValue(manager);
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }


                })
    }
}
