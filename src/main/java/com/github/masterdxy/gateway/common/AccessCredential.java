package com.github.masterdxy.gateway.common;

import java.io.Serializable;

/**
 * @author tomoyo
 */
public class AccessCredential implements Serializable {
	
	private Long   id;
	private String accessKey;
	private String accessSecret;
	private Long   expireInSecond;
	private String namespaceId;
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public String getAccessKey () {
		return accessKey;
	}
	
	public void setAccessKey (String accessKey) {
		this.accessKey = accessKey;
	}
	
	public String getAccessSecret () {
		return accessSecret;
	}
	
	public void setAccessSecret (String accessSecret) {
		this.accessSecret = accessSecret;
	}
	
	public Long getExpireInSecond () {
		return expireInSecond;
	}
	
	public void setExpireInSecond (Long expireInSecond) {
		this.expireInSecond = expireInSecond;
	}
	
	public String getNamespaceId () {
		return namespaceId;
	}
	
	public void setNamespaceId (String namespaceId) {
		this.namespaceId = namespaceId;
	}
}
