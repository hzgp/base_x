package com.frame.base.core.rx;

import android.text.TextUtils;
import android.util.Log;

import com.frame.base.R;
import com.frame.base.app.SourceApplication;
import com.frame.base.core.constant.BaseConstants;
import com.frame.base.core.http.BaseCommonResp;
import com.frame.base.core.http.exception.ServerException;
import com.frame.base.inter.ui.IView;
import com.jxkj.utils.OSUtils;

import java.io.IOException;

import io.reactivex.observers.ResourceObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;


public abstract class BaseObserver<T extends BaseCommonResp<?>> extends ResourceObserver<T> {

    private String mErrorMsg;
    private boolean isShowStatusView;

    private IView mView;

    protected BaseObserver(IView view, String errorMsg) {
        this(view, errorMsg, false);
    }

    protected BaseObserver(IView view, boolean isShowStatusView) {
        this(view, "", isShowStatusView);
    }

    protected BaseObserver(IView view, String errorMsg, boolean isShowStatusView) {
        this.mErrorMsg = errorMsg;
        this.mView = view;
        this.isShowStatusView = isShowStatusView;
        //  Activity topTask = AppManager.getAppManager().getTaskTop();
        // if (topTask instanceof AbstractSimpleActivity) mView= (AbstractSimpleActivity) topTask;
    }

    public abstract void onSuccess(T t);

    public void onFailure(int code, String message) {
        if (mView != null)
            mView.showErrorMsg(message);
    }

    @Override
    public final void onNext(T response) {
        if (response.isReqSuccess()) {
            onSuccess(response);
        } else if (response.isTokenInvalid()) {
            if (mView != null) {
                mView.loginOut();
            }
        } else {
            onFailure(response.getCode(), response.getMsg());
        }
        mView = null;
    }

    @Override
    public void onComplete() {
        if (mView == null) {
            return;
        }
        if (!OSUtils.isNetworkConnected(SourceApplication.getAppContext())) {
            mView.showErrorMsg(SourceApplication.getAppContext().getString(R.string.http_error));
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mView == null) {
            return;
        }
        if (e instanceof HttpException) {
            String errorMessage = e.getMessage();
            showHttpError((HttpException) e);
            if (errorMessage != null && (errorMessage.contains(BaseConstants.TOKEN_INVALID) || errorMessage.contains(BaseConstants.TOKEN_EXPIRED))) {
                mView.loginOut();
                return;
            }
//            if (errorMessage != null && )) {
//                //mView.showErrorMsg("没有此操作权限,请重新登录");
//                mView.loginOut();
//                return;
//            }
            mView.showErrorMsg(SourceApplication.getAppContext().getString(R.string.http_error));
            if (isShowStatusView) {
                mView.showNoNetwork();
            }
        } else if (e instanceof ServerException) {
            mView.showErrorMsg(e.toString());
            if (isShowStatusView) {
                mView.showError();
            }
        } else {
            if (!TextUtils.isEmpty(mErrorMsg)) {
                mView.showErrorMsg(mErrorMsg);
            }
            if (isShowStatusView) {
                mView.showError();
            }
        }
        mView = null;
    }

    private void showHttpError(HttpException e) {
        try {
            ResponseBody errorBody = e.response().errorBody();
            if (errorBody != null) Log.e("HttpError:", errorBody.string());
        } catch (IOException | NullPointerException ignore) {
        }
    }
}