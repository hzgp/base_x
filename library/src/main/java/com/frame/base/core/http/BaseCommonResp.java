package com.frame.base.core.http;

/**
 * Created by Administrator on 2017/4/10.
 */

public class BaseCommonResp<T> {

    public static final int SUCCESS = 200;
    public static final int LOGINOUT = -100003;
    public static final int TOKEN_INVALID=1001;

    private int Code;
    private int Status;
    private String Message;
    private String Msg;
    private boolean Success;
    protected T data;

    protected T Response;
    public int getCode() {
        return Code==0?getStatus():Code;
    }

    public String getMsg() {
        return Msg==null?getMessage():Msg;
    }


    public T getData() {
        return data;
    }

    public void setCode(int code) {
        Code = code;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return Status==0?getCode():Status;
    }

    public String getMessage() {
        return Message==null?getMessage():Message;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    public void setMessage(String message) {
        Message = message;
    }

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

    public Boolean isReqSuccess(){
        return getCode()==SUCCESS;
    }
    public Boolean isTokenInvalid(){
        return getCode() == LOGINOUT || getCode()==TOKEN_INVALID;
    }
}
