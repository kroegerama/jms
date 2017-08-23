package de.janhoelscher.jms;

import de.janhoelscher.jms.database.media.web.MediaHttpRequestHander;
import de.janhoelscher.jms.database.users.web.UsersHttpRequestHandler;
import de.janhoelscher.jms.setup.Setup;
import de.janhoelscher.jms.web.server.WebServer;

public class JmsMain {

	public static void main(String[] args) throws Exception {
		Setup.run();
		WebServer.getInstance().start();
		WebServer.getInstance().registerRequestHandler("/media", new MediaHttpRequestHander());
		WebServer.getInstance().registerRequestHandler("/users", new UsersHttpRequestHandler());
	}
}
