package com.github.masterdxy.gateway.plugin;

import com.github.masterdxy.gateway.spring.SpringContext;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class PluginChain {

    private int index;
    private List<Plugin> plugins;
    private RoutingContext context;

    public boolean execute() {
        if (index < plugins.size()) {
            Plugin plugin = plugins.get(index++);
            return plugin.execute(context, this);
        }
        return true;
    }

    public static PluginChain build(RoutingContext context) {
        PluginChain chain = new PluginChain();
        chain.setContext(context);
        chain.setIndex(0);
        chain.setPlugins(SpringContext.instances(Plugin.class));
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
