package org.freeplane.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.freeplane.collaboration.event.MapUpdated;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapCreated;
import org.freeplane.collaboration.event.messages.ImmutableMapId;
import org.freeplane.collaboration.event.messages.ImmutableMessageId;
import org.freeplane.collaboration.event.messages.MapCreateRequested;
import org.freeplane.collaboration.event.messages.MapCreated;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.MapUpdateRequested;
import org.freeplane.collaboration.event.messages.Message;
import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.server.genericmessages.GenericMapCreateRequested;
import org.freeplane.server.genericmessages.GenericMapUpdateRequested;
import org.freeplane.server.genericmessages.GenericMessage;
import org.freeplane.server.persistency.MongoDbEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class WebSocketServer extends TextWebSocketHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MongoDbEventStore mongoDbEventStore;
	
	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	Map<WebSocketSession,MapId> session2MapId = new HashMap<>();
	
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//    	logger.info("Server received text: {}", message.getPayload());
    	GenericMessage msg = objectMapper.readValue(message.getPayload(), GenericMessage.class);
		logger.info("Message received: {}", msg);
		
    	if (msg instanceof GenericMapCreateRequested)
    	{
    		logger.info("MapCreateRequested received!");
    		ImmutableMapId mapId = ImmutableMapId.of("mapIdFromServer");
			session2MapId.put(session, mapId);
    		MapCreated mapCreated = ImmutableMapCreated.builder()
    				.from(msg).id(mapId).requestId(ImmutableMessageId.of("myServerMsgId"))
    				.build();
    		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(mapCreated)));
    	}
    	else if (msg instanceof GenericMapUpdateRequested)
    	{
    		MapId correspondingMsgId = session2MapId.get(session);
    		
    		logger.info("MapUpdateRequested received!");
    		GenericMapUpdateRequested msgMapUpdateRequested = (GenericMapUpdateRequested)msg;
    		GenericUpdateBlockCompleted updateBlockCompleted = msgMapUpdateRequested.update();
    		
//    		logger.info("updateBlockCompleted: {}", updateBlockCompleted);
    		for (ObjectNode event : updateBlockCompleted.updateBlock())
    		{
//    			GenericEvent genericEvent = new GenericEvent.Builder()
//    			 	.mapId(correspondingMsgId.value())
//    			 	.nodeId("DUMMYNODEID")
//    			 	.
    			logger.info("ObjectNode: {}", event.toString());
    		}
    		
    		// distribute to clients as GenericUpdateBlockCompleted!!
    	}
    }

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
	}

}
