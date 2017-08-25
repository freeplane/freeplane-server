package org.freeplane.server.controller;

import org.freeplane.plugin.collaboration.client.event.batch.UpdatesFinished;
import org.freeplane.server.service.GetMapService;
import org.freeplane.server.service.PostMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/*
 * These are the methods that are currently supported by FreeplaneController:
 * 
 * get-id
 * get-id-version
 * post-name
 * post-id
 * 
 */
@Controller
public class FreeplaneController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FreeplaneController.class);
	
	@Autowired
	GetMapService getMapService;
	
	@Autowired
	PostMapService postMapService;
	
    @MessageMapping("/get-map")        // client sends to server - /freeplane/get-map
    @SendTo(value = "/topic/get-map")  // this is what the client subscribes to for return data
    public ResponseGetPackage mapRequest(RequestGetPackage requestPackage) throws Exception {
    	LOGGER.debug("Input from GET: /freeplane/get-map/ = " + requestPackage);
    	
    	ResponseGetPackage responsePackage = getMapService.processRequest(requestPackage);

    	return responsePackage;
    }
    
    @MessageMapping("/post-map")        // client sends to server - /freeplane/post-map
    @SendTo(value = "/topic/post-map")  // this is what the client subscribes to for return data
    public ResponsePostPackage mapRequest(RequestPostPackage requestPackage) throws Exception {
    	LOGGER.debug("Input from POST: /freeplane/post-map/ = " + requestPackage);
    	
    	ResponsePostPackage responsePackage = postMapService.processRequest(requestPackage);

    	return responsePackage;
    }
    
    @MessageMapping("/update-map1")        // client sends to server - /freeplane/post-map
    @SendTo(value = "/topic/post-map")  // this is what the client subscribes to for return data
    public void mapUpdateRequest(UpdatesFinished requestPackage) throws Exception {
    	LOGGER.debug("Input from POST: /freeplane/post-map/ = " + requestPackage);
    	
    	// ResponsePostPackage responsePackage = postMapService.processRequest(requestPackage);

    	return;
    }
    
}
