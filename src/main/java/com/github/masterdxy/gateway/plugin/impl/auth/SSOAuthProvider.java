package com.github.masterdxy.gateway.plugin.impl.auth;

import org.springframework.stereotype.Component;

/**
 * @author tomoyo
 */
@Component
public class SSOAuthProvider {

    public AuthResult doAuth(String token) {
        AuthResult authResult = new AuthResult();
        authResult.setAuthCode(1000);
        authResult.setUserId("GWU19002");
        authResult.setExpiredDateTime("2020-12-31 23:59:59");
        return authResult;
    }
}
