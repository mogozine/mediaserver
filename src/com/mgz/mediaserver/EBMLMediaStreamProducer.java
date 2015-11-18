package com.mgz.mediaserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mgz.mediaserver.ebml.EBMLDataType;
import com.mgz.mediaserver.ebml.EBMLElement;

public class EBMLMediaStreamProducer implements Runnable,IStreamProducer{
	private static int instancecounter;
	private Logger log;
	IMediaStream stream;
	private volatile boolean isRunning;
	private BlockingQueue<IMediaChunk> outputQueue;
	private long processedBytes;
	InputStream is = null;

	Thread thread = null;
	String name;
	private Closeable closeable;

	public EBMLMediaStreamProducer() {
		instancecounter++;
		outputQueue = new LinkedBlockingQueue<IMediaChunk>();
		log = Logger.getLogger( this.getName());
		name = EBMLMediaStreamProducer.class.getSimpleName() + instancecounter;
	}

	@Override
	public void run(){
		isRunning=true;
		try{

			EBMLElement element = null;
			do{
				element = EBMLElement.readNextElement(is);
				if(element==null){
					log.log(Level.INFO,"EBMLElement == null --> closing connection.;");
					break;
				}

				log.log(Level.FINE,element.toString());


				if(element.getElementType().getDataType()!=EBMLDataType.EBMLMasterElement){
					if(element.isSizeUndefined()){
						throw new IOException(element.getElementType().name() + " has undefined size.");

					}else{
						int dataLength = element.readElementData(is);
						log.log(Level.FINE,"Read element data: " + dataLength + " bytes");
					}
				}

				
				if(!outputQueue.offer(element)){
					log.log(Level.WARNING,"Stream missed an icomming element.");
				}else{
					synchronized(outputQueue){
						outputQueue.notifyAll();
					}
				}

			}while(isRunning && element!=null);

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				is=null;
			}
			isRunning=false;
			synchronized(outputQueue){
				outputQueue.notifyAll();
			}
		}


	}

	@Override
	public BlockingQueue<IMediaChunk> getOutputQueue() {
		return outputQueue;
	}

	@Override
	public long getProcessedBytes() {
		return processedBytes;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	public InputStream getInputStream() {
		return is;
	}

	public void setInputStream(InputStream inputStream) {
		this.is = inputStream;
	}

	@Override
	public void start() {
		thread = new Thread(this, name);
	}

	@Override
	public void stop() {
		this.isRunning = false;

	}

	@Override
	public void join(long l) throws InterruptedException, TimeoutException {
		if(thread!=null) thread.join(l);

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setCloseable(Closeable closeable) {
		this.closeable = closeable;
		
	}
}
