package com.frame.base.inter.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import com.frame.base.R
import com.frame.base.inter.ui.IView
import com.gyf.barlibrary.ImmersionBar
import me.yokeyword.fragmentation.SupportActivity
import java.lang.reflect.InvocationTargetException

abstract class AbstractSimpleActivity : SupportActivity(), IView {
    protected open var immersionBar: ImmersionBar? = null
    protected var tvTitle: TextView? = null
    protected var isTransparent = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preContentView()
        setContentView()
        val layBack = findViewById<View>(R.id.lay_back)
        layBack?.setOnClickListener { view: View? -> finish() }
        initStatusBar()
        onViewCreated()
        initToolbar()
        initView()
        initEventAndData()
    }

    protected open fun setImmersionBarDark(isDark: Boolean) {
        immersionBar!!.navigationBarDarkIcon(isDark).statusBarDarkFont(isDark)
        try {
            val barClass: Class<*> = immersionBar!!.javaClass
            var method = barClass.getDeclaredMethod("updateBarParams")
            method.isAccessible = true
            method.invoke(immersionBar)
            method = barClass.getDeclaredMethod("setBar")
            method.isAccessible = true
            method.invoke(immersionBar)
        } catch (ignore: NoSuchMethodException) {
        } catch (ignore: InvocationTargetException) {
        } catch (ignore: IllegalAccessException) {
        }
    }

    protected open fun preContentView() {}
    abstract fun setContentView()
    protected open fun initStatusBar() {
        immersionBar = ImmersionBar.with(this) //.transparentStatusBar();

        immersionBar?.run {
            val statusBarColor = statusBarColor()
            if (statusBarColor != -1) {
                statusBarColor(statusBarColor)
            }
            statusBarDarkFont(isStatusBarDark())
                .navigationBarDarkIcon(isStatusBarDark())
                .statusBarView(findViewById(R.id.status_bar_view))
                .keyboardEnable(isKeyboardEnable())
            init()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (immersionBar != null) {
            immersionBar!!.destroy()
            immersionBar = null
        }
    }

    protected open fun isStatusBarDark(): Boolean{
        return true
    }
    protected open fun isKeyboardEnable(): Boolean{
        return true
    }

    protected open  fun statusBarColor(): Int {
        return R.color.white
    }

    protected abstract fun initView()

    /**
     * 在initEventAndData()之前执行
     */
    protected abstract fun onViewCreated()
     override fun loginOut() { }
     override fun showError() {
     }
     override fun showNoNetwork() {
     }
    /**
     * 初始化ToolBar
     */
    protected open fun initToolbar() {
        val tvTitle = findViewById<TextView>(R.id.toolbar_title) ?: return
        val titleRes = title()
        var title: String?
        if (titleRes != -1) {
            tvTitle.text = getString(titleRes)
        } else if (titleStr().also { title = it } != null) {
            tvTitle.text = title
        }
    }

    @StringRes
    protected open fun title(): Int {
        return -1
    }

    protected open fun titleStr(): String? {
        return null
    }

    /**
     * 初始化数据
     */
    abstract fun initEventAndData()
}