package com.github.masterdxy.gateway.test.spring;

import com.github.masterdxy.gateway.config.GatewaySpringConfiguration;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.spring.SpringContext;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SpringContextTest {

    private static Logger logger = LoggerFactory.getLogger(SpringContextTest.class);

    @Test void testPluginsSort() {
        SpringContext.initContext(GatewaySpringConfiguration.class);
        List<Plugin> plugins = SpringContext.instances(Plugin.class);
        plugins.sort(Comparator.comparingInt(Plugin::order));
        logger.info("Plugins : {}",
            plugins.stream().map(p -> ClassUtils.getShortClassName(p, "null")).collect(Collectors.joining(",")));
    }
}
