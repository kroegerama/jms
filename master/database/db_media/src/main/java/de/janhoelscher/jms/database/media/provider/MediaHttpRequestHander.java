package de.janhoelscher.jms.database.media.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.LogFactory;

import de.janhoelscher.jms.database.media.MediaDatabase;
import de.janhoelscher.jms.database.media.VideoFile;
import de.janhoelscher.jms.database.media.scan.ffmpeg.FFmpeg;
import de.janhoelscher.jms.tasks.Task;
import de.janhoelscher.jms.web.http.HttpRequestUri;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class MediaHttpRequestHander implements HttpRequestHandler {

	@Override
	public Response handleGetRequest(IHTTPSession session) {
		try {
			HttpRequestUri requestUri = HttpRequestUri.fromString(session.getUri());
			if (requestUri.getLastPart(1).equals("getvideo")) {
				int id = Integer.parseInt(requestUri.getLastPart());
				VideoFile file = MediaDatabase.getVideoFile(id);
				return createVideoResponse(file);
			} else if (requestUri.getLastPart(1).equals("getaudio")) {

			} else if (requestUri.getLastPart(1).equals("getimage")) {

			} else if (requestUri.getLastPart().equals("rawvideo")) {
				int id = Integer.parseInt(requestUri.getLastPart(1));
				VideoFile file = MediaDatabase.getVideoFile(id);
				return createRawVideoResponse(file, session);
			} else if (requestUri.getLastPart().equals("extractedaudio")) {
				int id = Integer.parseInt(requestUri.getLastPart(1));
				VideoFile file = MediaDatabase.getVideoFile(id);
				return createExtractedAudioResponse(file, session);
			}
		} catch (Exception e) {
			LogFactory.getLog(MediaHttpRequestHander.class).warn("Failed to handle request: " + session, e);
			return NanoHTTPD.newFixedLengthResponse("500");
		}
		return NanoHTTPD.newFixedLengthResponse("404");
	}

	@Override
	public Response handlePostRequest(IHTTPSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response handleHeadRequest(IHTTPSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response handleTraceRequest(IHTTPSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	private Response createVideoResponse(VideoFile file) throws IOException {
		File rawFile = new File(file.getFile());
		if (!rawFile.exists()) {
			return NanoHTTPD.newFixedLengthResponse("404");
		}
		String videoPlayer;
		if (file.getFile().endsWith("mkv")) {
			videoPlayer =
						IOUtils.toString(new InputStreamReader(getClass().getResourceAsStream("/de/janhoelscher/jms/media/www/videoplayer_separate_audio.html")));
			videoPlayer = videoPlayer.replace("%audiosource%", "/media/" + file.getId() + "/extractedaudio");
			videoPlayer = videoPlayer.replace("%audiotype%", "audio/webm");
		} else {
			videoPlayer =
						IOUtils.toString(new InputStreamReader(getClass().getResourceAsStream("/de/janhoelscher/jms/media/www/videoplayer.html")));
		}
		videoPlayer = videoPlayer.replace("%videosource%", "rawvideo/");
		videoPlayer = videoPlayer.replace("%videotype%", "video/webm");
		return NanoHTTPD.newFixedLengthResponse(videoPlayer);
	}

	private Response createRawVideoResponse(VideoFile file, IHTTPSession session) throws IOException {
		File rawFile = new File(file.getFile());
		return MediaFileServer.serveMediaFile(session, "video/webm", rawFile.length(), new FileInputStream(rawFile));
	}

	private Response createExtractedAudioResponse(VideoFile file, IHTTPSession session) throws IOException {
		String audio = file.getExtractedAudioFile();
		if (audio == null) {
			Task<File> task = FFmpeg.extractAudio(file.getFile());
			for (int tries = 1; tries < 4 && !task.getTaskInformation().getAdditionalInformation().exists(); tries++) {
				LogFactory.getLog(MediaHttpRequestHander.class).info("Failed to get audio-file. Retrying in 1 second. (" + (3 - tries) + " tries left.");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignored
				}
			}
			File rawFile = task.getTaskInformation().getAdditionalInformation();
			file.setExtractedAudioFile(rawFile.getAbsolutePath());
			if (task.isFinished()) {
				return MediaFileServer.serveMediaFile(session, NanoHTTPD.getMimeTypeForFile(rawFile.getAbsolutePath()), rawFile.length(), new FileInputStream(rawFile));
			} else {
				return MediaFileServer.serveMediaFile(session, NanoHTTPD.getMimeTypeForFile(rawFile.getAbsolutePath()), rawFile.length(), new CautiousFileInputStream(task));
			}
		} else {
			File rawFile = new File(audio);
			return MediaFileServer.serveMediaFile(session, NanoHTTPD.getMimeTypeForFile(audio), rawFile.length(), new FileInputStream(rawFile));
		}
	}
}