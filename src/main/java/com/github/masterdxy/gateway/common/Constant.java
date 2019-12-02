package com.github.masterdxy.gateway.common;

/**
 * @author tomoyo
 */
public final class Constant {

    /**
     * Vertx
     */
    public static final String ROUTE_BASE_PATH = "/api/*";
    public static final int ROUTE_BASE_PATH_LENGTH = 4;
    public static final String APPLICATION_JSON = "application/json";
    public static final String LOGGER_FACTORY = "io.vertx.core.logging.SLF4JLogDelegateFactory";
    public static final String VERTICLE_PREFIX = "gateway";
    public static final String WORKER_POOL_NAME = "gateway-work-pool";
    /**
     * Scheduled delay of EndpointConfig and DubboService loader.
     * 10S
     */
    public static final int DELAY_LOAD_EPC = 1000 * 10;
    /**
     * Scheduled delay of MasterWriteLock.
     * 3S
     */
    public static final int DELAY_TYR_LOCK = 1000 * 3;
    /**
     * Try MasterWriteLock timeout.
     */
    public static final int LOCK_TIMEOUT_MS = 10;
    /**
     * MasterWriteLock Lock lease time
     * MasterWriteLock Hold lock time
     */
    public static final int LOCK_LEASE_TIME_MS = 1000 * 30;


    /**
     * Mango
     */
    public static final String DEFAULT_DATASOURCE_NAME = "def_ds";

    /**
     * Dubbo
     */
    public static final String DUBBO_CONSUMER_APPLICATION_NAME = "gateway-consumer";
    public static final int DUBBO_HEART_BEAT_INTERVAL = 1000;
    public static final int DUBBO_RETRY = 0;
    public static final String DUBBO_DEFAULT_VERSION = "1.0.0";

    /**
     * Protocol
     */
    public static final String PROTOCOL_DUBBO = "dubbo";
    public static final String PROTOCOL_HTTP = "http";

    /**
     * Context Key
     */
    public static final String GATEWAY_REQUEST_KEY = "gr";
    public static final String ENDPOINT_CONFIG = "epc";
    public static final String MOCK_DATA_KEY = "mock";

    /**
     * Response Headers
     */
    public static final String RESPONSE_HEADER_KEY_TIME = "gateway-response-time";

    /**
     * Response Status Code
     */
    public static final int RESPONSE_STATUS_OK = 200;
    public static final int RESPONSE_STATUS_ERROR = 500;

    /**
     * Hazelcast Key
     */
    public static final String HAZELCAST_EPC_MAP_KEY = "hazelcast_epc_map";
    public static final String HAZELCAST_LOCK_KEY = "config_keeper_lock";
    public static final String HAZELCAST_MOCK_DATA_MAP_KEY = "hazelcast_mock_data_map";

}
