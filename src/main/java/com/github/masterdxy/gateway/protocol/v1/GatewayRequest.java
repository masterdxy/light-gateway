package com.github.masterdxy.gateway.protocol.v1;

import java.util.Map;

public class GatewayRequest {

    private String namespace;
    private String version;

    private Map<String, String> data;


    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
