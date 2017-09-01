package org.freeplane.server.persistency;

import java.util.Arrays;
import java.util.List;

import org.freeplane.server.persistency.events.GenericEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
public class MongoDbEventStoreTest {

	@Configuration
	@ComponentScan // need for autowiring MongoDbEventStore!
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
	private MongoDbEventStore mongoDbEventStore;

	@Test
	public void shouldSaveAndRetrieveGenericEvent()
	{
		mongoDbEventStore.deleteAll();
		
		final String json1 = "{\"content\": \"json1\");";
		GenericEvent genericEvent1 = new GenericEvent("mapId1", "nodeId1", "CORE", json1);

		mongoDbEventStore.store(genericEvent1);
		
		Assert.assertEquals(json1, mongoDbEventStore.findByKey("mapId1:nodeId1:CORE", 1L).getJson());
		Assert.assertEquals(1, mongoDbEventStore.findById("mapId1:nodeId1:CORE").size());
	}
	
	@Test
	public void checkMultipleVersionsOfDocumentWithOneId() {
		mongoDbEventStore.deleteAll();
		
		GenericEvent genericEvent1 = new GenericEvent("mapId1", "nodeId1", "CORE", "{\"content\": \"json1\");");
		GenericEvent genericEvent2 = new GenericEvent("mapId1", "nodeId1", "CORE", 2L, "{\"content\": \"json1v2\");");
		GenericEvent genericEvent3 = new GenericEvent("mapId1", "nodeId1", "CORE", 3L, "{\"content\": \"json1v3\");");
		
		GenericEvent genericEvent4 = new GenericEvent("mapId1", "nodeId2", "CORE", "{\"content\": \"json2\");");
		GenericEvent genericEvent5 = new GenericEvent("mapId1", "nodeId2", "DETAILS", "{\"content\": \"json3\");");
		GenericEvent genericEvent6 = new GenericEvent("mapId2", "nodeId1", "CORE", "{\"content\": \"json4\");");
		GenericEvent genericEvent7 = new GenericEvent("mapId2", "nodeId1", "CORE", 33L, "{\"content\": \"json4v33\");");
		
		mongoDbEventStore.store(genericEvent1);
		mongoDbEventStore.store(genericEvent2);
		mongoDbEventStore.store(genericEvent1);
		mongoDbEventStore.store(genericEvent3);
		
		Assert.assertEquals("{\"content\": \"json1\");", mongoDbEventStore.findByKey("mapId1:nodeId1:CORE", 1L).getJson());
		Assert.assertEquals("{\"content\": \"json1\");", mongoDbEventStore.findByKey("mapId1:nodeId1:CORE", 1L).getJson());
		Assert.assertEquals("{\"content\": \"json1v3\");", mongoDbEventStore.findByKey("mapId1:nodeId1:CORE", 3L).getJson());
		Assert.assertEquals(3, mongoDbEventStore.findById("mapId1:nodeId1:CORE").size());

		mongoDbEventStore.store(genericEvent4);
		mongoDbEventStore.store(genericEvent5);
		mongoDbEventStore.store(genericEvent6);
		mongoDbEventStore.store(genericEvent7);
		
		Assert.assertEquals("{\"content\": \"json2\");", mongoDbEventStore.findByKey("mapId1:nodeId2:CORE", 1L).getJson());
		Assert.assertEquals(1, mongoDbEventStore.findById("mapId1:nodeId2:CORE").size());
		Assert.assertEquals("{\"content\": \"json3\");", mongoDbEventStore.findByKey("mapId1:nodeId2:DETAILS", 1L).getJson());
		Assert.assertEquals(1, mongoDbEventStore.findById("mapId1:nodeId2:DETAILS").size());
		Assert.assertEquals("{\"content\": \"json4\");", mongoDbEventStore.findByKey("mapId2:nodeId1:CORE", 1L).getJson());
		Assert.assertEquals("{\"content\": \"json4v33\");", mongoDbEventStore.findByKey("mapId2:nodeId1:CORE", 33L).getJson());
		Assert.assertEquals(2, mongoDbEventStore.findById("mapId2:nodeId1:CORE").size());
		
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapId("mapId1"),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3, genericEvent4, genericEvent5));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapId("mapId2"),
				Arrays.asList(genericEvent6, genericEvent7));
		
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapIdAndNodeId("mapId1", "nodeId1"),
			Arrays.asList(genericEvent1, genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapIdAndNodeId("mapId1", "nodeId2"),
				Arrays.asList(genericEvent4, genericEvent5));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapIdAndNodeId("mapId2", "nodeId1"),
				Arrays.asList(genericEvent6, genericEvent7));
		
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapIdAndNodeIdAndContentType("mapId1", "nodeId1", "CORE"),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapIdAndNodeIdAndContentType("mapId2", "nodeId1", "CORE"),
				Arrays.asList(genericEvent6, genericEvent7));
		
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapIdAndContentType("mapId1", "CORE"),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3, genericEvent4));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByMapIdAndContentType("mapId2", "CORE"),
				Arrays.asList(genericEvent6, genericEvent7));
	}
	
	private void assertListsAreEqualIgnoringOrder(final List<GenericEvent> actual, final List<GenericEvent> expected)
	{
		Assert.assertEquals(expected.size(), actual.size());
		Assert.assertTrue(expected.containsAll(actual));
	}

	@Test
	public void checkMinMaxVersionSearch() {
		mongoDbEventStore.deleteAll();
		
		GenericEvent genericEvent1 = new GenericEvent("mapId1", "nodeId1", "CORE", "{\"content\": \"json1\");");
		GenericEvent genericEvent2 = new GenericEvent("mapId1", "nodeId1", "CORE", 5L, "{\"content\": \"json1v5\");");
		GenericEvent genericEvent3 = new GenericEvent("mapId1", "nodeId1", "CORE", 20L, "{\"content\": \"json1v20\");");
		GenericEvent genericEvent4 = new GenericEvent("mapId2", "nodeId1", "CORE", 1L, "{\"content\": \"json1v1\");");
		GenericEvent genericEvent5 = new GenericEvent("mapId1", "nodeId2", "CORE", 1L, "{\"content\": \"json1v1\");");
		
		mongoDbEventStore.store(genericEvent1);
		mongoDbEventStore.store(genericEvent2);
		mongoDbEventStore.store(genericEvent3);
		mongoDbEventStore.store(genericEvent4);
		mongoDbEventStore.store(genericEvent5);

		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMinVersion("mapId1:nodeId1:CORE", 1L),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMinVersion("mapId1:nodeId1:CORE", 4L),
				Arrays.asList(genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMinVersion("mapId1:nodeId1:CORE", 5L),
				Arrays.asList(genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMinVersion("mapId1:nodeId1:CORE", 20L),
				Arrays.asList(genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMinVersion("mapId1:nodeId1:CORE", 21L),
				Arrays.asList());

		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMaxVersion("mapId1:nodeId1:CORE", 22L),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMaxVersion("mapId1:nodeId1:CORE", 20L),
				Arrays.asList(genericEvent1, genericEvent2, genericEvent3));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMaxVersion("mapId1:nodeId1:CORE", 5L),
				Arrays.asList(genericEvent1, genericEvent2));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMaxVersion("mapId1:nodeId1:CORE", 1L),
				Arrays.asList(genericEvent1));
		assertListsAreEqualIgnoringOrder(mongoDbEventStore.findByIdAndMaxVersion("mapId1:nodeId1:CORE", 0L),
				Arrays.asList());
	}
}
