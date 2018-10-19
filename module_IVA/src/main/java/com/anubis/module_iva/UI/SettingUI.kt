package com.anubis.module_iva.UI

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.MenuItem
import com.anubis.module_iva.Utils.ThemeInterace
import android.graphics.drawable.ColorDrawable
import android.preference.*
import com.anubis.kt_extends.eGetDefaultSharedPreferences
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.kt_extends.eString.eGetNumberPeriod
import com.anubis.module_iva.*
import com.anubis.module_iva.APP.Companion.mActivityList

/**
 * 作者 ： AnubisASN   on 2017/8/5 19:43.
 * 邮箱 ： anubisasn@gmail.com ( anubisasn@qq.com )
 * 主页 ： www.anubisasn.me
 *  Q Q:     441666482
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SettingUI : PreferenceActivity(), ThemeInterace, SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         mActivityList!!.add(this)
        PreferenceManager.setDefaultValues(this, R.xml.ui_setting, true)
        addPreferencesFromResource(R.xml.ui_setting)
        //启用导航栏图标
        actionBar.setDisplayHomeAsUpEnabled(true)
        //添加状态侦听
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        try {
            themeUI()
        } catch (e: Exception) {
            window.setStatusBarColor(Color.parseColor("#ff333333"))
        }
        sSummaryModify(findPreference("root_screen") as PreferenceScreen)
    }

    //状态侦听
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        eShowTip("设置成功")
        if (key.equals("set_theme_color")) {
            this.recreate()
        }
        IVA.getInstance().restartIVA()
    }

    //Summary修改
    fun sSummaryModify(group: PreferenceGroup) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in 0..group.preferenceCount - 1) {
            val p = group.getPreference(i)
            if (p is PreferenceGroup) {
                sSummaryModify(p)
            } else {
                val value = sp.all[p.key]
                when (p) {
                    is ListPreference -> p.summary = "\n${eGetNumberPeriod(value.toString(),",","MAX")}"
                    is EditTextPreference -> p.summary = "设置值：$value"
                }
                p.onPreferenceChangeListener = this as Preference.OnPreferenceChangeListener?
            }
        }
    }

    //Summary动态刷新
    override fun onPreferenceChange(p: Preference, newValue: Any): Boolean {
        eLog("p:$p newvalue:$newValue")
        when (p) {
            is ListPreference -> p.summary = "\n${eGetNumberPeriod(newValue.toString(),",","MAX")}"
            is EditTextPreference -> p.summary = "设置值：$newValue"
        }

        return true
    }


    //导航栏返回
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun themeUI() {
        window.statusBarColor = Color.parseColor(eGetNumberPeriod(eGetDefaultSharedPreferences("set_theme_color").toString(),0,","))
        val drawable = ColorDrawable(Color.parseColor(eGetNumberPeriod(eGetDefaultSharedPreferences("set_theme_color").toString(),0,",")))
        actionBar.setBackgroundDrawable(drawable)
    }

}
