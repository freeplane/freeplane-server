package org.freeplane.server.persistency.events;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

public class GenericEvent {

	@Id
	private final String id;

	private final String mapId;

	private final String nodeId;

	private final String contentType;

	private final String json;

	@CreatedDate
	private Instant createdDate;

	public GenericEvent(final String mapId, final String nodeId,
			final String contentType, final String json) {
		// id = new CompositeKey(mapId, nodeId, contentType);
		id = mapId + ":" + nodeId + ":" + contentType;
		this.mapId = mapId;
		this.nodeId = nodeId;
		this.contentType = contentType;
		this.json = json;
	}

	// see https://github.com/cybuch/sample-spring-data-mongo-composite-key
	// see
	// http://software-sympathy.blogspot.de/2017/01/spring-data-with-mongodb-and-composite.html
	// static class CompositeKey implements Serializable {
	// /**
	// *
	// */
	// private static final long serialVersionUID = 5392273919922170345L;
	//
	// private final String mapId;
	// private final String nodeId;
	// private final String contentType;
	//
	// public CompositeKey(final String mapId, final String nodeId, final String
	// contentType)
	// {
	// this.mapId = mapId;
	// this.nodeId = nodeId;
	// this.contentType = contentType;
	// }
	//
	// public String getMapId() {
	// return mapId;
	// }
	//
	// public String getNodeId() {
	// return nodeId;
	// }
	//
	// public String getContentType() {
	// return contentType;
	// }
	// }

	public Instant getCreatedDate() {
		return createdDate;
	}

	public String getId() {
		return id;
	}

	public String getMapId() {
		return mapId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getContentType() {
		return contentType;
	}

	public String getJson() {
		return json;
	}

	@Override
	public String toString() {
		return String.format(
				"GenericEvent[mapId=%s, nodeId=%s, contentType=%s]", mapId,
				nodeId, contentType);
	}
}
