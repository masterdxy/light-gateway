package com.github.masterdxy.gateway.plugin.impl.dubbo;

import com.google.common.collect.Maps;

import java.util.Map;

public class DubboServiceIdentity {

    private String interfaceName;
    private String version;
    private Map<String,Object> methodMapping;

    public static DubboServiceIdentity as(String interfaceName,String version){
        return new DubboServiceIdentity(interfaceName,version, Maps.newHashMap());
    }

    private DubboServiceIdentity(String interfaceName, String version, Map<String, Object> methodMapping) {
        this.interfaceName = interfaceName;
        this.version = version;
        this.methodMapping = methodMapping;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getVersion() {
        return version;
    }
}
