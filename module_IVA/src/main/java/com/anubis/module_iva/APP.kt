package com.anubis.module_iva

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.kt_extends.eApp
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_asrw.eASRW
import com.anubis.module_asrw.recognization.IStatus
import com.anubis.module_asrw.recognization.PidBuilder
import com.anubis.module_iva.Intelligentize.ASRLocalAnalysis
import com.anubis.module_iva.Service.IVAService
import com.anubis.module_tts.Bean.ParamMixMode
import com.anubis.module_tts.eTTS
import com.baidu.speech.asr.SpeechConstant
import com.tencent.bugly.crashreport.CrashReport
import java.util.LinkedHashMap

/**
 * Author  ： AnubisASN   on 2018-07-21 17:03.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *说明：
 */

class APP : Application() {
    var mTTS: eTTS? = null
    var mASRW: eASRW? = null
    val mHandler=object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            handleMsg(msg!!)
        }
    }
    companion object {
      private   var mInit: APP? = null
         val  mAPP :APP get() = mInit!!
        val mActivityList: ArrayList<Activity> =  ArrayList()
        val mState= HashMap<String,Boolean>()
    }

    override fun onCreate() {
        super.onCreate()
        mInit = this
        mTTS = eTTS.ttsInit(this@APP, mHandler, ParamMixMode =ParamMixMode.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI ,AID_AKY_SKY = arrayOf("11010053", "Yv1fNRycpH0SqaIvqVhKkd9k", "rOf0ISjaTHDAzzK3q8VP5lrfCsYEntTK"))
        mASRW=eASRW.start(this,mHandler,arrayOf("11010053", "Yv1fNRycpH0SqaIvqVhKkd9k", "rOf0ISjaTHDAzzK3q8VP5lrfCsYEntTK"))
        CrashReport.initCrashReport(applicationContext, "47d98f44ec", false)
        CrashReport.initCrashReport(applicationContext)
        ARouter.openLog()
        ARouter.openDebug()
        ARouter.init(this)
    }

    //    0 唤醒成功         3    引擎就绪 开始说话            4 监测到说话      9001  监测到结束说话        5  临时识别      6  识别结束        2 识别引擎空闲
//    arg1 类型   arg2 最终状态   what  引擎状态   obj String消息
    private val backTrackInMs = 2000
    private val MSG_TYPE_WUR = 11
    private val MSG_TYPE_ASR = 22
    private val MSG_TYPE_TTS = 33
    private val MSG_STATE_TTS_SPEAK_OVER = 0
    private val MSG_STATE_TTS_SPEAK_START = 1
    private fun handleMsg(msg: Message) {
        if (msg.what == IStatus.STATUS_WAKEUP_SUCCESS) {
            eShowTip("语音唤醒成功")
//                  此处 开始正常识别流程
            val params = LinkedHashMap<String, Any>()
            params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
            params[SpeechConstant.VAD] = SpeechConstant.VAD_DNN
            // 如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
            params[SpeechConstant.PID] = PidBuilder.create().model(PidBuilder.INPUT).toPId()
            if (backTrackInMs > 0) { // 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
                params[SpeechConstant.AUDIO_MILLS] = System.currentTimeMillis() - backTrackInMs
            }
            mASRW?.myRecognizer?.cancel()
            mASRW?.myRecognizer?.start(params)
        }
        when (msg.what) {
            0 -> {
                //唤醒成功
                eLog("唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_NONE -> {
//                识别引擎空闲
                eLog("识别引擎空闲:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_READY -> {
//                引擎就绪 开始说话
                eLog("引擎就绪 开始说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_SPEAKING -> {
//                监测到说话
                eLog("监测到说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_RECOGNITION -> {
//                临时识别
                eLog("临时识别:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_FINISHED -> {//识别结束
                eLog("识别结束:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                if (msg.arg2 == 1) {
                    eShowTip("最终识别：" + msg.obj.toString())
                    ASRLocalAnalysis(this,msg.obj.toString())
                }

            }

        }
    }

    override  fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
