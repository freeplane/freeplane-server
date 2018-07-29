package org.freeplane.server.domain.maps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.freeplane.collaboration.event.messages.Message;
import org.freeplane.server.adapters.websockets.WebSocketClient;

public class Client4Test implements Client {

	private final String name;
	
	private List<Message> recordedMessages;
	
	public Client4Test(String name)
	{
		this.name = name;
		this.recordedMessages = new LinkedList<>();
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
	public void sendMessage(Message message) 
	{
		recordedMessages.add(message);
	}

	public List<Message> getRecordedMessages()
	{
		return new ArrayList<Message>(recordedMessages);
	}
}
