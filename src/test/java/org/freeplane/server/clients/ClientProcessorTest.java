package org.freeplane.server.clients;

import java.util.Optional;

import org.freeplane.collaboration.event.MapUpdated;
import org.freeplane.collaboration.event.children.ImmutableNodeInserted;
import org.freeplane.collaboration.event.children.ImmutableNodePosition;
import org.freeplane.collaboration.event.children.Side;
import org.freeplane.collaboration.event.content.core.CoreMediaType;
import org.freeplane.collaboration.event.content.core.ImmutableCoreUpdated;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapId;
import org.freeplane.collaboration.event.messages.ImmutableUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableUserId;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.UserId;
import org.freeplane.server.Application;
import org.freeplane.server.json.JacksonConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ClientProcessorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientProcessorTest.class);
	
	@Configuration
	@Import({ Application.class })
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
	private ClientProcessor4Test clientProcessor4Test;
	
	@Test
	public void test()
	{
		logger.info("ClientProcessorTest.test()");
		
		WebSocketSession4Test session = new WebSocketSession4Test("session1");
		
		MapId mapId = clientProcessor4Test.registerNewMap(session);
		
		// create a short sequence of updates:
		MapUpdated update1 = ImmutableNodeInserted.builder()
				.position(ImmutableNodePosition.builder().parentId("roomongoDbEventStoretNode").position(0).side(Optional.of(Side.RIGHT)).build())
				.nodeId("ID123")
				.build();
		MapUpdated update2 = ImmutableCoreUpdated.builder()
				.content("new core content")
				.nodeId("ID123")
				.mediaType(CoreMediaType.PLAIN_TEXT)
				.build();
		UserId userId = ImmutableUserId.of("felixUser");
		UpdateBlockCompleted updateFromClient = ImmutableUpdateBlockCompleted.builder()
				.userId(userId)
				.mapId(mapId)
				.mapRevision(1L)
				.addUpdateBlock(update1, update2)
				.build();
		
		final String tmpJson = clientProcessor4Test.deserialize(updateFromClient);
		GenericUpdateBlockCompleted updateFromClient2 = clientProcessor4Test.serialize(tmpJson, GenericUpdateBlockCompleted.class);

		clientProcessor4Test.processSingleClientUpdates(session, updateFromClient2);
	}

}
