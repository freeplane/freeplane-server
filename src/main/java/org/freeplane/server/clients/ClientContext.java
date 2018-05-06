package org.freeplane.server.clients;

import java.util.UUID;

import org.freeplane.collaboration.event.messages.ImmutableMapId;
import org.freeplane.collaboration.event.messages.MapId;

public class ClientContext {

	// current only one map per client
	private final MapId mapId;
	
	// client registers new map
	public ClientContext()
	{
		mapId = generateMapId();
	}
	
	public ClientContext(MapId mapId)
	{
		this.mapId = mapId;
	}
	
	public MapId getMapId()
	{
		return mapId;
	}
	
	private MapId generateMapId() 
	{
		return ImmutableMapId.of(UUID.randomUUID().toString());
	}
}
