package de.janhoelscher.jms.web.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import de.janhoelscher.jms.config.Config;

public class RequestMetadata {

	private static Thread							metadataCleanupThread;

	private static HashMap<String, Long>			metadataUsage;

	private static HashMap<String, RequestMetadata>	metadataRegistry;

	public static RequestMetadata getMetadata(String clientId) {
		if (RequestMetadata.metadataRegistry == null) {
			RequestMetadata.metadataRegistry = new HashMap<>();
		}
		RequestMetadata metadata = RequestMetadata.metadataRegistry.get(clientId);
		if (metadata == null) {
			metadata = new RequestMetadata(clientId);
			RequestMetadata.metadataRegistry.put(clientId, metadata);
		}
		RequestMetadata.updateUsage(metadata);
		return metadata;
	}

	private static void updateUsage(RequestMetadata metadata) {
		if (RequestMetadata.metadataUsage == null) {
			RequestMetadata.metadataUsage = new HashMap<>();
		}
		RequestMetadata.metadataUsage.put(metadata.getClientId(), System.currentTimeMillis());
		if (RequestMetadata.metadataCleanupThread == null) {
			RequestMetadata.metadataCleanupThread = new Thread(() -> RequestMetadata.cleanupMetadata());
			RequestMetadata.metadataCleanupThread.start();
		}
	}

	private static void cleanupMetadata() {
		List<String> clientsToCleanup = new LinkedList<>();
		while (RequestMetadata.metadataCleanupThread != null) {
			for (Entry<String, Long> entry : RequestMetadata.metadataUsage.entrySet()) {
				if (entry.getValue() < System.currentTimeMillis() - Config.getInstance().Web.SessionTimeout) {
					clientsToCleanup.add(entry.getKey());
				}
			}
			for (String str : clientsToCleanup) {
				RequestMetadata.metadataRegistry.remove(str);
				RequestMetadata.metadataUsage.remove(str);
			}
			clientsToCleanup.clear();
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// ignored
			}
		}
	}

	private final String			clientId;

	private HashMap<String, Object>	clientMetadata;

	private RequestMetadata(String clientId) {
		this.clientId = clientId;
		this.clientMetadata = new HashMap<>();
	}

	public String getClientId() {
		return clientId;
	}

	public HashMap<String, Object> getClientMetadata() {
		return clientMetadata;
	}
}