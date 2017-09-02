package org.freeplane.server.wspoc2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.freeplane.plugin.collaboration.client.event.MapUpdated;
import org.freeplane.plugin.collaboration.client.event.batch.ImmutableUpdatesFinished;
import org.freeplane.plugin.collaboration.client.event.batch.ServerUpdatesFinished;
import org.freeplane.plugin.collaboration.client.event.batch.UpdatesFinished;
import org.freeplane.plugin.collaboration.client.event.children.ImmutableChildrenUpdated;
import org.freeplane.server.json.JacksonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

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
                    	final String serverUpdatesFinishedString = jacksonObjectMapper.readValue(message, ServerUpdatesFinished.class).toString();
                    	logger.info(serverUpdatesFinishedString);
                    }
                    catch (IOException ex)
                    {
                    	logger.error("Error on deserialization", ex);
                    }
                }
            });

            final UpdatesFinished updatesFinished = createClientUpdate();
            final ObjectMapper jacksonObjectMapper = new JacksonConfiguration().objectMapper();
            clientEndPoint.sendMessage(jacksonObjectMapper.writeValueAsString(updatesFinished));

            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
    
    private static UpdatesFinished createClientUpdate()
    {
        List<String> content = Arrays.asList("one", "two", "three");
    	
    	MapUpdated mapUpdated = ImmutableChildrenUpdated  // ImmutableGenericNodeUpdated
    			.builder()
    			.content(content)
    			.nodeId("myNodeId")
    			.build();
    	
    	UpdatesFinished updatesFinished = ImmutableUpdatesFinished
    			.builder()
    			.mapId("my-map-id")
    			.mapRevision(1)
    			.addUpdateEvents(mapUpdated)
    			.build();
    	return updatesFinished;
    }
}
