package org.freeplane.server.persistency;

import org.freeplane.server.persistency.events.GenericEvent;
import org.freeplane.server.persistency.events.GenericEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoDbEventStore implements EventStore, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(MongoDbEventStore.class);
	
	@Autowired
	private GenericEventRepository genericEventRepository;

	@Override
	public void store(String mapId, String nodeId, String contentType,
			long version, String content) {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("MongoDbEventStore: creating GenericEvent!");
		final String json = "{\"content\": \"PROD!!!\");";
		GenericEvent genericEvent = new GenericEvent("mapId1", "nodeId1", "CORE", json);
		genericEventRepository.save(genericEvent);
	}

}
