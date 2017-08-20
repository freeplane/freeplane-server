package org.freeplane.server.controller;

public class ResponseGetPackage {

	private String method;
	private String id;
	private String revision;
	private String contents;
	
	
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
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	@Override
	public String toString() {
		return "ResponseGetPackage [method=" + method + ", id=" + id + ", revision=" + revision + ", contents="
				+ contents + "]";
	}

}
