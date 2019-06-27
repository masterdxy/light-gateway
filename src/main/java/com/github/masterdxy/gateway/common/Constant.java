package com.github.masterdxy.gateway.common;

public final class Constant {
    public static final String LOGGER_FACTORY = "io.vertx.core.logging.SLF4JLogDelegateFactory";
    public static final String VERTICLE_PREFIX = "gateway";
    public static final String WORKER_POOL_NAME = "gateway-work-pool";

    public static final String DUBBO_CONSUMER_APPLICATION_NAME = "gateway-consumer";


    //Context Key
    public static final String GATEWAY_REQUEST_KEY = "gr";
    public static final String ENDPOINT_CONFIG = "epc";



    public static final String PLUGIN_RESULT_KEY = "plugin_result";
    public static final String PLUGIN_ERROR_MESSAGE_KEY = "plugin_error_msg";
    public static final String PLUGIN_ERROR_THROW_OBJ_KEY = "plugin_error_throwable";




    //Headers
    public static final String HEADER_RESPONSE_TIME = "gateway-response-time";


}
