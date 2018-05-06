package org.freeplane.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.freeplane.collaboration.event.MapUpdated;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapCreated;
import org.freeplane.collaboration.event.messages.ImmutableMapId;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateDistributed;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateProcessed;
import org.freeplane.collaboration.event.messages.ImmutableMessageId;
import org.freeplane.collaboration.event.messages.ImmutableUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableUserId;
import org.freeplane.collaboration.event.messages.MapCreateRequested;
import org.freeplane.collaboration.event.messages.MapCreated;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.MapUpdateDistributed;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed.UpdateStatus;
import org.freeplane.collaboration.event.messages.Message;
import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.server.genericmessages.GenericMapUpdateRequested;
import org.freeplane.server.genericmessages.ImmutableGenericMapUpdateRequested;
import org.freeplane.server.persistency.MongoDbEventStore;
import org.freeplane.server.persistency.events.GenericEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class WebSocketServer extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MongoDbEventStore mongoDbEventStore;

	@PostConstruct
	public void init(){
		objectMapper.registerSubtypes(new NamedType(ImmutableGenericMapUpdateRequested.class, "MapUpdateRequested"));
	}

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	Map<WebSocketSession,MapId> session2MapId = new HashMap<>();
	

	// later: distribute to clients as GenericUpdateBlockCompleted!!

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
    		ImmutableMapId mapId = ImmutableMapId.of("mapIdFromServer");
			session2MapId.put(session, mapId);
    		MapCreated mapCreated = ImmutableMapCreated.builder()
    				.from(msg).id(mapId).requestId(ImmutableMessageId.of("myServerMsgId"))
    				.build();
    		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(mapCreated)));
    	}
    	else if (msg instanceof GenericMapUpdateRequested)
    	{
    		MapId correspondingMapId = session2MapId.get(session);

    		logger.info("MapUpdateRequested received!");
    		GenericMapUpdateRequested msgMapUpdateRequested = (GenericMapUpdateRequested)msg;
    		GenericUpdateBlockCompleted updateBlockCompleted = msgMapUpdateRequested.update();

    		// TODO: for testing!
    		mongoDbEventStore.deleteAll();
    		int eventCounter = 1;
    		for (ObjectNode json : updateBlockCompleted.updateBlock())
    		{
    			final String contentType = json.get("contentType").toString();
    			final JsonNode nodeId = json.get("nodeId");
    			
    			GenericEvent genericEvent = new GenericEvent.Builder()
    			 	.mapId(correspondingMapId.value())
    			 	.nodeIdIf(nodeId != null, nodeId != null ? nodeId.toString() : null)	
    			 	.contentType(contentType)
    			 	.mapRevision(updateBlockCompleted.mapRevision())
    			 	.eventIndex(eventCounter)
    			 	.json(json.toString())
    			 	.build();
    			
    			mongoDbEventStore.store(genericEvent);
    			
//    			logger.info("ObjectNode: {}", json.toString());
    			eventCounter++;
    		}

    		MapUpdateProcessed mapUpdateProcessed = ImmutableMapUpdateProcessed.builder()
    				.from(msg)
    				.requestId(ImmutableMessageId.of("myServerMsgId4UpdateStatus"))
    				.status(UpdateStatus.MERGED)
    				.build();
    		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(mapUpdateProcessed)));
    		
    		// distribute updates back to client
    		List<MapUpdated> eventsForClient = new LinkedList<>();
    		for (GenericEvent thisEvent : mongoDbEventStore.findByMapIdAndMapRevision(correspondingMapId.value(), updateBlockCompleted.mapRevision()))
    		{
    			eventsForClient.add(objectMapper.readValue(thisEvent.getJson(), MapUpdated.class));
    		}
    		UpdateBlockCompleted updateBlockCompletedForClient = ImmutableUpdateBlockCompleted.builder()
    				.userId(ImmutableUserId.of("DUMMYUSER"))
    				.mapId(correspondingMapId)
    				.mapRevision(updateBlockCompleted.mapRevision() + 1L)
    				.updateBlock(eventsForClient)
    				.build();
    		MapUpdateDistributed mapUpdateDistributed = ImmutableMapUpdateDistributed.builder()
    				.from(msg)
    				.requestId(ImmutableMessageId.of("myMsgMapUpdateDistributed"))
    				.update(updateBlockCompletedForClient)
    				.build();
    		session.sendMessage(new TextMessage(objectMapper.writeValueAsString(mapUpdateDistributed)));
    	}
    }

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
	}

}
