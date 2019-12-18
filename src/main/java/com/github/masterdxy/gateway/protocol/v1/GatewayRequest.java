package com.github.masterdxy.gateway.protocol.v1;

public class GatewayRequest {
	
	// assign app
	private String namespace;
	// assign app version
	private String version;
	
	// only for dubbo service call
	private String method;
	
	// only mock=1 is mock request
	private int mock;
	
	// sign str for data check
	private String sign;
	
	// only for Http-POST or dubbo call
	private String data;
	
	// client : POST /api/crud/findXX/id?1, extra : /crud/findXX/id?1
	// this param is fill by request handler.
	private String extraUrl;
	
	/**
	 * auth token
	 */
	private String token;
	
	public String getToken () {
		return token;
	}
	
	public void setToken (String token) {
		this.token = token;
	}
	
	public String getNamespace () {
		return namespace;
	}
	
	public void setNamespace (String namespace) {
		this.namespace = namespace;
	}
	
	public String getVersion () {
		return version;
	}
	
	public void setVersion (String version) {
		this.version = version;
	}
	
	public String getData () {
		return data;
	}
	
	public void setData (String data) {
		this.data = data;
	}
	
	public String getMethod () {
		return method;
	}
	
	public void setMethod (String method) {
		this.method = method;
	}
	
	public int getMock () {
		return mock;
	}
	
	public void setMock (int mock) {
		this.mock = mock;
	}
	
	public String getSign () {
		return sign;
	}
	
	public void setSign (String sign) {
		this.sign = sign;
	}
	
	public String getExtraUrl () {
		return extraUrl;
	}
	
	public void setExtraUrl (String extraUrl) {
		this.extraUrl = extraUrl;
	}
}
