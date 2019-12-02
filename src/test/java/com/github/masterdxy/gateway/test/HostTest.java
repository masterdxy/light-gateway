package com.github.masterdxy.gateway.test;

import org.apache.dubbo.common.utils.NetUtils;

import org.junit.jupiter.api.Test;

public class HostTest {
    @Test void getAddress() {
        System.out.println(NetUtils.getIpByHost(NetUtils.getLocalHost()));
    }
}
