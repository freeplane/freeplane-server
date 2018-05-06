package org.freeplane.server.clients;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.freeplane.collaboration.event.MapUpdated;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateDistributed;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateProcessed;
import org.freeplane.collaboration.event.messages.ImmutableMessageId;
import org.freeplane.collaboration.event.messages.ImmutableUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableUserId;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.MapUpdateDistributed;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed.UpdateStatus;
import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.server.genericmessages.ImmutableGenericMapUpdateRequested;
import org.freeplane.server.persistency.MongoDbEventStore;
import org.freeplane.server.persistency.events.GenericEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class ClientProcessor {

	protected static final Logger logger = LoggerFactory.getLogger(ClientProcessor.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	protected MongoDbEventStore mongoDbEventStore;
	
	protected Map<WebSocketSession, ClientContext> clientContexts = new HashMap<>();

	@PostConstruct
	public void init(){
		objectMapper.registerSubtypes(new NamedType(ImmutableGenericMapUpdateRequested.class, "MapUpdateRequested"));
		
		// TODO: for testing!
		mongoDbEventStore.deleteAll();
	}
	
	public <T> T serialize(String text, Class<T> targetClass)
	{
		try {
			return objectMapper.readValue(text, targetClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String deserialize(Object object)
	{
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void sendMessage(WebSocketSession session, Object object)
	{
		try {
			session.sendMessage(new TextMessage(deserialize(object)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public MapId registerNewMap(WebSocketSession session)
	{
		if (clientContexts.containsKey(session))
		{
			throw new IllegalStateException("registerNewMap with existing session!");
		}
		ClientContext newClientContext = new ClientContext();
		clientContexts.put(session, newClientContext);
		return newClientContext.getMapId();
	}

	public void processSingleClientUpdates(WebSocketSession session, GenericUpdateBlockCompleted update)
	{
		MapId correspondingMapId = clientContexts.get(session).getMapId();
		
		int eventCounter = 1;
		for (ObjectNode json : update.updateBlock())
		{
			final String contentType = json.get("contentType").toString();
			final JsonNode nodeId = json.get("nodeId");
			
			GenericEvent genericEvent = new GenericEvent.Builder()
			 	.mapId(correspondingMapId.value())
			 	.nodeIdIf(nodeId != null, nodeId != null ? nodeId.toString() : null)	
			 	.contentType(contentType)
			 	.mapRevision(update.mapRevision())
			 	.eventIndex(eventCounter)
			 	.json(json.toString())
			 	.build();
			
			mongoDbEventStore.store(genericEvent);
			
			eventCounter++;
		}

		// distribute updates back to client
		List<MapUpdated> eventsForClient = new LinkedList<>();
		for (GenericEvent thisEvent : mongoDbEventStore.findByMapIdAndMapRevision(correspondingMapId.value(), update.mapRevision()))
		{
			eventsForClient.add(serialize(thisEvent.getJson(), MapUpdated.class));
		}
		UpdateBlockCompleted updateBlockCompletedForClient = ImmutableUpdateBlockCompleted.builder()
				.userId(ImmutableUserId.of("DUMMYUSER"))
				.mapId(correspondingMapId)
				.mapRevision(update.mapRevision() + 1L)
				.updateBlock(eventsForClient)
				.build();
		
		distributeUpdateBackToClient(session, UpdateStatus.MERGED, updateBlockCompletedForClient);
	}
	
	public void distributeUpdateBackToClient(WebSocketSession session, UpdateStatus status, UpdateBlockCompleted updateBlockCompleted)
	{
		MapUpdateProcessed mapUpdateProcessed = ImmutableMapUpdateProcessed.builder()
				.messageId(ImmutableMessageId.of("myServerMsgId"))
				.requestId(ImmutableMessageId.of("myServerMsgId4UpdateStatus"))
				.status(status)
				.build();

		sendMessage(session, mapUpdateProcessed);
		
		MapUpdateDistributed mapUpdateDistributed = ImmutableMapUpdateDistributed.builder()
				.messageId(ImmutableMessageId.of("myServerMsgId2"))
				.requestId(ImmutableMessageId.of("myMsgMapUpdateDistributed"))
				.update(updateBlockCompleted)
				.build();

		sendMessage(session, mapUpdateDistributed);
	}
}
