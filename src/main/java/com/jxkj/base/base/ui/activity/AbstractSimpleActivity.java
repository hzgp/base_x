package com.jxkj.base.base.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.jxkj.base.frame.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportActivity;

public abstract class AbstractSimpleActivity extends SupportActivity implements IActivity {
    private Unbinder unBinder;
    protected ImmersionBar immersionBar;
    protected TextView tvTitle;
    protected boolean isTransparent = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        View layBack = findViewById(R.id.lay_back);
        if (layBack != null) {
            layBack.setOnClickListener((view) -> this.finish());
        }
        unBinder = ButterKnife.bind(this);
        initStatusBar();
        onViewCreated();
        initToolbar();
        initView();
        initEventAndData();
    }

    protected void setImmersionBarDark(boolean isDark) {
        immersionBar.navigationBarDarkIcon(isDark).statusBarDarkFont(isDark);
        try {
            Class<?> barClass = immersionBar.getClass();
            Method method = barClass.getDeclaredMethod("updateBarParams");
            method.setAccessible(true);
            method.invoke(immersionBar);
            method = barClass.getDeclaredMethod("setBar");
            method.setAccessible(true);
            method.invoke(immersionBar);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignore) {
        }
    }

    protected void initStatusBar() {
        immersionBar = ImmersionBar.with(this);//.transparentStatusBar();
        int statusBarColor = statusBarColor();
        if (statusBarColor != -1) {
            immersionBar.statusBarColor(statusBarColor);
        }
        immersionBar
                .statusBarDarkFont(isStatusBarDark())
                .navigationBarDarkIcon(isStatusBarDark())
                .statusBarView(findViewById(R.id.status_bar_view))
                .keyboardEnable(isKeyboardEnable());
        immersionBar.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unBinder != null && unBinder != Unbinder.EMPTY) {
            unBinder.unbind();
            unBinder = null;
        }
        if (immersionBar != null) {
            immersionBar.destroy();
            immersionBar = null;
        }
    }
    protected boolean isStatusBarDark() {
        return false;
    }

    protected boolean isKeyboardEnable() {
        return true;
    }


    protected int statusBarColor() {
        return R.color.theme;
    }
    protected abstract void initView();

    /**
     * 在initEventAndData()之前执行
     */
    protected abstract void onViewCreated();

    /**
     * 获取当前Activity的UI布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化ToolBar
     */
    protected void initToolbar() {
        TextView tvTitle = findViewById(R.id.toolbar_title);
        if (tvTitle == null) {
            return;
        }
        int titleRes = title();
        if (titleRes != -1) {
            tvTitle.setText(getString(titleRes));
        }
    }


    protected @StringRes
    int title() {
        return -1;
    }

    /**
     * 初始化数据
     */
    protected abstract void initEventAndData();
//    /**
//     * 注册监听
//     */
//    protected abstract void setListener();
}
