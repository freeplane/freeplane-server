package org.freeplane.server.domain.maps;

import org.freeplane.collaboration.event.messages.Message;

public interface Client {

	public void sendMessage(Message message);
}
