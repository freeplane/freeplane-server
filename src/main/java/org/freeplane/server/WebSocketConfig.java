package org.freeplane.server;

import org.freeplane.server.wspoc2.PocTestServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;


// docu:
// http://www.devglan.com/spring-boot/spring-websocket-integration-example-without-stomp
// https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example
// https://www.youtube.com/watch?v=nxakp15CACY

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	private PocTestServer pocTestServer;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ExceptionWebSocketHandlerDecorator(pocTestServer), "/freeplane");
    }
}