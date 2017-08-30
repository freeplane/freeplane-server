package org.freeplane.server.wspoc1;

import java.util.List;

import org.freeplane.plugin.collaboration.client.event.batch.ServerUpdatesFinished;
import org.freeplane.server.controller.FreeplaneController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	private static List<PocTestClient> testClients;
	
	public static void registerTestClient(final PocTestClient testClient)
	{
		testClients.add(testClient);
	}
	
    @MessageMapping("/update-map-test1")        // client sends to server - /freeplane/update-map-test1
    @SendTo(value = "/topic/post-map-test1")  // this is what the client subscribes to for return data
    public void mapUpdateRequest(ServerUpdatesFinished requestPackage) throws Exception {
    	LOGGER.debug("TestController: Input from POST: /freeplane/update-map-test1/ = " + requestPackage);
    	
    	// ResponsePostPackage responsePackage = postMapService.processRequest(requestPackage);
    	
    	for (final PocTestClient testClient: testClients)
    	{
    		// TODO
    	}

    	return;
    }
}
