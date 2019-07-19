package com.github.masterdxy.gateway.plugin.impl.dubbo;

import org.apache.dubbo.rpc.service.GenericService;

import java.util.Objects;
import java.util.Optional;

public class DubboProxyService {

    private GenericService internal;
    private DubboServiceIdentity serviceIdentity;

    //todo monitor here
    //todo try catch here
    public Optional<Object> invoke(String method, Object param) {
        return Optional.ofNullable(internal.$invoke(method, null, new Object[]{param}));
    }

    public static DubboProxyService wrap(DubboServiceIdentity serviceIdentity, GenericService genericService) {
        return new DubboProxyService(Objects.requireNonNull(serviceIdentity, "DubboServiceIdentity is null"),
                Objects.requireNonNull(genericService, "genericService is null"));
    }

    private DubboProxyService(DubboServiceIdentity serviceIdentity, GenericService internal) {
        this.internal = internal;
        this.serviceIdentity = serviceIdentity;
    }
}
