package com.anubis.module_iva.Http

import com.anubis.kt_extends.*
import com.anubis.module_iva.APP
import com.anubis.module_iva.R
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import java.io.File

/**
 * Author  ： AnubisASN   on 18-10-15 下午4:12.
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
object Http {
    fun AppUpdate() {
        OkGo.post<File>("http://119.23.77.41:8081/apps/" + "iva.apk")
                .tag(this)
                .execute(object : FileCallback(APP.mAPP!!.externalCacheDir.path + "/APPUP_Download", "ewmkm.apk") {
                    override fun onSuccess(response: Response<File>?) {
                        APP.mAPP!!.mTTS!!.speak("下载完成,开始更新")
                        val result = eShell.eExecShellSilent("pm install -r ${APP.mAPP.externalCacheDir.path}/APPUP_Download/ewmkm.apk")
                    }
                    override fun downloadProgress(progress: Progress?) {
                        super.downloadProgress(progress)
                        if ((progress!!.fraction * 100).toInt() % 10 == 0) {
                            APP.mAPP!!.mTTS!!.speak("更新进度,百分之${(progress.fraction * 100).toInt()}")
                        }
                    }
                    override fun onError(response: Response<File>?) {
                        super.onError(response)
                        APP.mAPP!!.mTTS!!.speak("网络异常")
                    }
                })
    }
}

