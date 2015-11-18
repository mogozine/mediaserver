package com.mgz.mediaserver;

import java.io.Closeable;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public interface IStreamConsumer {
	
	/**
	 * Provides the input queue, {@link BlockingQueue}, of this {@link IStreamConsumer}. 
	 * @return {@link BlockingQueue}, of this {@link IStreamConsumer}.
	 */
	BlockingQueue<IMediaChunk> getInputQueue();
	
	/** Returns the number of bytes written to output so far. */ 
	public long getNumberOfBytesWriten();
	/** Returns the number of {@link IMediaChunk}s consumed by this {@link IStreamConsumer} so far. */
	public long getNumberOfConsumedMediaChunks();
	
	
	/** Starts processing of incoming {@link IMediaChunk}. */
	public void start();
	/** Stops processing of incoming {@link IMediaChunk}. */
	public void stop();
	/** 
	 * Blocks until this {@link IStreamConsumer} has stoped or timeout reached.
	 * 
	 * @param timeout max ms to wait for a stop.
	 * @throws InterruptedException if interrupted
	 * @throws TimeoutException if timeout ms passes before actual stop.
	 */
	public void join(long timeout) throws InterruptedException,TimeoutException;
	
	/**
	 * @param destination something that gives a nice description of destination, to which this {@link IStreamConsumer} writes to (e.g. String, {@link File}, {@link InetAddress}, etc.).
	 */
	public void setDestination(Object destination);
	public Object getDestination();
	
	public void setOutputStream(OutputStream os);
	public OutputStream getOutputStream();

	public void setCloseable(Closeable closeable);
	public Closeable getCloseable();


	/**
	 * Returns BigInteger that describes the temporal reference point where this {@link IStreamConsumer} has been started.
	 * Usually it is set&used by the underlying stream in order to adapt timestamps in a media stream.  
	 * @return temporal reference point where this {@link IStreamConsumer} has started.
	 */
	public BigInteger getStartTimeCode();
	void setStartTimeCode(BigInteger startTimecode);

	/**
	 * Returns a unique and somewhat descriptive name of the {@link IStreamConsumer}.
	 * It usually contains the actual type, instance number, destination.
	 * Its purpose is to make {@link IStreamConsumer}s distinguishable for humans. 
	 * @return
	 */
	String getName();
	
	public boolean isRunning();

	
}
