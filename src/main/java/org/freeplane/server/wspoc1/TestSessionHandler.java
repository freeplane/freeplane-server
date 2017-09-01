package org.freeplane.server.wspoc1;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

import org.freeplane.server.controller.ResponsePostPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

class TestSessionHandler extends StompSessionHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(TestSessionHandler.class);

//	ResponsePostPackage responsePackage;
	StompSession session;
	
//	public void setResponsePackage(ResponsePostPackage responsePackage) {
//		this.responsePackage = responsePackage;
//	}
	
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
        logger.error("handleFrame({}, {})", headers, payload);
    }

    @Override
    public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
        this.failure.set(ex);
        logger.error("handleException()", ex);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable ex) {
        this.failure.set(ex);
        logger.error("handleTransportError()", ex);
    }
    
    @Override
    public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
    	setSession(session);
        session.subscribe("/topic/post-map-test1", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
            	logger.info("getPayLoadType({})", headers);
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
            	logger.error("handleFrame/subscribe({}, {})", headers, payload);
            	/*
                try {
                	if (!(payload instanceof ResponsePostPackage)) {
                		throw new IllegalArgumentException("Wrong data type returned: " + payload.getClass().getName());
                	}
                	ResponsePostPackage responsePackage = (ResponsePostPackage) payload; 
                    setResponsePackage(responsePackage);
                    System.out.println("DEBUG: freeplane data returned from server: " + responsePackage);
                } catch (Throwable t) {
                	logger.debug("handleFrame() error", t);
                    failure.set(t);
                }
                */
            }
        });
        
    }

}