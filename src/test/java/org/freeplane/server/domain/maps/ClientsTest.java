package org.freeplane.server.domain.maps;

import java.util.Optional;

import org.freeplane.collaboration.event.MapUpdated;
import org.freeplane.collaboration.event.children.ImmutableNodeInserted;
import org.freeplane.collaboration.event.children.ImmutableNodePosition;
import org.freeplane.collaboration.event.children.Side;
import org.freeplane.collaboration.event.content.core.CoreMediaType;
import org.freeplane.collaboration.event.content.core.ImmutableCoreUpdated;
import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableMapId;
import org.freeplane.collaboration.event.messages.ImmutableUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.ImmutableUserId;
import org.freeplane.collaboration.event.messages.MapId;
import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.UserId;
import org.freeplane.server.json.JacksonConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ClientsTest {
	@InjectMocks
	private Clients clients;
	
	static private ObjectMapper objectMapper= new JacksonConfiguration().objectMapper();
	
	private GenericUpdateBlockCompleted convertToGenericUpdate(UpdateBlockCompleted update) throws Exception
	{
		final String tmpJson = objectMapper.writeValueAsString(update);
		return objectMapper.readValue(tmpJson, GenericUpdateBlockCompleted.class);
	}
	
	private GenericUpdateBlockCompleted createSimpleGenericUpdate(MapId map1)
			throws Exception
	{
		// create a short sequence of updates:
		MapUpdated update1 = ImmutableNodeInserted.builder()
				.position(ImmutableNodePosition.builder().parentId("roomongoDbEventStoretNode").position(0).side(Optional.of(Side.RIGHT)).build())
				.nodeId("ID123")
				.build();
		MapUpdated update2 = ImmutableCoreUpdated.builder()
				.content("new core content")
				.nodeId("ID123")
				.mediaType(CoreMediaType.PLAIN_TEXT)
				.build();
		UserId userId = ImmutableUserId.of("felixUser");
		UpdateBlockCompleted updateBlock1 = ImmutableUpdateBlockCompleted.builder()
				.userId(userId)
				.mapId(map1)
				.mapRevision(1L)
				.addUpdateBlock(update1, update2)
				.build();

		GenericUpdateBlockCompleted genericUpdate = convertToGenericUpdate(updateBlock1);
		return genericUpdate;
	}

	@Test
	public void testSimpleUpdate() throws Exception
	{
		Client4Test client1 = new Client4Test("client1");
		MapId map1 = ImmutableMapId.of("map1");
		clients.subscribe(map1, client1);

		GenericUpdateBlockCompleted genericUpdate = createSimpleGenericUpdate(map1);
		
		clients.sendUpdates(map1, genericUpdate);
		
		Assert.assertEquals(1, client1.getRecordedMessages().size());
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateForUnregisteredMapFails() throws Exception
	{
		MapId map1 = ImmutableMapId.of("map1");
		
		GenericUpdateBlockCompleted genericUpdate = createSimpleGenericUpdate(map1);
		
		clients.sendUpdates(map1, genericUpdate);
	}
}
