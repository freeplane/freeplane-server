package org.freeplane.server;

import java.util.List;

import org.freeplane.server.json.JacksonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Autowired
	private JacksonConfiguration jacksonConfiguration;
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	// send everything to /topic directly to simple broker!
        config.enableSimpleBroker("/topic");
        // /app/FOO always forwarded to controller!
        config.setApplicationDestinationPrefixes("/freeplane");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/freeplane").withSockJS();
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> converters) {
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setObjectMapper(jacksonConfiguration.objectMapper());
		converters.add(mappingJackson2MessageConverter);
        converters.add(new StringMessageConverter());
        return false;
    }
}