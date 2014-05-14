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
	
	public Properties(String filePath) {
		this.properties = new java.util.Properties();
		this.propertiesFile = new File(filePath);
		try (FileInputStream inputStream = new FileInputStream(this.propertiesFile)) {
			this.properties.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Properties(File file) {
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
	
	public Boolean exists() {
		return this.propertiesFile.exists();
	}
	
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	public Set<String> getPropertyNames() {
		return this.properties.stringPropertyNames();
	}
	
	public void setProperty(String key, Long value) {
		this.properties.setProperty(key, Long.toString(value));
	}
	
	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}
	
	public void removeProperty(Object key) {
		this.properties.remove(key);
	}

	public Set<Object> getKeySet() {
		return this.properties.keySet();
	}
	
	public void save(String comment) {
		try (FileOutputStream outputStream = new FileOutputStream(this.propertiesFile)) {
			this.properties.store(outputStream, comment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(String filePath, String comment) {
		try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
			if (this.propertiesFile == null) this.propertiesFile = new File(filePath);
			this.properties.store(outputStream, comment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		save(null);
	}
	
	public void delete() {
		if (!this.propertiesFile.delete()) {
			EvilBook.logSevere("Failed to delete " + this.propertiesFile.getAbsolutePath());
		}
	}
}
