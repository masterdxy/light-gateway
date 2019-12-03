package com.github.masterdxy.gateway.test.client.params;

import com.alibaba.fastjson.JSON;
import com.github.masterdxy.gateway.protocol.v1.GatewayRequest;
import org.junit.jupiter.api.Test;

class GatewayRequestTest {
    @Test
    void testMapToJson() {
        GatewayRequest req = new GatewayRequest();

        System.out.println(JSON.toJSONString(req));
    }
}
