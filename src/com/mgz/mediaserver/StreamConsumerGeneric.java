package com.mgz.mediaserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * This class provides a generic functionality to poll {@link IMediaChunk}s from the input Queue and writes it to an {@link OutputStream} by calling the method {@link IMediaChunk#write(OutputStream)}.
 * Overwrite methods {@link #processChunk(IMediaChunk)} or {@link #writeChunk(IMediaChunk)} to change behavior.
 * 
 */
public class StreamConsumerGeneric extends Thread implements IStreamConsumer{
	private static int instanceCounter=0;
	private Logger log;
	private boolean isRunning;

	private LinkedBlockingQueue<IMediaChunk> inputQueue; 

	IMediaStream stream;
	OutputStream os;
	Closeable closeable;
	private BigInteger startTimecode;
	private Object destination;
	private volatile long numberOfBytesWrittenToOutputStream;
	private volatile long numberOfConsumedIMediaChunks;


	public StreamConsumerGeneric() {
		super(StreamConsumerGeneric.class.getName() + instanceCounter);
		instanceCounter++;
		log = Logger.getLogger(this.getName());
		inputQueue = new LinkedBlockingQueue<IMediaChunk>();
	}

	@Override
	public void run(){
		isRunning=true;

		stream.addStreamConsumer(this);

		try {

			do{

				IMediaChunk nextChunk=null;
				do{
					try {
						nextChunk = inputQueue.poll(1000, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) { 
						// NOP;
					}

					if(nextChunk != null){
						numberOfConsumedIMediaChunks++;
						
						log.log(Level.INFO, "Consuming next Chunk, h = " + nextChunk.hashCode());
						nextChunk = processChunk(nextChunk);
						numberOfBytesWrittenToOutputStream += writeChunk(nextChunk);
					}
				}while(!inputQueue.isEmpty());
				os.flush();

			}while(isRunning && stream.isRunning());

			log.log(Level.INFO, "End consuming.");

		}catch(IOException ioExc){

			ioExc.printStackTrace();

		}finally{
			shutDown();
		}
	}
	
	/**
	 * Writes the given {@link IMediaChunk} to the output stream by using {@link IMediaChunk#write(OutputStream)}.<br>
	 * Overwrite this method if you have to send additional data before/after the given {@link IMediaChunk} is sent.<br>
	 * If you plan to change the way how {@link IMediaChunk} are serialized in general, is usually a better way to overwrite {@link IMediaChunk#write(OutputStream)}.<br>
	 * 
	 * @param nextChunk to send
	 * @return the number of bytes acually written to output stream.
	 * @throws IOException
	 */
	protected long writeChunk(IMediaChunk nextChunk) throws IOException {
		return nextChunk.write(os);
		
	}

	/**
	 * This method does nothing and simply returns the given {@link IMediaChunk}.<br>
	 * Override this method id you have to manipulate the {@link IMediaChunk}.
	 * @param nextChunk
	 * @return an {@link IMediaChunk} that replaces the given {@link IMediaChunk}.
	 */
	protected IMediaChunk processChunk(IMediaChunk nextChunk) {
		return nextChunk;
	}

	/*
	private void sendEBMLElement(EBMLElement element) throws IOException {
		if(EBMLElementType.Timecode == element.getElementType()){
			BigInteger tmpStreamTimecode = (BigInteger) element.getInnerValue();
			if(startTimecode==null) startTimecode = tmpStreamTimecode;
			sendTimeCode(tmpStreamTimecode.subtract(startTimecode),os);
		}else{
			consumedBytes += element.write(os);							
		}
	}

	private void sendTimeCode(BigInteger consumerTimecode, OutputStream os) throws IOException {
		byte[] payload = consumerTimecode.toByteArray();
		EBMLElement timecode = new EBMLElement(EBMLElementType.Timecode.getElementTypeCode(), new byte[] {(byte)(0x80 + payload.length)}, payload);
		consumedBytes += timecode.write(os);
	}
	 */
	public void shutDown() {
		log.log(Level.INFO, "Shut down.");

		isRunning = false;

		this.stream.removeStreamConsumer(this);
		inputQueue.clear();

		if(os!=null)
			try {
				os.close();
				os=null;
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		if(closeable!=null){
			try {
				closeable.close();
				closeable=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			this.join(1000);
		} catch (InterruptedException e) {
			// NOP;
		}
	}	

	@Override
	public BigInteger getStartTimeCode() {
		return startTimecode;
	}

	@Override
	public void setStartTimeCode(BigInteger startTimecode) {
		this.startTimecode = startTimecode;

	}

	@Override
	public void setDestination(Object destination) {
		this.destination = destination;
	}

	@Override
	public Object getDestination() {
		return this.destination;

	}

	@Override
	public BlockingQueue<IMediaChunk> getInputQueue() {
		return inputQueue;
	}

	@Override
	public long getNumberOfBytesWriten() {
		return numberOfBytesWrittenToOutputStream;
	}

	@Override
	public long getNumberOfConsumedMediaChunks() {
		return numberOfConsumedIMediaChunks;
	}

	@Override
	public void setOutputStream(OutputStream os) {
		this.os = os;
		
	}

	@Override
	public OutputStream getOutputStream() {
		return os;
	}

	@Override
	public void setCloseable(Closeable closeable) {
		this.closeable = closeable;
		
	}

	@Override
	public Closeable getCloseable() {
		return closeable;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

}
