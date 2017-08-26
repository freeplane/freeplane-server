package org.freeplane.server.persistency.events;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.Mongo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class GenericEventRepositoryTest {

	@Configuration
	@EnableMongoRepositories("org.freeplane.server.persistency.events")
	static class TestConfiguration {
		public @Bean Mongo mongo() throws Exception {
			return new Mongo("localhost");
		}

		public @Bean MongoTemplate mongoTemplate() throws Exception {
			return new MongoTemplate(mongo(), "testdb");
		}
	}
	
	@Autowired
	private GenericEventRepository genericEventRepository;
	
	@Test
	public void shouldSaveAndRetrieveGenericEvent() {
		final String json = "{\"content\": \"bla\");";
		GenericEvent genericEvent = new GenericEvent("mapId1", "nodeId1", "CORE", json);
		genericEventRepository.save(genericEvent);
		Assert.assertEquals(json, genericEventRepository.findOne("mapId1:nodeId1:CORE").getJson());
		//Assert.assertEquals(1, genericEventRepository.findByCompositeKey("mapId1", "nodeId1", "CORE"));
	}
}
