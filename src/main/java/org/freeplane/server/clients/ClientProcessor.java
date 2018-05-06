package org.freeplane.server.clients;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.freeplane.collaboration.event.MapUpdated;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableUserId;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed.UpdateStatus;
import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.server.genericmessages.ImmutableGenericMapUpdateRequested;
import org.freeplane.server.persistency.MongoDbEventStore;
import org.freeplane.server.persistency.events.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class ClientProcessor {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MongoDbEventStore mongoDbEventStore;
	
	private Map<WebSocketSession, ClientContext> clientContexts = new HashMap<>();

	@PostConstruct
	public void init(){
		objectMapper.registerSubtypes(new NamedType(ImmutableGenericMapUpdateRequested.class, "MapUpdateRequested"));
		
		// TODO: for testing!
		mongoDbEventStore.deleteAll();
	}
	
	private <T> T serialize(String text, Class<T> targetClass)
	{
		try {
			return objectMapper.readValue(text, targetClass);
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

	public Pair<UpdateStatus, UpdateBlockCompleted> processSingleClientUpdates(WebSocketSession session, GenericUpdateBlockCompleted update)
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
		
		return Pair.of(UpdateStatus.MERGED, updateBlockCompletedForClient);
	}
}
