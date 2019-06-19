package com.github.masterdxy.gateway.plugin;

import com.github.masterdxy.gateway.spring.SpringContext;
import io.vertx.ext.web.RoutingContext;

import java.util.Comparator;
import java.util.List;

public class PluginChain {

    private int index;
    private List<Plugin> plugins;
    private RoutingContext context;

    /*
    EndpointSelector -> AuthPlugin
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
