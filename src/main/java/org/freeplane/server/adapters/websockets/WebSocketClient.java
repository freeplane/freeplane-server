package org.freeplane.server.adapters.websockets;

import java.io.IOException;

import org.freeplane.collaboration.event.messages.Message;
import org.freeplane.server.domain.maps.Client;
import org.freeplane.server.json.MessageEncoderDecoder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketClient implements Client {
	
	private final WebSocketSession webSocketSession;
	private static MessageEncoderDecoder messageEncoderDecoder;
	
	public WebSocketClient(WebSocketSession webSocketSession) {
		this.webSocketSession = webSocketSession;
	}
	
	public static void setMessageEncoderDecoder(MessageEncoderDecoder myMessageEncoderDecoder)
	{
		messageEncoderDecoder = myMessageEncoderDecoder;
	}
	
	@Override
	public synchronized void sendMessage(Message message) {
		try {
			webSocketSession.sendMessage(new TextMessage(messageEncoderDecoder.serialize(message)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof WebSocketClient))
		{
			return false;
		}
		return webSocketSession.getId().equals(((WebSocketClient)other).webSocketSession.getId());
	}
}
