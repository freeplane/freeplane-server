package org.freeplane.server.domain.maps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import org.apache.commons.lang3.tuple.Pair;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapUpdateDistributed;
import org.freeplane.collaboration.event.messages.ImmutableMessageId;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.MapUpdateDistributed;
import org.freeplane.collaboration.event.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Clients {
	protected static final Logger logger = LoggerFactory.getLogger(Clients.class);
	
	private Map<MapId, List<Client>> mapUpdateSubscribers  = new HashMap<>();
	private SynchronousQueue<Pair<Client, Message>> mapUpdates = new SynchronousQueue<>();
	
	public void subscribe(MapId mapId, Client client)
	{
		if (!mapUpdateSubscribers.containsKey(mapId))
		{
			mapUpdateSubscribers.put(mapId, new LinkedList<>());
		}
		mapUpdateSubscribers.get(mapId).add(client);
	}
	
	public void unsubscribe(MapId mapId, Client client)
	{
		if (mapUpdateSubscribers.containsKey(mapId))
		{
			mapUpdateSubscribers.get(mapId).remove(client);
		}
	}
	
	public void disconnect(Client client)
	{
		for (Map.Entry<MapId, List<Client>> entries : mapUpdateSubscribers.entrySet())
		{
			entries.getValue().remove(client);
		}
	}
	
	public void sendUpdates(MapId mapId, GenericUpdateBlockCompleted update)
	{
		if (mapUpdateSubscribers.containsKey(mapId))
		{
			MapUpdateDistributed mapUpdateDistributed = ImmutableMapUpdateDistributed.builder()
					.messageId(ImmutableMessageId.of("myServerMsgId2"))
					.requestId(ImmutableMessageId.of("myMsgMapUpdateDistributed"))
					.update(update)
					.build();

			for (Client client : mapUpdateSubscribers.get(mapId))
			{
				client.sendMessage(mapUpdateDistributed);
			}
		}
	}

}
