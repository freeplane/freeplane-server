package org.freeplane.server.clients;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketSession4Test implements WebSocketSession {

	private final String id;
	
	public WebSocketSession4Test(final String id)
	{
		this.id = id;
	}
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpHeaders getHandshakeHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAcceptedProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTextMessageSizeLimit(int messageSizeLimit) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getTextMessageSizeLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBinaryMessageSizeLimit(int messageSizeLimit) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getBinaryMessageSizeLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<WebSocketExtension> getExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(WebSocketMessage<?> message) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(CloseStatus status) throws IOException {
		// TODO Auto-generated method stub

	}

}
