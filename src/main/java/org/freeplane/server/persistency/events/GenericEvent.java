package org.freeplane.server.persistency.events;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "genericevent_compound_key1", def = "{'mapId' : 1, 'nodeId': 1}"),
    @CompoundIndex(name = "genericevent_compound_key2", def = "{'mapId' : 1, 'nodeId': 1, 'contentType': 1}"),
    @CompoundIndex(name = "genericevent_compound_key3", def = "{'mapId' : 1, 'contentType': 1}")
})
public class GenericEvent {

	@Id
	private final CompositeKey key;
	
	// duplicated, because key.id is hard to match!
	private final String id;
	
	private final String mapId;

	// Optional! \exists events for whole map!!
	private final String nodeId;

	private final String contentType;
	
	// the index of this event in the batch update:
	// (used to reconstruct the order of events in a batch event!)
	private final long eventIndex;
	
	private final String json;

	@CreatedDate
	private Instant createdDate;
	
	public static class Builder {
		private String mapId;
		private String nodeId;
		private String contentType;
		private long mapRevision = -1L;
		private long eventIndex = 1L;
		private String json;
		
		public Builder mapId(String mapId)
		{
			this.mapId = mapId;
			return this;
		}

		public Builder nodeIdIf(boolean condition, String nodeId)
		{
			if (condition)
			{
				this.nodeId = nodeId;
			}
			else
			{
				this.nodeId = "DUMMYNODEID";
			}
			return this;
		}

		public Builder contentType(String contentType)
		{
			this.contentType = contentType;
			return this;
		}

		public Builder mapRevision(long mapRevision)
		{
			this.mapRevision = mapRevision;
			return this;
		}
		
		public Builder eventIndex(long eventIndex)
		{
			this.eventIndex = eventIndex;
			return this;
		}
		
		public Builder json(String json)
		{
			this.json = json;
			return this;
		}
		
		public GenericEvent buildInitialEvent()
		{
			mapRevision = 1L;
			return build();
		}
		
		public GenericEvent build()
		{
			if (nodeId == null)
			{
				nodeId = "<nonode>";
			}
			return new GenericEvent(mapId, nodeId, contentType, mapRevision, eventIndex, json);
		}
	}

	public GenericEvent(final String mapId, final String nodeId,
			final String contentType, final long mapRevision, final long eventIndex, final String json) {
		// id = new CompositeKey(mapId, nodeId, contentType);
		this.key = new CompositeKey(mapId + ":" + nodeId + ":" + contentType, mapRevision, eventIndex);
		this.id = this.key.getId();
		this.mapId = mapId;
		this.nodeId = nodeId;
		this.contentType = contentType;
		this.json = json;
		this.eventIndex = eventIndex;
	}

	public GenericEvent(final String mapId, final String nodeId,
			final String contentType, final String json) {
		this(mapId, nodeId, contentType, 1L, 1L, json);
	}

	// see https://github.com/cybuch/sample-spring-data-mongo-composite-key
	// and
	// http://software-sympathy.blogspot.de/2017/01/spring-data-with-mongodb-and-composite.html
	static class CompositeKey implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 5392273919922170345L;

		private final String id;
		
		// mapRevision is per map!!	
		// mapRevision is the "batchid" (corresponds to one batch update!)
		private final long mapRevision;
		
		private final long eventIndex;

		public CompositeKey(final String id, final long mapRevision, final long eventIndex) {
			this.id = id;
			this.mapRevision = mapRevision;
			this.eventIndex = eventIndex;
		}

		public String getId() {
			return id;
		}

		public long getMapRevision() {
			return mapRevision;
		}
		
		public long getEventIndex() {
			return eventIndex;
		}

		@Override
		public String toString()
		{
			return String.format("key[%s-%d-%d]", id, mapRevision, eventIndex);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + (int) (mapRevision ^ (mapRevision >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CompositeKey other = (CompositeKey) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (mapRevision != other.mapRevision)
				return false;
			return true;
		}
		
		
	}
	
	// without this, I get:
	// MappingInstantiationException: Failed to instantiate org.freeplane.server.persistency.events.GenericEvent using constructor NO_CONSTRUCTOR with arguments 
	private GenericEvent() {
		this(null, null, null, null);
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public CompositeKey getKey() {
		return key;
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
	
	public long getEventIndex() {
		return eventIndex;
	}
	
	public String getJson() {
		return json;
	}

	@Override
	public String toString() {
		return String.format(
				"GenericEvent[id=%s, mapId=%s, nodeId=%s, contentType=%s, eventIndex=%d]",
				key, mapId, nodeId, contentType, eventIndex);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + (int) (eventIndex ^ (eventIndex >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((json == null) ? 0 : json.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericEvent other = (GenericEvent) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (eventIndex != other.eventIndex)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (json == null) {
			if (other.json != null)
				return false;
		} else if (!json.equals(other.json))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (mapId == null) {
			if (other.mapId != null)
				return false;
		} else if (!mapId.equals(other.mapId))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}

	
}
