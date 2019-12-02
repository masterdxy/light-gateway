package com.github.masterdxy.gateway.plugin.impl.auth;

/**
 * @author tomoyo
 */
public class AuthResult {

    private String userId;
    private String expiredDateTime;
    /**
     * 1000 SUCCESS
     * 2000 AUTH FAILED
     */
    private int authCode;

    public int getAuthCode() {
        return authCode;
    }

    public void setAuthCode(int authCode) {
        this.authCode = authCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExpiredDateTime() {
        return expiredDateTime;
    }

    public void setExpiredDateTime(String expiredDateTime) {
        this.expiredDateTime = expiredDateTime;
    }
}
