package org.freeplane.server.service;

import org.freeplane.server.controller.RequestPostPackage;
import org.freeplane.server.controller.ResponsePostPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PostMapService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PostMapService.class);

	public ResponsePostPackage processRequest(RequestPostPackage requestPackage) {
		LOGGER.debug("Content = " + requestPackage.getContents());
		
		ResponsePostPackage response = new ResponsePostPackage();
		
    	response.setId(requestPackage.getId() + "-FOOBAR");
    	response.setMethod(requestPackage.getMethod());
    	response.setRevision(requestPackage.getRevision());
    	
		return response;
		
	}
}
