package com.github.masterdxy.gateway.common;

import java.io.Serializable;

/**
 * namespace is a group of upstream and endpoint.
 *
 * @author tomoyo
 */
public class Namespace implements Serializable {
	
	private String id;
	private String owner;
	private String status;
	private String description;
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public String getOwner () {
		return owner;
	}
	
	public void setOwner (String owner) {
		this.owner = owner;
	}
	
	public String getStatus () {
		return status;
	}
	
	public void setStatus (String status) {
		this.status = status;
	}
	
	public String getDescription () {
		return description;
	}
	
	public void setDescription (String description) {
		this.description = description;
	}
}
