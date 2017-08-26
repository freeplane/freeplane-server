package org.freeplane.server.persistency;

import org.springframework.stereotype.Component;

@Component
public class MongoDbEventStore implements EventStore {

	@Override
	public void store(String mapId, String nodeId, String contentType,
			long version, String content) {
	}

}
