package org.freeplane.server.persistency.events;

import java.util.List;

import org.freeplane.server.persistency.events.GenericEvent.CompositeKey;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenericEventRepository extends MongoRepository<GenericEvent, String> {
	
	// get a particular version of a document
	GenericEvent findByKey(CompositeKey id);
	default GenericEvent findByKey(final String id, final long version) {
		CompositeKey key = new CompositeKey(id, version);
		return findByKey(key);
	}
	
	// get all versions of a document
	List<GenericEvent> findById(final String id);

	// get multiple events
	List<GenericEvent> findByMapId(final String mapId);
	List<GenericEvent> findByMapIdAndNodeId(final String mapId, final String nodeId);
	List<GenericEvent> findByMapIdAndNodeIdAndContentType(final String mapId, final String nodeId, final String contentType);
	List<GenericEvent> findByMapIdAndContentType(final String mapId, final String contentType);
}