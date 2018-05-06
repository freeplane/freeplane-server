package org.freeplane.server.persistency;

import java.util.List;

import org.freeplane.server.persistency.events.GenericEvent;

public interface EventStore {
	
	void deleteAll();

	void store(final GenericEvent genericEvent);
	
	GenericEvent findByKey(final String id, final long mapRevision, long eventIndex);

	// get all versions of a document
	List<GenericEvent> findById(final String id);

	// get multiple events
	List<GenericEvent> findByMapId(final String mapId);
	List<GenericEvent> findByMapIdAndNodeId(final String mapId, final String nodeId);
	List<GenericEvent> findByMapIdAndNodeIdAndContentType(final String mapId, final String nodeId, final String contentType);
	List<GenericEvent> findByMapIdAndContentType(final String mapId, final String contentType);
	
	List<GenericEvent> findByIdAndMaxMapRevision(final String id, final long maxMapRevision);
	
	List<GenericEvent> findByIdAndMinMapRevision(final String id, final long minMapRevision);
	
	List<GenericEvent> findByMapIdAndMapRevision(final String mapId, final long mapRevision);
}
