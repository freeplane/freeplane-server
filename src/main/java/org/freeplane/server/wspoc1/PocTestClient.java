package org.freeplane.server.wspoc1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.freeplane.plugin.collaboration.client.event.MapUpdated;
import org.freeplane.plugin.collaboration.client.event.batch.ImmutableUpdatesFinished;
import org.freeplane.plugin.collaboration.client.event.batch.UpdatesFinished;
import org.freeplane.plugin.collaboration.client.event.children.ImmutableChildrenUpdated;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class PocTestClient {
    private int port = 8080;
    private SockJsClient sockJsClient;
    private WebSocketStompClient stompClient;
    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private AtomicReference<Throwable> failure = new AtomicReference<>();
    private TestSessionHandler handler;

    public PocTestClient() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        
        handler = new TestSessionHandler(failure);
    }

    public void run() throws Exception
    {
        stompClient.connect("ws://localhost:8080/freeplane", headers, handler, port).get();

        List<String> content = Arrays.asList("one", "two", "three");
    	
    	MapUpdated mapUpdated = ImmutableChildrenUpdated  // ImmutableGenericNodeUpdated
    			.builder()
    			.content(content)
    			.nodeId("myNodeId")
    			.build();
    	
    	UpdatesFinished updatesFinished = ImmutableUpdatesFinished
    			.builder()
    			.mapId("my-map-id")
    			.mapRevision(1)
    			.addUpdateEvents(mapUpdated)
    			.build();
    	final StompSession session = handler.getSession();
		session.send("/freeplane/update-map-test1", updatesFinished);
    }
}
