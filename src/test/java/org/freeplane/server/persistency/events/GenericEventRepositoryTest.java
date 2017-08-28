package org.freeplane.server.persistency.events;

import java.util.Arrays;
import java.util.List;

import org.freeplane.server.persistency.events.GenericEvent.CompositeKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class GenericEventRepositoryTest {

	@Configuration
	@EnableMongoRepositories("org.freeplane.server.persistency.events")
	@PropertySource("classpath:application.properties")
	static class TestConfiguration {

		@Value("${spring.data.mongodb.test.host}")
		private String mongoTestHost;

		@Value("${spring.data.mongodb.test.database}")
		private String mongoTestDatabase;

		@Bean
		public Mongo mongo() throws Exception {
			return new MongoClient(mongoTestHost);
		}

		@Bean
		public MongoTemplate mongoTemplate() throws Exception {
			return new MongoTemplate(mongo(), mongoTestDatabase);
		}
	}
	
	@Autowired
	private GenericEventRepository genericEventRepository;

	@Test
	public void shouldSaveAndRetrieveGenericEvent()
	{
		genericEventRepository.deleteAll();
		
		final String json1 = "{\"content\": \"json1\");";
		GenericEvent genericEvent1 = new GenericEvent("mapId1", "nodeId1", "CORE", json1);

		genericEventRepository.save(genericEvent1);
		
		Assert.assertEquals(json1, genericEventRepository.findByKey(new CompositeKey("mapId1:nodeId1:CORE", 1L)).getJson());
		Assert.assertEquals(1, genericEventRepository.findById("mapId1:nodeId1:CORE").size());
	}
	
	@Test
	public void checkMultipleVersionsOfDocumentWithOneId() {
		genericEventRepository.deleteAll();
		
		GenericEvent genericEvent1 = new GenericEvent("mapId1", "nodeId1", "CORE", "{\"content\": \"json1\");");
		GenericEvent genericEvent2 = new GenericEvent("mapId1", "nodeId1", "CORE", 2L, "{\"content\": \"json1v2\");");
		GenericEvent genericEvent3 = new GenericEvent("mapId1", "nodeId1", "CORE", 3L, "{\"content\": \"json1v3\");");
		
		GenericEvent genericEvent4 = new GenericEvent("mapId1", "nodeId2", "CORE", "{\"content\": \"json2\");");
		GenericEvent genericEvent5 = new GenericEvent("mapId1", "nodeId2", "DETAILS", "{\"content\": \"json3\");");
		GenericEvent genericEvent6 = new GenericEvent("mapId2", "nodeId1", "CORE", "{\"content\": \"json4\");");
		GenericEvent genericEvent7 = new GenericEvent("mapId2", "nodeId1", "CORE", 33L, "{\"content\": \"json4v33\");");
		
		genericEventRepository.save(genericEvent1);
		genericEventRepository.save(genericEvent2);
		genericEventRepository.save(genericEvent1);
		genericEventRepository.save(genericEvent3);
		
		Assert.assertEquals("{\"content\": \"json1\");", genericEventRepository.findByKey(new CompositeKey("mapId1:nodeId1:CORE", 1L)).getJson());
		Assert.assertEquals("{\"content\": \"json1\");", genericEventRepository.findByKey("mapId1:nodeId1:CORE", 1L).getJson());
		Assert.assertEquals("{\"content\": \"json1v3\");", genericEventRepository.findByKey("mapId1:nodeId1:CORE", 3L).getJson());
		Assert.assertEquals(3, genericEventRepository.findById("mapId1:nodeId1:CORE").size());

		genericEventRepository.save(genericEvent4);
		genericEventRepository.save(genericEvent5);
		genericEventRepository.save(genericEvent6);
		genericEventRepository.save(genericEvent7);
		
		Assert.assertEquals("{\"content\": \"json2\");", genericEventRepository.findByKey("mapId1:nodeId2:CORE", 1L).getJson());
		Assert.assertEquals(1, genericEventRepository.findById("mapId1:nodeId2:CORE").size());
		Assert.assertEquals("{\"content\": \"json3\");", genericEventRepository.findByKey("mapId1:nodeId2:DETAILS", 1L).getJson());
		Assert.assertEquals(1, genericEventRepository.findById("mapId1:nodeId2:DETAILS").size());
		Assert.assertEquals("{\"content\": \"json4\");", genericEventRepository.findByKey("mapId2:nodeId1:CORE", 1L).getJson());
		Assert.assertEquals("{\"content\": \"json4v33\");", genericEventRepository.findByKey("mapId2:nodeId1:CORE", 33L).getJson());
		Assert.assertEquals(2, genericEventRepository.findById("mapId2:nodeId1:CORE").size());
		
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapId("mapId1"),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3, genericEvent4, genericEvent5));
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapId("mapId2"),
				Arrays.asList(genericEvent6, genericEvent7));
		
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapIdAndNodeId("mapId1", "nodeId1"),
			Arrays.asList(genericEvent1, genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapIdAndNodeId("mapId1", "nodeId2"),
				Arrays.asList(genericEvent4, genericEvent5));
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapIdAndNodeId("mapId2", "nodeId1"),
				Arrays.asList(genericEvent6, genericEvent7));
		
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapIdAndNodeIdAndContentType("mapId1", "nodeId1", "CORE"),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapIdAndNodeIdAndContentType("mapId2", "nodeId1", "CORE"),
				Arrays.asList(genericEvent6, genericEvent7));
		
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapIdAndContentType("mapId1", "CORE"),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3, genericEvent4));
		assertListsAreEqualIgnoringOrder(genericEventRepository.findByMapIdAndContentType("mapId2", "CORE"),
				Arrays.asList(genericEvent6, genericEvent7));
	}
	
	private void assertListsAreEqualIgnoringOrder(final List<GenericEvent> expected, List<GenericEvent> actual)
	{
		Assert.assertEquals(expected.size(), actual.size());
		Assert.assertTrue(expected.containsAll(actual));
	}
}
