package com.github.masterdxy.gateway.protocol;

public class Protocol {

    public static final String VERSION_HEADER = "gateway-protocol-version";
    public static final String TRACE_HEADER = "gateway-trace-id";

    public static final int RESPONSE_SUCCESS_CODE = 1000;
    public static final int RESPONSE_ERROR_CODE = 2000;

    public static final String RESPONSE_SUCCESS_MSG = "success";
    public static final String RESPONSE_ERROR_MSG = "error";
}
