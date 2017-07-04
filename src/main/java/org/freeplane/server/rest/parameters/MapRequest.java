package org.freeplane.server.rest.parameters;

public class MapRequest {

	private String mapName;
	private String description;
	private String xmlVersion;
	private String mapContent;
	
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getXmlVersion() {
		return xmlVersion;
	}
	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}
	public String getMapContent() {
		return mapContent;
	}
	public void setMapContent(String mapContent) {
		this.mapContent = mapContent;
	}
	
	@Override
	public String toString() {
		return "MapRequest [mapName=" + mapName + ", description=" + description + ", xmlVersion=" + xmlVersion
				+ ", mapContent=" + mapContent + "]";
	}
	
	
}
