package com.mgz.mediaserver;

import java.util.concurrent.TimeoutException;

import com.mgz.mediaserver.exception.StreamStateException;

public interface IMediaStream {

	public void addStreamConsumer(IStreamConsumer streamConsumer);
	public void removeStreamConsumer(IStreamConsumer streamConsumer);
	public void setStreamProducer(IStreamProducer streamProducer) throws StreamStateException;
	public IStreamProducer getStreamProducer();
	
	public String getName();
	public boolean isRunning();

	public void start();
	public void stop();
	public void join(long timeout) throws InterruptedException, TimeoutException;
	
	public String getMimeType();

}
