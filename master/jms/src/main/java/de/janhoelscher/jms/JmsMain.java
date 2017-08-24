package de.janhoelscher.jms;

import java.io.OutputStream;
import java.io.PrintStream;

import de.janhoelscher.jms.database.media.web.MediaHttpRequestHander;
import de.janhoelscher.jms.database.users.web.UserHttpRequestHandler;
import de.janhoelscher.jms.database.users.web.login.UserLoginProvider;
import de.janhoelscher.jms.setup.Setup;
import de.janhoelscher.jms.web.server.WebServer;

public class JmsMain {

	public static void main(String[] args) throws Exception {
		System.setErr(new PrintStream(new VoidStream()));
		Setup.run();
		WebServer.create(new MainHttpHandler());
		WebServer server = WebServer.getInstance();
		server.setLoginProvider(new UserLoginProvider());
		server.registerRequestHandler("/media", new MediaHttpRequestHander());
		server.registerRequestHandler("/users", new UserHttpRequestHandler());
		server.start();
	}

	private static class VoidStream extends OutputStream {
		@Override
		public void write(int b) {
			// throw data away
		}

		@Override
		public void write(byte[] b) {
			// throw data away
		}

		@Override
		public void write(byte[] b, int off, int len) {
			// throw data away
		}
	}
}