package com.mgz.mediaserver.exception;

public class StreamStateException extends Exception {
	private static final long serialVersionUID = 1L;

	Object offendingObject;

	public StreamStateException(String msg) {
		super(msg);
	}

	public Object getOffendingObject() {
		return offendingObject;
	}

	public void setOffendingObject(Object offendingObject) {
		this.offendingObject = offendingObject;
	}
	
	public String getMessage(){
		return super.getMessage() + "\n Offending object: " + offendingObject;
	}
}
