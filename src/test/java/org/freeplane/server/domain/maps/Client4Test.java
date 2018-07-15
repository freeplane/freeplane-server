package org.freeplane.server.domain.maps;

import org.freeplane.collaboration.event.messages.Message;
import org.freeplane.server.adapters.websockets.WebSocketClient;

public class Client4Test implements Client {

	private final String name;
	
	public Client4Test(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof WebSocketClient))
		{
			return false;
		}
		return name.equals(((Client4Test)other).name);
	}
	
	@Override
	public void sendMessage(Message message) {
	}

}
