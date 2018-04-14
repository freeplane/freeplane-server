package org.freeplane.server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.freeplane.server.persistency.MongoDbEventStore;
import org.freeplane.server.persistency.events.GenericEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WebSocketServer extends TextWebSocketHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoDbEventStore mongoDbEventStore;
	
	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    	logger.info("Server received text: {}", message.getPayload());
    	//GenericUpdateBlockCompleted serverUpdatesFinished = objectMapper.readValue(message.getPayload(), GenericUpdateBlockCompleted.class);
    	
    	
    	
//    	GenericUpdateBlockCompleted serverUpdatesFinished = objectMapper.readValue(message.getPayload(), GenericUpdateBlockCompleted.class);
//    	logger.info("Server received message: {}", serverUpdatesFinished);
//    	for(WebSocketSession webSocketSession : sessions) {
//    		webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(serverUpdatesFinished)));
//    	}
    }

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//the messages will be broadcasted to all users
		sessions.add(session);
	}

}
