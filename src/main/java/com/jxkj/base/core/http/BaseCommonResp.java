package com.jxkj.base.core.http;

/**
 * Created by Administrator on 2017/4/10.
 */

public class BaseCommonResp<T> {

    public static final int SUCCESS = 200;
    public static final int LOGINOUT = -100003;

    private int Status;
    private String Message;
    private boolean Success;
    protected T Response;

    public T getResponse() {
        return Response;
    }

    public void setResponse(T response) {
        Response = response;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
