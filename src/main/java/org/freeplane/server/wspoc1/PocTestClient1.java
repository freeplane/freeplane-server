package org.freeplane.server.wspoc1;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class PocTestClient1 extends PocTestClient implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(PocTestClient1.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try { Thread.sleep(new Random().nextInt(5) * 1000 + 30000); }
				catch (InterruptedException ex) { }
				logger.info("STARTING CLIENT1...");
				try
				{
					prepare();
					PocTestClient1.this.run();
				}
				catch (Throwable t)
				{
					logger.error("Testclient1 failed", t);
				}
			}
			
		}).start();
	}
}
