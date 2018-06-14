package org.freeplane.server.security.authserver;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
public class AuthorizationServerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}
	 
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		Map<String, Object> props = new HashMap<>();
		props.put("spring.config.name", "authorizationServerApplication");
		application.properties(props);
		return application.sources(AuthorizationServerApplication.class);
	}
	 
}
