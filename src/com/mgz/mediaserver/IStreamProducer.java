package com.mgz.mediaserver;

import java.io.Closeable;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public interface IStreamProducer {
	BlockingQueue<IMediaChunk> getOutputQueue();
	public void setInputStream(InputStream is);
	public void setCloseable(Closeable closeable);
	
	public void start();
	boolean isRunning();
	public void stop();
	public void join(long l) throws InterruptedException, TimeoutException;
	
	String getName();
	long getProcessedBytes();
}
