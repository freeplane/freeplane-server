package org.freeplane.server.adapters.mongodb;

import java.util.List;

import org.freeplane.server.adapters.mongodb.events.GenericEvent;
import org.freeplane.server.adapters.mongodb.events.GenericEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MongoDbEventStore implements EventStore {

	@Autowired
	private GenericEventRepository genericEventRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void deleteAll()
	{
		genericEventRepository.deleteAll();
	}
	
	@Override
	public void store(final GenericEvent genericEvent) {
		genericEventRepository.save(genericEvent);
	}

	@Override
	public GenericEvent findByKey(String id, long mapRevision, long eventIndex) {
		return genericEventRepository.findByKey(id, mapRevision, eventIndex);
	}

	@Override
	public List<GenericEvent> findById(String id) {
		return genericEventRepository.findById(id);
	}

	@Override
	public List<GenericEvent> findByMapId(String mapId) {
		return genericEventRepository.findByMapId(mapId);
	}

	@Override
	public List<GenericEvent> findByMapIdAndNodeId(String mapId, String nodeId) {
		return genericEventRepository.findByMapIdAndNodeId(mapId, nodeId);
	}

	@Override
	public List<GenericEvent> findByMapIdAndNodeIdAndContentType(String mapId,
			String nodeId, String contentType) {
		return genericEventRepository.findByMapIdAndNodeIdAndContentType(mapId, nodeId, contentType);
	}

	@Override
	public List<GenericEvent> findByMapIdAndContentType(String mapId,
			String contentType) {
		return genericEventRepository.findByMapIdAndContentType(mapId, contentType);
	}

	@Override
	public List<GenericEvent> findByIdAndMaxMapRevision(String id, long maxMapRevision) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("_id.mapRevision").lte(maxMapRevision));
		query.with(new Sort(Sort.Direction.ASC, "eventIndex"));
		return mongoTemplate.find(query, GenericEvent.class);
	}

	@Override
	public List<GenericEvent> findByIdAndMinMapRevision(String id, long minMapRevision) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		query.addCriteria(Criteria.where("_id.mapRevision").gte(minMapRevision));
		query.with(new Sort(Sort.Direction.ASC, "eventIndex"));
		return mongoTemplate.find(query, GenericEvent.class);
	}

	@Override
	public List<GenericEvent> findByMapIdAndMapRevision(final String mapId, final long mapRevision)
	{
		Query query = new Query();
		query.addCriteria(Criteria.where("mapId").is(mapId));
		query.addCriteria(Criteria.where("_id.mapRevision").is(mapRevision));
		query.with(new Sort(Sort.Direction.ASC, "eventIndex"));
		return mongoTemplate.find(query, GenericEvent.class);
	}
}
