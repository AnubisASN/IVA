package com.anubis.module_iva.Intelligentize

import android.content.Context
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eString
import com.anubis.module_ewifi.eWiFi
import com.anubis.module_ewifi.eWiFi.eCloseWifiHotspot
import com.anubis.module_iva.APP
import com.anubis.module_iva.Http.Http
import com.anubis.module_iva.R.id.ss

/**
 * Author  ： AnubisASN   on 18-10-15 下午2:19.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
class ASRLocalAnalysis(val context: Context, val str: String) {
    private var mTTS = APP.mAPP!!.mTTS!!
    private var index = 0
    private val hashMap = HashMap<String, String>() //name  operation result

    //    语音测试打开wifi
    init {
        A1(str)
    }

    //一级智能化处理
    private fun A1(str: String) {
        val str = str.toUpperCase()
        eLog("ASRLocalAnalysis$str")
        when {
            //WIFI控制
            str.indexOf("热点").apply { if (this != -1) hashMap["name"] = "热点" } != -1 -> if (str.indexOf("打开").apply { hashMap["operation"] = if (this != -1) "打开" else "关闭" } != -1)
                hashMap["result"] = if (eWiFi.eCreateWifiHotspot(context, "Anubis AD", "anubisasn..").indexOf("失败") != -1) "失败" else "成功"
            else
                hashMap["result"] = if (eWiFi.eCloseWifiHotspot(context)) "成功" else "失败"
           //APP更新控制
            str.indexOf("程序更新").apply { if (this != -1) hashMap["name"] = "IVA" } != -1 -> if (str.indexOf("更新").apply { hashMap["operation"] = if (this != -1) "更新" else "关闭" } != -1)
             Http.AppUpdate()
            else
                hashMap["result"] = "暂无操作"
        }
        mTTS.speak(hashMap["name"] + hashMap["operation"] + hashMap["result"])
    }

}
