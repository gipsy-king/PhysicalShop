package com.wolvereness.physicalshop.config;

import java.io.File;

import org.bukkit.util.config.Configuration;

/**
 * Parent class for config implementations
 * 
 * @author Wolfe
 * 
 */
public abstract class Config {
	public static final String directory = "plugins" + File.separator
			+ "PhysicalShop";
	private final Configuration configuration;

	/**
	 * Creates a config in the subdirectory for standard configs.
	 * 
	 * @param subDirectory
	 * @param fileName
	 */
	public Config(String subDirectory, String fileName) {
		new File(directory).mkdir();
		new File(directory + File.separator + subDirectory).mkdir();
		File file = new File(directory + File.separator + subDirectory + File.separator + fileName);
		makeFile(file);
		configuration = new Configuration(file);
		configuration.load();
		defaults();
		configuration.save();
	}

	/**
	 * Creates a config in the standard config directory
	 * 
	 * @param fileName Name of the file, or null if you wish for an inactive config
	 */
	public Config(String fileName) {
		if (fileName == null) {
			configuration = null;
			return;
		}
		new File(directory).mkdir();
		File file = new File(directory + File.separator + fileName);
		makeFile(file);
		configuration = new Configuration(file);
		configuration.load();
		defaults();
		configuration.save();
	}

	/**
	 * Allows for temporary instances of config. Should NOT be called, use standard constructor with null parameter instead.
	 */
	@Deprecated
	protected Config() {
		configuration = null;
	}

	/**
	 * Makes file f if it does not exist
	 * 
	 * @param f
	 */
	public static final boolean makeFile(File f) {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (java.io.IOException ex) {
				ex.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public abstract void defaults();

	protected final Configuration getConfig() {
		return configuration;
	}
}
