package de.janhoelscher.jms.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.LogFactory;

abstract class ConfigLoader {

	private static final String CONFIG_NAME = "config.xml";

	public static Config loadConfig() {
		try (FileReader reader = new FileReader(CONFIG_NAME)) {
			Unmarshaller u = JAXBContext.newInstance(Config.class).createUnmarshaller();
			return (Config) u.unmarshal(reader);
		} catch (FileNotFoundException e) {
			Config res = new Config();
			saveConfig(res);
			return res;
		} catch (Exception e) {
			LogFactory.getLog(ConfigLoader.class).warn("Failed to load config!", e);
			return null;
		}
	}

	public static void saveConfig(Config config) {
		try {
			File cfgFile = new File(CONFIG_NAME);
			Marshaller m = JAXBContext.newInstance(Config.class).createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(config, cfgFile);
		} catch (Exception e) {
			LogFactory.getLog(ConfigLoader.class).warn("Failed to save config!", e);
		}
	}
}