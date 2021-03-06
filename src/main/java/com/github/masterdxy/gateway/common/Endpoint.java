package com.github.masterdxy.gateway.common;

import org.jfaster.mango.annotation.ID;

import java.io.Serializable;

//(version in DTO)
//Parse config file e.g.  /api/crud --> dubbo --> com.xxx.CRUDService --> auth   --> LB_DEF  --> Rate --> Sign
//                        /api/echo --> http  --> http://127.0.0.1:8001 -> no auth --> LB_DEF -->  Rate --> Sign
public class Endpoint implements Serializable {
	
	@ID
	private Long    id;
	//match uriPattern : /crud, matcher will use AntPathMatcher
	private String  uriPattern;
	//ONLY support http/dubbo
	private String  upstreamType;
	//for http is proxy server urls use "," split
	//for dubbo is interface name
	private String  upstreamUrl;
	//auth use header value as key find in auth redis
	private boolean isNeedAuth;
	//sign check for this epc
	private boolean isNeedSign;
	
	/**
	 * timeout ms
	 */
	private long timeoutInMs;
	
	private String namespaceId;
	private String version;
	
	//0 OK 1 stop
	private int status;
	
	public String getUriPattern () {
		return uriPattern;
	}
	
	public void setUriPattern (String uriPattern) {
		this.uriPattern = uriPattern;
	}
	
	public String getUpstreamType () {
		return upstreamType;
	}
	
	public void setUpstreamType (String upstreamType) {
		this.upstreamType = upstreamType;
	}
	
	public String getUpstreamUrl () {
		return upstreamUrl;
	}
	
	public void setUpstreamUrl (String upstreamUrl) {
		this.upstreamUrl = upstreamUrl;
	}
	
	public boolean isNeedAuth () {
		return isNeedAuth;
	}
	
	public void setNeedAuth (boolean needAuth) {
		isNeedAuth = needAuth;
	}
	
	public boolean isNeedSign () {
		return isNeedSign;
	}
	
	public void setNeedSign (boolean needSign) {
		isNeedSign = needSign;
	}
	
	public Long getId () {
		return id;
	}
	
	public void setId (Long id) {
		this.id = id;
	}
	
	public String getNamespaceId () {
		return namespaceId;
	}
	
	public void setNamespaceId (String namespaceId) {
		this.namespaceId = namespaceId;
	}
	
	public String getVersion () {
		return version;
	}
	
	public void setVersion (String version) {
		this.version = version;
	}
	
	public int getStatus () {
		return status;
	}
	
	public void setStatus (int status) {
		this.status = status;
	}
	
	public long getTimeoutInMs () {
		return timeoutInMs;
	}
	
	public void setTimeoutInMs (long timeoutInMs) {
		this.timeoutInMs = timeoutInMs;
	}
}
