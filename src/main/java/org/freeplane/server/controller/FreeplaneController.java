package org.freeplane.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class FreeplaneController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FreeplaneController.class);
	
	
    @MessageMapping("/map")        // client sends to server - /freeplane/map
    @SendTo(value = "/topic/map")  // this is what the client subscribes to for return data
    public ResponsePackage mapRequest(RequestPackage requestPackage) throws Exception {
    	LOGGER.debug("Input from GET: /freeplane/map/ = " + requestPackage);
    	
    	ResponsePackage responsePackage = new ResponsePackage();
    	responsePackage.setId(requestPackage.getId() + "-JOE");
    	responsePackage.setMethod(requestPackage.getMethod());
    	responsePackage.setRevision(requestPackage.getRevision());
    	
    	// based on the 'method' string received, execute the appropriate service and return
    	
    	return responsePackage;
    }
    
}
