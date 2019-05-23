package com.github.camque.queue.commons.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParameterUtils {
	
	private static final Logger LOG = LogManager.getLogger(ParameterUtils.class);
	
	private static Properties parameters;
	
	/**
	 * Get a value parameter
	 * @param key
	 * @return
	 */
	public static String getParam(String key) {
		if ( parameters == null ) {
			loadParameters();
		}
		
		return parameters.getProperty(key);
		
	}

	/**
	 * Load parameters file
	 */
	private static void loadParameters() {
		parameters = new Properties();
		
		String pathFile = System.getProperty("jboss.server.config.dir") + "/" + "queue.properties";
		LOG.info(pathFile);
		
		try (BufferedReader bufferedReader = Files.newBufferedReader( Paths.get(pathFile) ) ) {
			parameters.load(bufferedReader);
        } catch (IOException e) {
        	LOG.info("Error al leer archivo de propiedades.");
			LOG.error("", e);
		}
	}
	
	/**
	 * Load parameters file
	 */
	private static void loadParametersClassLoader() {
		InputStream inputStream = null;
		try {
			parameters = new Properties();
			inputStream = ParameterUtils.class.getClassLoader().getResourceAsStream("queue.properties");
			parameters.load(inputStream);
		} catch (final FileNotFoundException ex) {
			LOG.info("No existe el archivo de propiedades.");
			LOG.error("", ex);
		} catch (final IOException ex) {
			LOG.info("Error al leer archivo de propiedades.");
			LOG.error("", ex);
		} finally {
			try {
				if ( inputStream != null ) {
					inputStream.close();
				}
			} catch (final IOException ex) {
				LOG.info("Error al cerrar el archivo.");
				LOG.error("", ex);
			}
		}
	}

}
