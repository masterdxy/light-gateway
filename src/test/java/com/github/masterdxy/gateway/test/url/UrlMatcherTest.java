package com.github.masterdxy.gateway.test.url;

import com.github.masterdxy.gateway.plugin.endpoint.AntPathMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlMatcherTest {

    @Test void testMatch() {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Assertions.assertTrue(antPathMatcher.match("/crud/**", "/crud/user/findOne"));
    }
}
