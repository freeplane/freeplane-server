package org.freeplane.server.rest;

import org.freeplane.server.rest.parameters.MapRequest;
import org.freeplane.server.rest.parameters.PostByMapIdRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.freeplane.server.rest.parameters.IdResponse;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/map")
public class FreeplaneController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FreeplaneController.class);
	
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    @ApiOperation(value = "Fetch map by ID", notes = "By specifying the map ID, an entire Freeplane map is retrieved.")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public IdResponse getMapById(@PathVariable int userId) {
    	LOGGER.debug("Input from GET: /map/{userId} = " + userId);

    	IdResponse postByMapIdResponse = new IdResponse();
    	postByMapIdResponse.setMapId(userId);
    	postByMapIdResponse.setMapRevisionNumber(456);
    	postByMapIdResponse.setMapContent("<xml>this is a map</xml>");
    	return postByMapIdResponse;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public Integer postMap(@RequestBody MapRequest mapRequest) {
    	LOGGER.debug("Input from POST: /map } = " + mapRequest);
    	Integer mapId = 0;
    	return mapId;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST)
    public int postMapById(@PathVariable PostByMapIdRequest postByMapIdRequest, @RequestBody PostByMapIdRequest mapIdRequest) {
    	LOGGER.debug("Input from POST: /map/{userId} = " + mapIdRequest);
    	
    	Integer mapRevisionNumber = 123;
    	return mapRevisionNumber;
    }
}
