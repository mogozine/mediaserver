package com.mgz.mediaserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mgz.mediaserver.exception.StreamStateException;


/**
 * Polls {@link IMediaChunk} from stream producer,
 * distributes it by offering it to the input queues of registered stream consumers. 
 * If the input queue refuses the offer (e.g. queue is full) a log message is issued. However, the incoming {@link IMediaChunk} is not 
 * offered to the consumer's input queue a second time.<br>
 * <br>   
 * In order to change the way how incoming {@link IMediaChunk}s are distributed, overwrite {@link #distribute(IMediaChunk)}.
 */

public class MediaStreamGeneric implements Runnable,IMediaStream{
	private static volatile int instanceCounter;

	protected Logger log;
	private String name;
	private String mimeType;

	protected volatile boolean isRunning;
	protected IStreamProducer streamProducer;
	protected List<IStreamConsumer> streamConsumers = new ArrayList<IStreamConsumer>();

	private Thread thread;


	/**
	 * Creates a new {@link MediaStreamGeneric} object.
	 * 
	 * @param name the name of this {@link MediaStreamGeneric}. If null, a name is created.
	 * @param mimeType the mime type of the media of this stream.
	 */
	public MediaStreamGeneric(String name, String mimeType){
		instanceCounter++;
		this.name = name!=null ? name : this.getClass().getName() + instanceCounter;
		this.mimeType = mimeType;
		log = Logger.getLogger(name);
	}

	@Override
	public final void start() {
		if(this.thread!=null && this.thread.isAlive()){
			log.log(Level.SEVERE, "Starting stream which is allready running.");
		}else{
			this.isRunning = true;
			this.thread = new Thread(this);
			this.thread.start();
		}
	}

	@Override
	public void run() {
		this.isRunning = true;
		while(isRunning && this.streamProducer==null){
			try {
				wait();
			} catch (InterruptedException e) {/*NOP*/}
		}
		if(streamProducer!=null){
			BlockingQueue<IMediaChunk> queue = this.streamProducer.getOutputQueue();

			while (isRunning && this.streamProducer.isRunning()){
				try{
					while(!queue.isEmpty()){
						IMediaChunk nextChunk = queue.poll();
						distribute(nextChunk);
					}
					synchronized (queue) {
						try {
							queue.wait(1000);
						} catch (InterruptedException e) {}	
					}
				} catch (StreamStateException e) {
					log.log(Level.SEVERE, "Stream state exception:" + e.getMessage());
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * Iterates over all registered {@link IStreamConsumer} and overs the given {@link IMediaChunk} to their input queue.
	 * @param nextChunk
	 * @throws StreamStateException 
	 */
	protected void distribute(IMediaChunk nextChunk) throws StreamStateException {
		IStreamConsumer[] consumers = null;

		synchronized(streamConsumers){
			consumers = (IStreamConsumer[]) streamConsumers.toArray();
		}

		for(IStreamConsumer consumer : consumers){
			if(consumer.isRunning()){
				BlockingQueue<IMediaChunk> queue = consumer.getInputQueue();
				if(!queue.offer(nextChunk)){
					log.warning("Consumer '" + consumer.getName() +"' missed offer.");
				}else{
					synchronized (queue) {
						queue.notifyAll();
					}
				}
			}
		}
	}

	@Override
	public final void stop() {
		this.isRunning = false;
		try {
			thread.join(2000l);
		} catch (InterruptedException e) {}
	}


	@Override
	public final void join(long timeout) throws InterruptedException,
	TimeoutException {

		if(thread!=null && thread.isAlive()){
			thread.join(timeout);
			if(thread.isAlive()) throw new TimeoutException();
		}

	}	

	/**
	 * Set the given producer and calls this.noifyAll().
	 * If thsi stream allready has a producer a {@link StreamStateException} is thrown.
	 */
	@Override
	public void setStreamProducer(IStreamProducer producer) throws StreamStateException{
		if(streamProducer==null){
			this.streamProducer = producer;
			synchronized (this) {
				this.notifyAll();
			}
			log.log(Level.INFO, "Producer set: " +producer.getName() + ".");	
		}
		else throw new StreamStateException("This stream already has a producer set.");
	}

	@Override
	public IStreamProducer getStreamProducer(){
		return this.streamProducer;
	}

	@Override
	public void removeStreamConsumer(IStreamConsumer consumer){
		synchronized (this.streamConsumers) {
			this.streamConsumers.remove(consumer);	
		}
		log.log(Level.INFO, "Removed consumer " +consumer.getName() + ".");		

	}

	@Override
	public void addStreamConsumer(IStreamConsumer streamConsumer) {
		synchronized (this.streamConsumers) {
			this.streamConsumers.add(streamConsumer);
			this.streamConsumers.notifyAll();
		}
		log.log(Level.INFO, "Added consumer " +streamConsumer.getName() + ".");		
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public final String getName() {
		return name;
	}
	@Override
	public boolean isRunning() {
		return isRunning;
	}

}
