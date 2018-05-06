package org.freeplane.server;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapCreated;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateDistributed;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateProcessed;
import org.freeplane.collaboration.event.messages.ImmutableMessageId;
import org.freeplane.collaboration.event.messages.MapCreateRequested;
import org.freeplane.collaboration.event.messages.MapCreated;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.MapUpdateDistributed;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed.UpdateStatus;
import org.freeplane.collaboration.event.messages.Message;
import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.server.clients.ClientProcessor;
import org.freeplane.server.genericmessages.GenericMapUpdateRequested;
import org.freeplane.server.genericmessages.ImmutableGenericMapUpdateRequested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

@Component
public class WebSocketServer extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ClientProcessor clientProcessor;

	@PostConstruct
	public void init(){
		objectMapper.registerSubtypes(new NamedType(ImmutableGenericMapUpdateRequested.class, "MapUpdateRequested"));
	}

	// many clients:
	// server tracks synched maprevision and mapid for all maps!
	// status=ACCEPTED: no contention
	// status=MERGED: events from different clients were reordered if client-maprevision is already used!
	//                change maprevision for some clients!
	// 1 thread per mapid for sending updates (TODO: new message on freeplane-events "MapUpdateDistributed")

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//    	logger.info("Server received text: {}", message.getPayload());
    	Message msg = objectMapper.readValue(message.getPayload(), Message.class);
		logger.info("Message received: {}", msg.getClass().getSimpleName());

    	if (msg instanceof MapCreateRequested)
    	{
    		logger.info("MapCreateRequested received!");
    		MapId mapId = clientProcessor.registerNewMap(session);
    		MapCreated mapCreated = ImmutableMapCreated.builder()
    				.from(msg)
    				.id(mapId)
    				.requestId(ImmutableMessageId.of("myServerMsgId"))
    				.build();
    		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(mapCreated)));
    	}
    	else if (msg instanceof GenericMapUpdateRequested)
    	{
    		logger.info("MapUpdateRequested received!");
    		GenericMapUpdateRequested msgMapUpdateRequested = (GenericMapUpdateRequested)msg;
    		GenericUpdateBlockCompleted updateBlockCompleted = msgMapUpdateRequested.update();

    		Pair<UpdateStatus, UpdateBlockCompleted> result = clientProcessor.processSingleClientUpdates(session, updateBlockCompleted);
    		
    		MapUpdateProcessed mapUpdateProcessed = ImmutableMapUpdateProcessed.builder()
    				.messageId(ImmutableMessageId.of("myServerMsgId"))
    				.requestId(ImmutableMessageId.of("myServerMsgId4UpdateStatus"))
    				.status(result.getLeft())
    				.build();

    		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(mapUpdateProcessed)));
    		
    		MapUpdateDistributed mapUpdateDistributed = ImmutableMapUpdateDistributed.builder()
    				.from(msg)
    				.requestId(ImmutableMessageId.of("myMsgMapUpdateDistributed"))
    				.update(result.getRight())
    				.build();
    		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(mapUpdateDistributed)));
    	}
    }

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	}

}
