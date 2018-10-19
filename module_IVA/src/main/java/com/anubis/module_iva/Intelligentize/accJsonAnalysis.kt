package com.anubis.module_iva.Intelligentize

import com.anubis.kt_extends.eJson.eGetJsonArray
import com.anubis.kt_extends.eJson.eGetJsonObject
import com.anubis.kt_extends.eLog

/**
 * 作者 ： AnubisASN   on 2017/9/6 17:19.
 * 邮箱 ： anubisasn@gmail.com ( anubisasn@qq.com )
 * 主页 ： www.anubisasn.me
 *  Q Q:     441666482
 */
object accJsonAnalysis {
    var startSite: String? = null
    var endSite: String? = null
    var sortby: String? = null

    fun MainEXE(JSON: String): String {
        val JSONresult = eGetJsonObject(JSON, "result")
        val JSONaction_list = eGetJsonArray(JSONresult, "action_list", 0)
        val JSONmain_exe = eGetJsonObject(JSONaction_list, "main_exe")
        return JSONmain_exe
    }

    fun BotMergedSlots(JSON: String): ArrayList<String>? {

        var array: ArrayList<String>? = null
        val JSONresult = eGetJsonObject(JSON, "result")
        val JSONqu_res = eGetJsonObject(JSONresult, "qu_res")
        val JSONintent_candidates = eGetJsonArray(JSONqu_res, "intent_candidates", 0)
        val JSONslots = eGetJsonArray(JSONintent_candidates, "slots")
        for (i in 0..JSONslots.length()) {
            val obj = JSONslots.getJSONObject(i)
            eLog("obj$obj ")
            when (eGetJsonObject(obj.toString(), "type")) {
                "user_start_site" -> startSite = eGetJsonObject(obj.toString(), "original_word")
                "user_end_site" -> endSite = eGetJsonObject(obj.toString(), "original_word")
                "user_sortby" -> sortby = eGetJsonObject(obj.toString(), "original_word")
            }
            array = arrayListOf<String>(startSite.toString(), endSite.toString(), sortby.toString())
        }
        eLog("sssssggg:$array")
        return array
    }

    fun dofor() {

    }
}
