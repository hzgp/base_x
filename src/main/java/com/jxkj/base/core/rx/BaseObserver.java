package com.jxkj.base.core.rx;

import android.text.TextUtils;
import android.util.Log;

import com.jxkj.base.app.SourceApplication;
import com.jxkj.base.base.view.IView;
import com.jxkj.base.core.constant.BaseConstants;
import com.jxkj.base.core.http.BaseCommonResp;
import com.jxkj.base.core.http.exception.ServerException;
import com.jxkj.base.frame.R;
import com.jxkj.utils.OSUtils;

import io.reactivex.observers.ResourceObserver;
import retrofit2.HttpException;

import static com.jxkj.base.core.http.BaseCommonResp.LOGINOUT;
import static com.jxkj.base.core.http.BaseCommonResp.SUCCESS;


public abstract class BaseObserver<T> extends ResourceObserver<T> {
    private static final String TAG = "BaseObserver";

    private IView mView;
    private String mErrorMsg;
    private boolean isShowStatusView = true;

    protected BaseObserver(IView view) {
        this.mView = view;
    }

    protected BaseObserver(IView view, String errorMsg) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
    }

    protected BaseObserver(IView view, boolean isShowStatusView) {
        this.mView = view;
        this.isShowStatusView = isShowStatusView;
    }

    protected BaseObserver(IView view, String errorMsg, boolean isShowStatusView) {
        this.mView = view;
        this.mErrorMsg = errorMsg;
        this.isShowStatusView = isShowStatusView;
    }

    public abstract void onSuccess(T t);

    public void onFailure(int code, String message) {
        if (mView != null)
            mView.showErrorMsg(message);
    }

    @Override
    protected void onStart() {
    }

    @Override
    public final void onNext(T response) {
        BaseCommonResp<?> baseCommonResp = (BaseCommonResp<?>) response;
        if (baseCommonResp.getStatus() == SUCCESS && baseCommonResp.isSuccess()) {
            onSuccess(response);
        } else if (baseCommonResp.getStatus() == LOGINOUT) {
            if (mView != null) {
                mView.loginOut();
            }
        } else {
            onFailure(baseCommonResp.getStatus(), baseCommonResp.getMessage());
        }

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
            if (errorMessage != null && errorMessage.contains(BaseConstants.TOKEN_INVALID)) {
                mView.loginOut();
                return;
            }
            if (errorMessage != null && errorMessage.contains(BaseConstants.TOKEN_EXPIRED)) {
                mView.showErrorMsg("没有此操作权限");
                return;
            }
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
            Log.e(TAG, e.toString());
        }
    }

}