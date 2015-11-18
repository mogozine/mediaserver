package com.mgz.mediaserver;

import java.io.IOException;
import java.net.InetAddress;

public interface IMediaServer {
	public void setMediaStreamParams(MediaStreamParameters params);
	public void setIsMediaProducer(boolean isMediaProducer);
	public boolean isMediaProducer();
	
	public void init() throws IOException;
	
	public void start();
	public boolean isRunning();
	public void stop();
	public void join(long milliseconds) throws InterruptedException;
	
	public void addAcceptableHostAddress(InetAddress hostAddress);

	public void removeAcceptableHostAddress(InetAddress hostAddress);
	
}
