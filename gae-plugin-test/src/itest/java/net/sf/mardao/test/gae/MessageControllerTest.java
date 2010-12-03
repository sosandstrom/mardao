package net.sf.mardao.test.gae;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

public class MessageControllerTest extends TestCase {

	public void testGetForm() throws IOException {
		URL url = new URL("http://localhost:8282/message");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		assertEquals(200, con.getResponseCode());
	}

}
