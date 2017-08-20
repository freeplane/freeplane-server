package org.freeplane.server.controller;

public class RequestGetPackage {

	private String method;
	private String id;
	private String revision;

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	@Override
	public String toString() {
		return "RequestGetPackage [method=" + method + ", id=" + id + ", revision=" + revision + "]";
	}

}
