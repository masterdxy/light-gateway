package com.github.masterdxy.gateway.plugin;

import com.github.masterdxy.gateway.spring.SpringContext;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//TODO support A/B test and ParamSelectRoute Plugin
public class PluginChain {
    private static Logger logger = LoggerFactory.getLogger(PluginChain.class);
    private int index;
    private List<Plugin> plugins;
    private RoutingContext context;

    /*
    EndpointSelector ->  MockPlugin ->  SignPlugin -> AuthPlugin
                                                        -> RateLimitPlugin
                                                                |->  DubboPlugin
                                                                |->  RewritePlugin  ->  LBPlugin  ->  HttpPlugin


    context modify :
        EndpointSelector (add GATEWAY_ENDPOINT)
        AuthPlugin (add GATEWAY_AUTH)

     */

    public boolean execute() {
        if (index < plugins.size()) {
            Plugin plugin = plugins.get(index++);
            if (plugin.match(context))
                return plugin.execute(context, this);
            execute();
        }
        return true;
    }

    public static PluginChain build(RoutingContext context) {
        List<Plugin> plugins = SpringContext.instances(Plugin.class);
        plugins.sort(Comparator.comparingInt(Plugin::order));
        logger.info("Plugins : {}", plugins.stream().map(p -> ClassUtils.getShortClassName(p, "null"
                                        )).collect(Collectors.joining(",")));
        PluginChain chain = new PluginChain();
        chain.setContext(context);
        chain.setPlugins(plugins);
        return chain;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public RoutingContext getContext() {
        return context;
    }

    public void setContext(RoutingContext context) {
        this.context = context;
    }
}
