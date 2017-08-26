package org.freeplane.server.client;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.freeplane.server.controller.ResponsePostPackage;
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


public class ProductionTestClient {

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
        
        ProductionTestClient testClient = new ProductionTestClient();
        testClient.setup();
        
        TestSessionHandler handler = testClient.new TestSessionHandler(failure) {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
            	setSession(session);
                session.subscribe("/topic/post-map", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ResponsePostPackage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        try {
                        	if (!(payload instanceof ResponsePostPackage)) {
                        		throw new IllegalArgumentException("Wrong data type returned: " + payload.getClass().getName());
                        	}
                        	ResponsePostPackage responsePackage = (ResponsePostPackage) payload; 
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

        List<String> content = Arrays.asList("one", "two", "three");
    	
//    	MapUpdated mapUpdated = ImmutableChildrenUpdated  // ImmutableGenericNodeUpdated
//    			.builder()
//    			.content(content)
//    			.nodeId("myNodeId")
//    			.build();
//    	
//    	UpdatesFinished updatesFinished = ImmutableUpdatesFinished
//    			.builder()
//    			.mapId("my-map-id")
//    			.mapRevision(1)
//    			.addUpdateEvents(mapUpdated)
//    			.build()
//    			;
//    	handler.getSession().send("/freeplane/update-map1", updatesFinished);
    }
    
    private class TestSessionHandler extends StompSessionHandlerAdapter {
    	ResponsePostPackage responsePackage;
    	StompSession session;

    	public void setResponsePackage(ResponsePostPackage responsePackage) {
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
