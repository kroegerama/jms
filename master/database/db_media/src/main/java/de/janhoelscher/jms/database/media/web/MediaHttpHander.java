package de.janhoelscher.jms.database.media.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import de.janhoelscher.jms.database.media.AudioFile;
import de.janhoelscher.jms.database.media.MediaDatabase;
import de.janhoelscher.jms.database.media.VideoFile;
import de.janhoelscher.jms.database.media.scan.ffmpeg.AudioStreamExtractor;
import de.janhoelscher.jms.logging.Logger;
import de.janhoelscher.jms.tasks.Task;
import de.janhoelscher.jms.web.http.HttpRequestUri;
import de.janhoelscher.jms.web.http.Request;
import de.janhoelscher.jms.web.server.HttpRequestHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class MediaHttpHander implements HttpRequestHandler {

	@Override
	public Response handleHttpRequest(Request request) throws Exception {
		try {
			HttpRequestUri requestUri = HttpRequestUri.fromString(request.getUri());
			if (requestUri.getLastPart(1).equals("getvideo")) {
				int id = Integer.parseInt(requestUri.getLastPart());
				VideoFile file = MediaDatabase.getVideoFile(id);
				return createVideoResponse(file);
			} else if (requestUri.getLastPart(1).equals("getaudio")) {

			} else if (requestUri.getLastPart(1).equals("getimage")) {

			} else if (requestUri.getLastPart().equals("rawvideo")) {
				int id = Integer.parseInt(requestUri.getLastPart(1));
				VideoFile file = MediaDatabase.getVideoFile(id);
				return createRawVideoResponse(file, request);
			} else if (requestUri.getLastPart().equals("extractedaudio")) {
				int id = Integer.parseInt(requestUri.getLastPart(1));
				VideoFile file = MediaDatabase.getVideoFile(id);
				return createExtractedAudioResponse(file, request);
			}
		} catch (Exception e) {
			Logger.warn("Failed to handle request: " + request, e);
			//LogFactory.getLog(MediaHttpRequestHander.class).warn("Failed to handle request: " + session, e);
			return NanoHTTPD.newFixedLengthResponse("500");
		}
		return NanoHTTPD.newFixedLengthResponse("404");
	}

	private Response createVideoResponse(VideoFile file) throws IOException {
		File rawFile = new File(file.getPath());
		if (!rawFile.exists()) {
			return NanoHTTPD.newFixedLengthResponse("404");
		}
		String videoPlayer;
		if (FilenameUtils.getExtension(file.getPath()).endsWith("mkv")) {
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

	private Response createRawVideoResponse(VideoFile file, Request request) throws IOException {
		File rawFile = new File(file.getPath());
		return MediaFileServer.serveMediaFile(request, "video/mp4", rawFile.length(), new FileInputStream(rawFile));
	}

	private Response createExtractedAudioResponse(VideoFile file, Request request) throws IOException {
		AudioFile audio = file.getExtractedAudioFile();
		if (audio == null) {
			Task<AudioFile> task = AudioStreamExtractor.extractAudio(file);
			for (int tries = 1; tries < 4 && !task.getTaskInformation().getAdditionalInformation().exists(); tries++) {
				Logger.info("Failed to get audio-file. Retrying in 1 second. (" + (3 - tries) + " tries left.");
				//LogFactory.getLog(MediaHttpRequestHander.class).info("Failed to get audio-file. Retrying in 1 second. (" + (3 - tries) + " tries left.");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignored
				}
			}
			audio = task.getTaskInformation().getAdditionalInformation();
			file.setExtractedAudioFile(audio);
			if (task.isFinished()) {
				return MediaFileServer.serveMediaFile(request, NanoHTTPD.getMimeTypeForFile(audio.getPath()), audio.getSize(), new FileInputStream(audio));
			} else {
				return MediaFileServer.serveMediaFile(request, NanoHTTPD.getMimeTypeForFile(audio.getPath()), audio.getSize(), new CautiousFileInputStream(task));
			}
		} else {
			File rawFile = new File(audio.getPath());
			return MediaFileServer.serveMediaFile(request, NanoHTTPD.getMimeTypeForFile(audio.getPath()), rawFile.length(), new FileInputStream(rawFile));
		}
	}

	@Override
	public boolean isLoginNeeded(Request request) {
		return true;
	}
}