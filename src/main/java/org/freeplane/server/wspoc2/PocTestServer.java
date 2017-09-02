package org.freeplane.server.wspoc2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class PocTestServer extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(PocTestServer.class);
	
	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
    	logger.info("Server received message: {}", message.getPayload());
    }

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//the messages will be broadcasted to all users
		sessions.add(session);
	}
}
