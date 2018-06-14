package org.freeplane.server.adapters.mongodb;

import org.freeplane.server.adapters.mongodb.users.User;
import org.freeplane.server.adapters.mongodb.users.UserRepository;
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
public class MongoDbUserStoreTest {

	@Configuration
	@ComponentScan
	@EnableMongoRepositories("org.freeplane.server.adapters.mongodb.users")
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
	private UserRepository userRepository;
	
	@Test
	public void shouldSaveAndRetrieveUser()
	{
		userRepository.deleteAll();
		
		User user1 = new User("user1", "password1");

		userRepository.save(user1);
		
		User u = userRepository.findByUserName("user1");
		Assert.assertEquals(u, user1);
	}
	
}
