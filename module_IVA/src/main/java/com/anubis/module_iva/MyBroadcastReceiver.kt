package com.anubis.module_iva

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import com.anubis.kt_extends.*
import com.anubis.module_iva.UI.IVA

/**
 * Author  ： AnubisASN   on 2018-07-23 9:12.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 * Router :  /'Module'/'Function'
 * 说明：
 */
class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        eLog("接收到广播")
        var state=eBReceiver.eSetPowerBoot(context,intent,IVA::class.java)
        APP.mAPP!!.mTTS!!.speak(state)
         state=eBReceiver.eSetAPPUpdateBoot(context,intent,IVA::class.java)
        APP.mAPP!!.mTTS!!.speak(state)
    }

    object eBReceiver {
        //开机启动
        private var isSetAutoBoot = true

        fun eSetPowerBoot(context: Context, intent: Intent, cls: Class<*>): String {
            return if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                eLog("开机自启", "SAK")
                if (isSetAutoBoot) {
                    isSetAutoBoot = false
                    val startServiceIntent = Intent(context, cls)
                    startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(startServiceIntent)
                    "IVA自启完成"
                } else ""
            } else ""
        }
        //APP更新启动
        fun eSetAPPUpdateBoot(context: Context, intent: Intent, cls: Class<*>): String {
            //接收更新广播
            if (intent.action == "android.intent.action.PACKAGE_REPLACED") {
                eLog("升级了一个安装包，重新启动此程序", "SAK")
                Toast.makeText(context, "升级了一个安装包，重新启动此程序", Toast.LENGTH_SHORT).show()
                val startServiceIntent = Intent(context, cls)
                startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(startServiceIntent)
                return "IVA已完成更新"
            }
            //接收安装广播
            if (intent.action == "android.intent.action.PACKAGE_ADDED") {
                eLog("升级了一个安装包，重新启动此程序", "SAK")
                Toast.makeText(context, "升级了一个安装包，重新启动此程序", Toast.LENGTH_SHORT).show()
                val packName = intent.resolveActivityInfo(context.packageManager, 0).toString()
                eLog("packName:$packName")
                val startServiceIntent = Intent(context, cls)
                startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(startServiceIntent)
            }
            //接收卸载广播
            if (intent.action == "android.intent.action.PACKAGE_REMOVED") {
//                val packageName = intent.dataString
//                println("卸载了:" + packageName + "包名的程序")
            }
            return ""
        }
    }
}
