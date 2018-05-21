package org.freeplane.server.domain.maps;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapCreated;
import org.freeplane.collaboration.event.messages.ImmutableMapId;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateProcessed;
import org.freeplane.collaboration.event.messages.ImmutableMessageId;
import org.freeplane.collaboration.event.messages.MapCreated;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed.UpdateStatus;
import org.freeplane.server.adapters.mongodb.events.GenericEvent;
import org.freeplane.server.json.MessageEncoderDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class Maps {

	protected static final Logger logger = LoggerFactory.getLogger(Maps.class);
	
	private final Clients clients;
	
	private final EventStore eventStore;
	
	
	@Autowired 
	public Maps(Clients clients, EventStore eventStore) {
		super();
		this.clients = clients;
		this.eventStore = eventStore;
	}

	@Autowired
	private MessageEncoderDecoder messageEncoderDecoder;
	
	private Map<MapId, Long> mapRevisions = new ConcurrentHashMap<>();
	
	public void registerNewMap(Client client, String mapName)
	{
		MapId mapId;
		if (!mapRevisions.containsKey(ImmutableMapId.of(mapName)))
		{
			mapId = ImmutableMapId.of(mapName);
		}
		else
		{
			mapId = ImmutableMapId.of(mapName + UUID.randomUUID().toString());
		}
		mapRevisions.put(mapId, 1L);
		
		clients.subscribe(mapId, client);
		
		MapCreated mapCreated = ImmutableMapCreated.builder()
				.id(mapId)
				.messageId(ImmutableMessageId.of("1j2l3j23ljk"))
				.requestId(ImmutableMessageId.of("myServerMsgId"))
				.build();
		client.sendMessage(mapCreated);
	}
	
	public void registerExistingMap(Client client, MapId mapId)
	{
		// TODO: read latest MapRevision from database
		
		clients.subscribe(mapId, client);
	}

	public void processMapUpdates(Client client, GenericUpdateBlockCompleted update)
	{
		// send status to client:
		MapUpdateProcessed mapUpdateProcessed = ImmutableMapUpdateProcessed.builder()
				.messageId(ImmutableMessageId.of("myServerMsgId"))
				.requestId(ImmutableMessageId.of("myServerMsgId4UpdateStatus"))
				.status(UpdateStatus.ACCEPTED)
				.build();
		client.sendMessage(mapUpdateProcessed);
	
		saveUpdateInDatabase(update);
		
		// send updates to all subscribed clients:
		clients.sendUpdates(update.mapId(), update);
	}
	
	private void saveUpdateInDatabase(GenericUpdateBlockCompleted update)
	{
		int eventCounter = 1;
		for (ObjectNode json : update.updateBlock())
		{
			final String contentType = json.get("contentType").toString();
			final JsonNode nodeId = json.get("nodeId");
			
			GenericEvent genericEvent = new GenericEvent.Builder()
			 	.mapId(update.mapId().value())
			 	.nodeIdIf(nodeId != null, nodeId != null ? nodeId.toString() : null)	
			 	.contentType(contentType)
			 	.mapRevision(update.mapRevision())
			 	.eventIndex(eventCounter)
			 	.json(json.toString())
			 	.build();
			
			eventStore.store(genericEvent);
			
			eventCounter++;
		}
	}
	
	public void disconnect(Client client)
	{
		clients.disconnect(client);
	}
}
