package org.freeplane.server.wspoc2;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PocTestClient {
	private static final Logger logger = LoggerFactory.getLogger(PocTestClient.class);
	
    public static void main(String[] args)
    {
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("ws://localhost:8080/freeplane"));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    logger.info(message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
