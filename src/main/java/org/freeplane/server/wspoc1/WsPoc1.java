package org.freeplane.server.wspoc1;

import org.springframework.stereotype.Component;

@Component
public class WsPoc1 {
	public static void main(String[] args) throws Exception
	{
		new PocTestClient1().run();
		Thread.sleep(5000);
		new PocTestClient2().run();

		Thread.currentThread().join();
	}
}
