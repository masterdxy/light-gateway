package com.github.masterdxy.gateway.plugin.impl.dubbo;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

public class DubboServiceIdentity {

    private String interfaceName;
    private String version;
    private Map<String,Object> methodMapping;

    public static DubboServiceIdentity as(String interfaceName,String version){
        return new DubboServiceIdentity(Objects.requireNonNull(interfaceName,"interfaceName is null"),
                Objects.requireNonNull(version,"version is null"),
                Maps.newHashMap());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DubboServiceIdentity that = (DubboServiceIdentity) o;
        return com.google.common.base.Objects.equal(interfaceName, that.interfaceName) &&
                com.google.common.base.Objects.equal(version, that.version);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(interfaceName, version);
    }
}
