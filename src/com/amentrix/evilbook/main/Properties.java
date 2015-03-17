package com.amentrix.evilbook.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;

/**
 * java.util.Properties framework
 * @author Reece Aaron Lecrivain
 */
public class Properties {
	private java.util.Properties properties;
	private File propertiesFile;
	
	Properties(String filePath) {
		this.properties = new java.util.Properties();
		this.propertiesFile = new File(filePath);
		try (FileInputStream inputStream = new FileInputStream(this.propertiesFile)) {
			this.properties.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Properties(File file) {
		this.properties = new java.util.Properties();
		this.propertiesFile = file;
		try (FileInputStream inputStream = new FileInputStream(this.propertiesFile)) {
			this.properties.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Properties() {
		this.properties = new java.util.Properties();
	}
	
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	public Set<String> getPropertyNames() {
		return this.properties.stringPropertyNames();
	}
	
	void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

	public Set<Object> getKeySet() {
		return this.properties.keySet();
	}
	
	private void save(String comment) {
		try (FileOutputStream outputStream = new FileOutputStream(this.propertiesFile)) {
			this.properties.store(outputStream, comment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void save(String filePath, String comment) {
		try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
			if (this.propertiesFile == null) this.propertiesFile = new File(filePath);
			this.properties.store(outputStream, comment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void save() {
		save(null);
	}
}
