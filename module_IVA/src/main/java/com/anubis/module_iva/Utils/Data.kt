package com.anubis.module_iva.Utils

import java.util.*

/**
 * 作者 ： AnubisASN   on 2017/8/21 15:04.
 * 邮箱 ： anubisasn@gmail.com ( anubisasn@qq.com )
 * 主页 ： www.anubisasn.me
 *  Q Q:     441666482
 */
enum class Type{ ME, IVA }


data class dataUnit(val AK: String = "ycQT0HtuCYYdu7zKBHl4PLGX",
                    val SK: String = "5XEfRamiACaH0QynaBMg6RzvkNAdnCKh",
                    val sceneId:String="10007",
                    val talkUrl: String = "https://aip.baidubce.com/rpc/2.0/solution/v1/unit_utterance")

data class dataUser(val userName: String,
                    val userPasswrod: String)

data class dataASRAnimation(
        val ASR_START: Int = 1,
        val ASR_UNDER_WAY: Int = 2,
        val ASR_END: Int = 0,
        val ASR_Result: Int = 3,
        val ASR_ERROR: Int = 5)

data class dataChatMessage(
        val msg:String="诺诺能为你做些什么呢？",
        val type:Type=Type.IVA,
        val date: Date=Date()
)
data class dataNavigation(
        var startSite: String? =null,
        var endSite:String?=null,
        var sortby:String?=null
)
