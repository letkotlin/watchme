package com.watchme.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.watchme.R
import com.watchme.base.view.BaseActivity
import com.watchme.constant.Const
import io.reactivex.Flowable
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit


class IntroActivity : BaseActivity() {

    lateinit var mDatabase: DatabaseReference

    override fun inject() {

    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_intro
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance().reference
        checkVer()
        getKeyHash()
        Log.i(Const.TAG, "IntroActivity onCreate()")
        Flowable.timer(1, TimeUnit.SECONDS)
                .subscribe {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
    }

    private fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.i(Const.TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    private fun checkVer() {
        var eventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.i(Const.TAG, "checkVer() p0 = " + p0?.value)
                Log.i(Const.TAG, "checkVer() manager = " + p0?.child("manager").value)
                var playVersion = p0?.child("manager").value.toString().replace(".", "").toInt()
                if (playVersion > getCurrentVersion()) {
                    landingUpdateMarket()
                } else {

                }
            }
        }
        mDatabase.child("versions").child("android").addListenerForSingleValueEvent(eventListener)
    }

    private fun getCurrentVersion(): Int {
        var ver = 0
        return try {
            ver = applicationContext.packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA).versionName.replace(".", "").toInt()
            Log.i(Const.TAG, "getCurrentVersion ver = " + ver)
            ver
        } catch (e: PackageManager.NameNotFoundException) {
            ver
        }

    }

    private fun landingUpdateMarket() {
        try {
            AlertDialog.Builder(this)
                    .setMessage(R.string.alert_new_version)
                    .setCancelable(false)
                    .setPositiveButton(R.string.update) { dialog, _ ->
                        dialog.dismiss()
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
                        finish()
                    }
                    .setNegativeButton(R.string.close) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }.show()

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.alert_empty_market_message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.alert_fail_connect_market, Toast.LENGTH_SHORT).show()
        }
    }

}
