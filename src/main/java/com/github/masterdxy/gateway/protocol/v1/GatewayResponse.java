package com.github.masterdxy.gateway.protocol.v1;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import static com.github.masterdxy.gateway.protocol.Protocol.RESPONSE_ERROR_CODE;
import static com.github.masterdxy.gateway.protocol.Protocol.RESPONSE_ERROR_MSG;
import static com.github.masterdxy.gateway.protocol.Protocol.RESPONSE_SUCCESS_CODE;
import static com.github.masterdxy.gateway.protocol.Protocol.RESPONSE_SUCCESS_MSG;

public class GatewayResponse {

    private String msg;
    private int code;
    private Object data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public GatewayResponse(int code, String msg, Object data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public static String asJson(int code, String msg, Object data) {
        GatewayResponse response = new GatewayResponse(code, msg, data);
        return JSON.toJSONString(response);
    }

    public static String asErrorJson(String msg) {
        return asJson(RESPONSE_ERROR_CODE, StringUtils.isEmpty(msg) ? RESPONSE_ERROR_MSG : msg, null);
    }

    public static String asSuccessJson(Object data) {
        return asJson(RESPONSE_SUCCESS_CODE, RESPONSE_SUCCESS_MSG, data);
    }
}
