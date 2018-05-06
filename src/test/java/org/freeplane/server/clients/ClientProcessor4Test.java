package org.freeplane.server.clients;

import org.freeplane.collaboration.event.messages.UpdateBlockCompleted;
import org.freeplane.collaboration.event.messages.MapUpdateProcessed.UpdateStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ClientProcessor4Test extends ClientProcessor {

	@Override
	public void distributeUpdateBackToClient(WebSocketSession session, UpdateStatus status, UpdateBlockCompleted updateBlockCompleted)
	{
		logger.info("distributeUpdateBackToClient({}, {}, {})", session, status, updateBlockCompleted);
	}
}
