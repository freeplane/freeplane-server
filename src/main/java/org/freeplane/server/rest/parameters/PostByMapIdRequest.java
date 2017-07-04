package org.freeplane.server.rest.parameters;

public class PostByMapIdRequest {

	private String xmlVersion;
	private String contents;
	
	public String getXmlVersion() {
		return xmlVersion;
	}
	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	@Override
	public String toString() {
		return "PostByMapIdRequest [xmlVersion=" + xmlVersion + ", contents=" + contents + "]";
	}
}
