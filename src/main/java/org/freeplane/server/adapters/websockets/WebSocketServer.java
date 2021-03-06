package org.freeplane.server.adapters.websockets;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.freeplane.collaboration.event.messages.GenericUpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.MapCreateRequested;
import org.freeplane.collaboration.event.messages.Message;
import org.freeplane.server.domain.maps.Maps;
import org.freeplane.server.genericmessages.GenericMapUpdateRequested;
import org.freeplane.server.json.MessageEncoderDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketServer extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	@Autowired
	private MessageEncoderDecoder messageEncoderDecoder;

	@Autowired
	private Maps maps;
	
	@PostConstruct
	public void init(){
		WebSocketClient.setMessageEncoderDecoder(messageEncoderDecoder);
	}

	// many clients:
	// server tracks synched maprevision and mapid for all maps!
	// status=ACCEPTED: no contention
	// status=MERGED: events from different clients were reordered if client-maprevision is already used!
	//                change maprevision for some clients!
	// 1 thread per mapid for sending updates (TODO: new message on freeplane-events "MapUpdateDistributed")

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//    	logger.info("Server received text: {}", message.getPayload());
    	Message msg = messageEncoderDecoder.deserialize(message.getPayload(), Message.class);
		logger.info("Message received: {}", msg.getClass().getSimpleName());

    	if (msg instanceof MapCreateRequested)
    	{
    		logger.info("MapCreateRequested received!");
    		maps.registerNewMap(new WebSocketClient(session), "initName");
    	}
    	else if (msg instanceof GenericMapUpdateRequested)
    	{
    		logger.info("MapUpdateRequested received!");
    		GenericMapUpdateRequested msgMapUpdateRequested = (GenericMapUpdateRequested)msg;
    		GenericUpdateBlockCompleted updateBlockCompleted = msgMapUpdateRequested.update();

    		maps.processMapUpdates(new WebSocketClient(session), updateBlockCompleted);
    	}
    }

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	}

}
