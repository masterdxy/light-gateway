package com.github.masterdxy.gateway.protocol.v1;

import com.alibaba.fastjson.JSON;

public class ResponseBuilder {

    public static String buildJson(int code, String msg, String data) {
        GatewayResponse response = new GatewayResponse();
        response.setCode(code);
        response.setData(data);
        response.setMsg(msg);
        return JSON.toJSONString(response);
    }

    public static String build(int code, String msg) {
        GatewayResponse response = new GatewayResponse();
        response.setCode(code);
        response.setMsg(msg);
        return JSON.toJSONString(response);
    }

}
