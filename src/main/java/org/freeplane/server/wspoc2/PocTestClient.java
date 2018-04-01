package org.freeplane.server.wspoc2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.freeplane.collaboration.event.MapUpdated;
import org.freeplane.collaboration.event.batch.ImmutableMapId;
import org.freeplane.collaboration.event.batch.ImmutableUpdateBlockCompleted;
import org.freeplane.collaboration.event.batch.MapId;
import org.freeplane.collaboration.event.batch.UpdateBlockCompleted;
import org.freeplane.collaboration.event.content.other.ImmutableNodeContentUpdated;
import org.freeplane.server.json.JacksonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PocTestClient {
	private static final Logger logger = LoggerFactory.getLogger(PocTestClient.class);
	
    public static void main(String[] args) throws Exception
    {
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("ws://localhost:8080/freeplane"));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    logger.info("handleMessage({})", message);
                    final ObjectMapper jacksonObjectMapper = new JacksonConfiguration().objectMapper();
                    try
                    {
                    	final String serverUpdatesFinishedString = jacksonObjectMapper.readValue(message, UpdateBlockCompleted.class).toString();
                    	logger.info(serverUpdatesFinishedString);
                    }
                    catch (IOException ex)
                    {
                    	logger.error("Error on deserialization", ex);
                    }
                }
            });

            final ImmutableUpdateBlockCompleted updatesFinished = createClientUpdate();
            final ObjectMapper jacksonObjectMapper = new JacksonConfiguration().objectMapper();
            clientEndPoint.sendMessage(jacksonObjectMapper.writeValueAsString(updatesFinished));

            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
    
    private static ImmutableUpdateBlockCompleted createClientUpdate()
    {
    	MapUpdated mapUpdated = ImmutableNodeContentUpdated 
    			.builder()
    			.content("mynodetext")
    			.nodeId("myNodeId")
    			.build();
    	
    	ImmutableUpdateBlockCompleted updatesFinished = ImmutableUpdateBlockCompleted
    			.builder()
    			.mapId(ImmutableMapId.of("my-map-id"))
    			.mapRevision(1)
    			.addUpdateBlock(mapUpdated)
    			.build();
    	return updatesFinished;
    }
}
