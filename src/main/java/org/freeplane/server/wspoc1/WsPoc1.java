package org.freeplane.server.wspoc1;

public class WsPoc1 {
	public static void main(String[] args) throws Exception
	{
		new PocTestClient().run();
		Thread.sleep(5000);
//		new PocTestClient().run();
	}
}
