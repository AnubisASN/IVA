package com.anubis.module_iva.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import com.anubis.kt_extends.eApp
import com.anubis.kt_extends.eGetDefaultSharedPreferences
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.kt_extends.eString.eGetNumberPeriod
import com.anubis.module_ewifi.eWiFi
import com.anubis.module_iva.*
import com.anubis.module_iva.APP.Companion.mActivityList
import com.anubis.module_iva.APP.Companion.mState
import com.anubis.module_iva.R
import com.anubis.module_iva.Utils.*
import com.anubis.module_iva.Service.IVAService
import kotlinx.android.synthetic.main.ui_fragment_chat.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.onClick
import java.util.*
import kotlin.collections.HashMap


class IVA : AppCompatActivity(), ThemeInterace, NavigationView.OnNavigationItemSelectedListener {
    //Custom
    private var navigationView: NavigationView? = null
    private var navHeaderLayout: View? = null
    private var contentLayout: View? = null
    private var chatASR: ImageView? = null
    private var asrResult: TextView? = null
    private var asrState: TextView? = null
    private var inputASR: EditText? = null
    //Fragment
    private var mSectionsPagerAdapter: IVA.SectionsPagerAdapter? = null
    private var mViewPager: ViewPager? = null
    //Chat
    private var mListView: ListView? = null
    private var mAdapter: MyBaseAdapter? = null
    private var mDatas: List<dataChatMessage>? = null
    private var mTTS = APP.mAPP.mTTS!!
    override fun onCreate(savedInstanceState: Bundle?) {
        mActivityList.add(this)
        ivaInstance = this
        try {
            GaussianBlur()
        } catch (e: Exception) {
            setTheme(R.style.AppTheme_NoActionBar_Translucent)
        }
        setContentView(R.layout.iva_main)
        super.onCreate(savedInstanceState)
        CustomObject()
        //状态栏沉浸
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        try {
            themeUI()
        } catch (e: Exception) {
            navHeaderLayout?.setBackgroundColor(Color.TRANSPARENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contentLayout?.background = getDrawable(R.mipmap.home)

                contentLayout?.alpha = 0.95f
                try {
                    window.statusBarColor = Color.TRANSPARENT
                } catch (e: NoSuchMethodError) {
                    Log.e("TAG", "窗口颜色设置错误")
                }
            }
        }
        //Fragment---
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter
        //Custom---
        headerUserInfo()

        PermissionManager()
        InitChatMessage()
        if (!eApp.eIsServiceRunning(this, IVAService::class.java.name)) {
            startService(Intent(this, IVAService::class.java))
        }
        postDelayed()
    }

    private fun postDelayed() {
        Handler().postDelayed({
            val state = if (eWiFi.eCreateWifiHotspot(this, "Anubis AD", "anubisasn..").indexOf("失败") != -1) "失败" else "成功"
            mTTS.speak("热点自动开启$state")
        }, 3000)
    }

    /**
     * 单例化
     */
    companion object {
        private var ivaInstance: IVA? = null
        fun getInstance() = ivaInstance!!
    }

    /**
     * 界面布局事件序列---------------------------------------------------------
     */
    private fun CustomObject() {
        navigationView = findViewById(R.id.nav_view) as NavigationView?
        navHeaderLayout = navigationView?.inflateHeaderView(R.layout.iva_nav_header)
        navigationView?.setNavigationItemSelectedListener(this)
        contentLayout = findViewById(R.id.content_Layout)
        mListView = findViewById(R.id.content_lvContent) as ListView?
    }

    //Navigation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.drawer_personage -> eShowTip("个人")
            R.id.drawer_capacity -> eShowTip("能力")
            R.id.drawer_setting -> startActivity(Intent(this@IVA, SettingUI::class.java))
            R.id.drawer_help -> eShowTip("帮助")
            R.id.drawer_share -> eShowTip("分享")
            R.id.drawer_feedback -> eShowTip("反馈")
            R.id.drawer_about -> eShowTip("关于")
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    //    NavigationHeader
    private fun headerUserInfo() {
        val userIco = navHeaderLayout?.findViewById<ImageView>(R.id.header_ivUserIco)
        val userName = navHeaderLayout?.findViewById<TextView>(R.id.header_tvUserName)
        val userMessger = navHeaderLayout?.findViewById<TextView>(R.id.header_tvMessage)
        userIco?.onClick { eShowTip("用户图标") }
        userName?.onClick { eShowTip("用户名") }
        userMessger?.onClick { eShowTip("用户消息") }
    }

    /**
     * Fragment+Pager
     */
    //Pager类
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        //定义页面数
        override fun getCount(): Int {
            return 2
        }
    }

    //Fragment类
    class PlaceholderFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val IVA = IVA.getInstance()
            var rootView: View? = null

            when (arguments!!.getInt(ARG_SECTION_NUMBER)) {
                1 -> {
                    rootView = inflater!!.inflate(R.layout.ui_fragment_chat, container, false)
                    IVA.chatASR = rootView.findViewById(R.id.fragment_btChatASR) as ImageView
                    IVA.chatASR!!.onClick {
                        eLog("ggg" + mState.size)
                    }
                    IVA.asrResult = rootView.findViewById(R.id.fragment_tvResult) as TextView
                    IVA.asrState = rootView.findViewById(R.id.fragment_tvStart) as TextView
                }

//                2 -> {
//                    rootView = inflater!!.inflate(R.layout.ui_fragment_input, container, false)
//                    IVA.inputASR = rootView.findViewById(R.id.fragment_etInput) as EditText?
//                    val button=rootView!!.findViewById(R.id.fragment_btSendASR) as Button
//                           button .onClick {
//                        val inputASR = IVA.inputASR!!.text.toString()
//                        val intent =Intent(com.anubis.module_iva.UI.IVA.getInstance(), IVAService::class.java)
//                        val bundle = Bundle()
//                        bundle.putString("TYPE", "TTS")
//                        bundle.putString("STR",inputASR)
//                        intent.putExtras(bundle)
//                        IVA.startService(intent)
//                    }
//                }
            }
            return rootView
        }

        //页面值传送
        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * ListView消息管理序列-----------------------------------------------
     */
    //列表初始化
    private fun InitChatMessage() {
        mDatas = ArrayList()
//        (mDatas as ArrayList<dataChatMessage>).add(dataChatMessage())
        mAdapter = MyBaseAdapter(this, mDatas)
        mListView!!.adapter = mAdapter
        mListView!!.setDivider(null)
    }

    //添加消息列表
    fun IVAChatMessage(MSG: String, TYPE: Type) {
        (mDatas as ArrayList<dataChatMessage>).add(dataChatMessage(MSG, TYPE, Date()))
        mAdapter!!.notifyDataSetChanged()
        mListView!!.setSelection(mDatas!!.size - 1)
    }

    //权限管理
    fun PermissionManager() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    /**
     * UI界面更新序列-------------------------------------------------------------------
     */
    //重启
    fun restartIVA() {
        ivaInstance!!.recreate()
        this.recreate()
    }

    //高斯模糊设置
    fun GaussianBlur() {
        if (eGetDefaultSharedPreferences("set_gaussian_blur") as Boolean) {
            setTheme(R.style.AppTheme_NoActionBar_Translucent)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }

    }

    //主题设置
    override fun themeUI() {
        //主题颜色--
        navHeaderLayout?.setBackgroundColor(Color.parseColor(eGetNumberPeriod(eGetDefaultSharedPreferences("set_theme_color").toString(), 0, ",")))
        //状态栏---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (eGetDefaultSharedPreferences("set_statusbar_transparent") as Boolean) {
                window.statusBarColor = Color.TRANSPARENT
            } else {
                window.statusBarColor = Color.parseColor(eGetNumberPeriod(eGetDefaultSharedPreferences("set_theme_color").toString(), 0, ","))
            }
        }
        //高斯模糊--
        if (eGetDefaultSharedPreferences("set_gaussian_blur") as Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.TRANSPARENT
            }
            navHeaderLayout?.setBackgroundColor(Color.TRANSPARENT)
        }
//        contentLayout?.background = BitmapDrawable(eGetGaussianBlur(R.mipmap.home,
//                eGetDefaultSharedPreferences("set_gaussian_blur_radius").toInt(),
//                eGetDefaultSharedPreferences("set_gaussian_blur_scale").toInt()))
        contentLayout?.alpha = (100.0f - eGetDefaultSharedPreferences("set_gaussian_blur_transparency") as Float) / 100
    }

    //ASR动态
    fun ASRAnimation(State: Int, str: String = "") {
        val Data = dataASRAnimation()
        when (State) {
            Data.ASR_START -> {
                fragment_tvResult?.text = ""
                chatASR?.setImageResource(R.mipmap.microphone_1)
            }
            Data.ASR_UNDER_WAY -> asrState?.text = str
            Data.ASR_END -> {
                chatASR?.imageResource = R.mipmap.microphone_0
                asrState?.text = str
            }
            Data.ASR_Result -> asrResult!!.text = str
            Data.ASR_ERROR -> {
                chatASR?.imageResource = R.mipmap.microphone_0
                asrState?.text = str
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        TTS!!.ttsDestroy()
    }
}





