package com.watchme.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.watchme.R
import com.watchme.base.view.BaseActivity
import io.reactivex.Flowable
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit


class IntroActivity : BaseActivity() {
    override fun inject() {

    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_intro
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getKeyHash()
        Log.i("WATCHME", "IntroActivity onCreate()")
        Flowable.timer(1, TimeUnit.SECONDS)
                .subscribe {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                };
    }

    private fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.i("WATCHME",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }

    }
}
