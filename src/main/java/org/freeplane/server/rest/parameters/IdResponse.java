package org.freeplane.server.rest.parameters;

public class IdResponse {

	private int mapId;
	private int mapRevisionNumber;
	private String mapContent;
	
	public String getMapContent() {
		return mapContent;
	}

	public void setMapContent(String mapContent) {
		this.mapContent = mapContent;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getMapRevisionNumber() {
		return mapRevisionNumber;
	}

	public void setMapRevisionNumber(int mapRevisionNumber) {
		this.mapRevisionNumber = mapRevisionNumber;
	}

	@Override
	public String toString() {
		return "IdResponse [mapId=" + mapId + ", mapRevisionNumber=" + mapRevisionNumber + ", mapContent=" + mapContent
				+ "]";
	}

}
