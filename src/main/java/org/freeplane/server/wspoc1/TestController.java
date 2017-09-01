package org.freeplane.server.wspoc1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.freeplane.plugin.collaboration.client.event.batch.ServerUpdatesFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
	
	private SimpMessagingTemplate template;

    private CountDownLatch clientCountDownLatch;

    @Autowired
    public TestController(SimpMessagingTemplate template) {
        this.template = template;
        clientCountDownLatch = new CountDownLatch(2);
    }
    
	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	private static List<PocTestClient> testClients = new ArrayList<>();
	
	public static void registerTestClient(final PocTestClient testClient)
	{
		testClients.add(testClient);
	}
	
    @MessageMapping("/update-map-test1")        // client sends to server - /freeplane/update-map-test1
    @SendTo(value = "/topic/post-map-test1")  // this is what the client subscribes to for return data
    public @ResponseBody String mapUpdateRequest(ServerUpdatesFinished requestPackage) throws Exception {
    	LOGGER.debug("{}: Input from POST: /freeplane/update-map-test1/ = {}", this, requestPackage);
    	
    	// ResponsePostPackage responsePackage = postMapService.processRequest(requestPackage);
//    	for (final PocTestClient testClient: testClients)
//    	{
//    		LOGGER.info("handling {}", testClient);
//    	}
    	clientCountDownLatch.countDown();
    	
    	if (clientCountDownLatch.getCount() == 0)
    	{
    		LOGGER.info("Responding to all clients...");
    		greetClients("hello world!");
    		LOGGER.info("Responding to all clients... DONE");
    	}

    	return "{\"key\": \"value\"}";
    }

    @RequestMapping(path="/post-map-test1", method=RequestMethod.POST)
    public void greetClients(String greeting) {
        String text = "[FROM SERVER]: " + greeting;
        this.template.convertAndSend("/topic/post-map-test1", text);
    }

}
