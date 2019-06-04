package com.github.masterdxy.gateway.plugin;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Plugins {


    public void execute() {

    }

    public Plugins() {
        //Load all plugins
        //Create pluginChain
    }


    private static class PluginChain {

        private List<Plugin> plugins;


    }
}
