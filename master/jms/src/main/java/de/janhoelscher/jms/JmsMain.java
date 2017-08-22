package de.janhoelscher.jms;

import de.janhoelscher.jms.database.media.provider.MediaHttpRequestHander;
import de.janhoelscher.jms.web.server.WebServer;

public class JmsMain {

	public static void main(String[] args) throws Exception {
		WebServer.getInstance().start();
		WebServer.getInstance().registerRequestHandler("/media", new MediaHttpRequestHander());
	}
}
