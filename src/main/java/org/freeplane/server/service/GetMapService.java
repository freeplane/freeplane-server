package org.freeplane.server.service;

import org.freeplane.server.controller.RequestGetPackage;
import org.freeplane.server.controller.ResponseGetPackage;
import org.springframework.stereotype.Component;

@Component
public class GetMapService {

	public ResponseGetPackage processRequest(RequestGetPackage requestPackage) {
		ResponseGetPackage response = new ResponseGetPackage();
    	response.setId(requestPackage.getId() + "-JOE");
    	response.setMethod(requestPackage.getMethod());
    	response.setRevision(requestPackage.getRevision());
    	
    	response.setContents("<xml>this is some cool data</xml>");
		return response;
	}
}
