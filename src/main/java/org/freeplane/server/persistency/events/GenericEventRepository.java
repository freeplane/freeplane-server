package org.freeplane.server.persistency.events;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenericEventRepository extends MongoRepository<GenericEvent, String> {
//	List<GenericEvent> findByCompositeKey(final String mapId, final String nodeId, final String contentType);
}