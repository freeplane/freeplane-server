package org.freeplane.server.persistency;

public interface EventStore {
	void store(final String mapId, final String nodeId, final String contentType, final long version, final String content);
}
