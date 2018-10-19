package com.anubis.module_iva.Service

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import com.anubis.kt_extends.eApp
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eNetWork
import com.anubis.module_iva.APP.Companion.mAPP
import com.anubis.module_iva.APP.Companion.mState

class IVAService : Service() {
    private var mRunnable: Runnable? = null


    override fun onCreate() {
        super.onCreate()
        Daemon()
        Handler().postDelayed({
            mState["语音"] = true
            mState["声控"] = mAPP.mASRW != null
            for (state in mState) {
                eLog(state.key + "--" + state.value)
                if (state.value) mAPP.mTTS!!.speak("${state.key}就绪") else mAPP.mTTS!!.speak("${state.key}未完成")
            }
           if ( eNetWork.eIsNetworkAvailable(this@IVAService)) mAPP.mTTS!!.speak("网络就绪") else  mAPP.mTTS!!.speak("网络连接失败")
        }, 3000)
    }

    private fun Daemon() {
        mState["保护程序"] = true
        mRunnable = Runnable {
            try {
                val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val cn = am.getRunningTasks(1)[0].topActivity
                Log.i("TAG", "守护线程run: " + cn.packageName + "---" + packageName)
                if (cn.packageName != packageName) {
                    val LaunchIntent = packageManager.getLaunchIntentForPackage(application.packageName)
                    LaunchIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(LaunchIntent)
                }
            } catch (e: Exception) {
                Log.i("TAG", "run: 守护线程发生错误$e")
            }

            Handler().postDelayed(mRunnable, 5000)
        }
        Handler().postDelayed(mRunnable, 5000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        mAPP.mASRW!!.asrwDestroy()
        mAPP.mTTS!!.ttsDestroy()
    }
}




