package com.mgz.mediaserver.exception;

public class MediaStreamManagerException extends Exception {
	private String responseMessage;

	public MediaStreamManagerException(String msg) {
		super(msg);
		setResponseMessage(msg);
	}
	public MediaStreamManagerException(String msg, Throwable th) {
		super(msg,th);
		setResponseMessage(msg);
	}

	public String getResponseMessage() {
		return responseMessage!=null ? responseMessage : MediaStreamManagerException.class.getSimpleName();
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	private static final long serialVersionUID = 1L;

}
