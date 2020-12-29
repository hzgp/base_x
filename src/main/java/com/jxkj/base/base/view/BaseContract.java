package com.jxkj.base.base.view;


import com.jxkj.base.base.presenter.IPresenter;

/**
 * Created by DY on 2019/9/29.
 */

public interface BaseContract {

    interface View extends IView{

    }

    interface Presenter extends IPresenter<View> {
        void GetSysConfig();
    }
}
