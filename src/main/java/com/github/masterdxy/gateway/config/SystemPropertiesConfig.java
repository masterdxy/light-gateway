package com.github.masterdxy.gateway.config;

import com.github.masterdxy.gateway.common.Constant;

public class SystemPropertiesConfig {

    public static void prepareProperties() {
        //switch vertx's logging delegate to slf4j.
        System.setProperty("vertx.logger-delegate-factory-class-name", Constant.LOGGER_FACTORY);
        //switch hazelcast's logging delegate to slf4j.
        System.setProperty("hazelcast.logging.type", "slf4j");
    }

}
