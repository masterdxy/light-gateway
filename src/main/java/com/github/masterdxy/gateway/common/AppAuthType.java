package com.github.masterdxy.gateway.common;

/**
 * @author tomoyo
 */

public enum AppAuthType {
	
	/**
	 * 无认证方式
	 */
	NONE("none"),
	/**
	 * 通过AccessKey Secret验证
	 */
	AK("ak"),
	/**
	 * 证书验证
	 */
	CERT("cert"),
	/**
	 * JWT认证
	 */
	JWT("jwt");
	
	String value;
	
	AppAuthType (String value) {
		this.value = value;
	}
}
