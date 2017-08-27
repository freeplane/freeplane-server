package org.freeplane.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@SpringBootApplication
@EnableMongoRepositories("org.freeplane.server.persistency.events")
public class Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	@Value("${spring.data.mongodb.host}")
	private String mongoHost;

	@Value("${spring.data.mongodb.database}")
	private String mongoDatabase;

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
        	LOGGER.info("*** Starting Freeplane Server. ***");
        };
    }

	@Bean
	public Mongo mongo() throws Exception {
		return new MongoClient(mongoHost);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), mongoDatabase);
	}
}
