package com.euromoby.books;

public class Config {

	private int taskPoolSize;
	private int taskQueueSize;
	private String location;
	private String extension;

	public int getTaskPoolSize() {
		return taskPoolSize;
	}

	public void setTaskPoolSize(int taskPoolSize) {
		this.taskPoolSize = taskPoolSize;
	}

	public int getTaskQueueSize() {
		return taskQueueSize;
	}

	public void setTaskQueueSize(int taskQueueSize) {
		this.taskQueueSize = taskQueueSize;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
