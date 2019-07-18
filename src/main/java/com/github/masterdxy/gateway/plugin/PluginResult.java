package com.github.masterdxy.gateway.plugin;

//plugin chain execute result, returned to plugin handler.
public class PluginResult {

    private Throwable throwable;
    private boolean error;
    private String errorMsg;

    private Object data;


    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static PluginResult success(Object data) {
        PluginResult result = new PluginResult();
        result.setData(data);
        return result;
    }

    public static PluginResult fail(String errorMsg) {
        PluginResult result = new PluginResult();
        result.setError(true);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public static PluginResult fail(Throwable throwable, String errorMsg) {
        PluginResult result = new PluginResult();
        result.setError(true);
        result.setErrorMsg(errorMsg);
        result.setThrowable(throwable);
        return result;
    }
}
