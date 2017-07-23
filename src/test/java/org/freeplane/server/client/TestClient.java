package org.freeplane.server.client;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import org.freeplane.server.controller.RequestPackage;
import org.freeplane.server.controller.ResponsePackage;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;


public class TestClient {

    private int port = 8080;
    private SockJsClient sockJsClient;
    private WebSocketStompClient stompClient;
    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public static void main(String[] args) throws InterruptedException, AssertionError {
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        
        TestClient testClient = new TestClient();
        testClient.setup();
        
        TestSessionHandler handler = testClient.new TestSessionHandler(failure) {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
            	setSession(session);
                session.subscribe("/topic/map", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ResponsePackage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        try {
                        	if (!(payload instanceof ResponsePackage)) {
                        		throw new IllegalArgumentException("Wrong data type returned: " + payload.getClass().getName());
                        	}
                        	ResponsePackage responsePackage = (ResponsePackage) payload; 
                            setResponsePackage(responsePackage);
                            System.out.println("DEBUG: freeplane data returned from server: " + responsePackage);
                        } catch (Throwable t) {
                            failure.set(t);
                        }
                    }
                });
                
            }
        };

        testClient.stompClient.connect("ws://localhost:8080/freeplane", testClient.headers, handler, testClient.port);
    	String msg;
    	int cntr = 0;
    	RequestPackage requestPackage;
    	Scanner input = null;
        try {
        	input = new Scanner(System.in);
            while (true) {
            	System.out.println("Enter data to be sent to server: ");
            	msg = input.nextLine();
            	requestPackage = new RequestPackage();
            	requestPackage.setId("" + ++cntr);
            	requestPackage.setRevision("1.0");
            	requestPackage.setMethod("get-updates");
            	requestPackage.setContents(msg);
	            handler.getSession().send("/freeplane/map", requestPackage);
            }
        } catch (Exception t) {
        	t.printStackTrace();
            failure.set(t);
        }
        finally {
        	input.close();
        }
    }

    private class TestSessionHandler extends StompSessionHandlerAdapter {
    	ResponsePackage responsePackage;
    	StompSession session;

    	public void setResponsePackage(ResponsePackage responsePackage) {
    		this.responsePackage = responsePackage;
    	}
    	
    	public StompSession getSession() {
    		return session;
    	}
    	public void setSession(StompSession session) {
    		this.session = session;
    	}
    	
        private final AtomicReference<Throwable> failure;

        public TestSessionHandler(AtomicReference<Throwable> failure) {
            this.failure = failure;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex);
        }
    }
}
