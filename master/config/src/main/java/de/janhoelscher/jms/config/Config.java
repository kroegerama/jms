package de.janhoelscher.jms.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Config {

	private static Config instance;

	public static Config getInstance() {
		if (instance == null) {
			instance = ConfigLoader.loadConfig();
		}
		return instance;
	}

	public static void save() {
		ConfigLoader.saveConfig(getInstance());
	}

	public static String getAbsolutePath() {
		return ConfigLoader.getAbsoluteConfigPath();
	}

	@XmlElement(required = true)
	public MediaLibrary	MediaLibrary	= new MediaLibrary();

	@XmlElement(required = true)
	public Database		Database		= new Database();

	protected Config() {

	}

	@XmlRootElement
	public static class MediaLibrary {

		@XmlElement(required = true)
		public boolean	AutoConvertLargeFileTypes	= false;

		@XmlElement(required = true)
		public FFmpeg	FFmpeg						= new FFmpeg();

		@XmlRootElement
		public static class FFmpeg {

			@XmlElement(required = true)
			public String	Path									= "";

			@XmlElement(required = true)
			public String	ExtractedAudioCodec						= "mp3";

			@XmlElement(required = true)
			public String	ExtractedAudioFileExtension				= "mp3";

			@XmlElement(required = true)
			public boolean	StoreExtractedAudioInSameFolderAsVideo	= false;

			@XmlElement(required = true)
			public String	ExtractedAudioLocation					= "media/extracted_audio/";
		}
	}

	@XmlRootElement
	public static class Database {

		@XmlElement(required = true)
		public String Name = "database";
	}
}