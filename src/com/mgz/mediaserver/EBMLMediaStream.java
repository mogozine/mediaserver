package com.mgz.mediaserver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

import com.mgz.mediaserver.ebml.EBMLElement;
import com.mgz.mediaserver.ebml.EBMLElementType;
import com.mgz.mediaserver.exception.StreamStateException;


/**
 * Specialized EBML Media Stream.<br>
 */
public class EBMLMediaStream extends MediaStreamGeneric{

	volatile List<EBMLElement> streamHeaderElements;
	private List<EBMLElement> tmpStreamHeaderElements;

	volatile int clusterCounter = 0;
	private BigInteger streamStartTimeCode;

	public EBMLMediaStream(String name, String mimeType) {
		super(name , mimeType);
	}


	@Override
	public void distribute(IMediaChunk nextMediaChunk) throws StreamStateException{
		try{

			if(!(nextMediaChunk instanceof EBMLElement)){
				StreamStateException ex = new StreamStateException(EBMLMediaStream.class.getSimpleName() + " can only eat " + EBMLElement.class.getSimpleName() + ".");
				ex.setOffendingObject(nextMediaChunk);
				throw ex;
			}



			EBMLElement nextElement = (EBMLElement) nextMediaChunk;
			EBMLElementType elementType = nextElement.getElementType();

			if(EBMLElementType.EBML == elementType){
				log.log(Level.INFO,"Stream header start.");
				tmpStreamHeaderElements = new ArrayList<EBMLElement>();

			}else if(EBMLElementType.Cluster == elementType){

				if(tmpStreamHeaderElements!=null){
					log.log(Level.INFO,"Stream header finished.");
					synchronized (this){
						streamHeaderElements = tmpStreamHeaderElements;
						tmpStreamHeaderElements=null;
						distributeStreamHeaderToAllConsumers();
					}
				}

				clusterCounter++;
			}

			if(tmpStreamHeaderElements!=null){
				// Currently the stream header is assembled.
				tmpStreamHeaderElements.add(nextElement);

			}else{
				distributeNextEBMLElement(nextElement);
			}


		} catch (Throwable e) {
			e.printStackTrace();
		}finally{
			stop();
		}
	}



	private void distributeNextEBMLElement(EBMLElement nextElement) {
		IStreamConsumer[] consumers = null;
		synchronized (streamConsumers) {
			consumers = (IStreamConsumer[]) streamConsumers.toArray();
		}
		for(IStreamConsumer consumer : consumers){
			if(consumer.isRunning()){
				sendEBMLElement(nextElement, consumer);
			}
		}
	}


	private void distributeStreamHeaderToAllConsumers() {
		synchronized (streamConsumers) {
			for(EBMLElement ebmlElement : (EBMLElement[]) streamHeaderElements.toArray()){

				for(IStreamConsumer consumer:streamConsumers){
					sendEBMLElement(ebmlElement, consumer);
				}

			}
		}

	}


	private void sendEBMLElement(EBMLElement element, IStreamConsumer streamConsumer) {
		BlockingQueue<IMediaChunk> consumerInputQueue = streamConsumer.getInputQueue();


		if(EBMLElementType.Timecode == element.getElementType()){
			BigInteger newTimeCode = (BigInteger) element.getInnerValue();
			BigInteger consumerStartTimecode = streamConsumer.getStartTimeCode();

			if(streamStartTimeCode==null){
				// First time code in stream.
				streamStartTimeCode = newTimeCode;

			}

			if(consumerStartTimecode==null){
				streamConsumer.setStartTimeCode(newTimeCode);
				element = EBMLElement.createTimeCodeEBMLElement(BigInteger.ZERO);

			}else{

				if(!streamStartTimeCode.equals(consumerStartTimecode)) {
					// --> Adopt time code to consumer's individual start time code.
					BigInteger timeCodeForConsumer = newTimeCode.subtract(consumerStartTimecode);
					element = EBMLElement.createTimeCodeEBMLElement(timeCodeForConsumer);
				}
			}
		}

		if(!consumerInputQueue.offer(element)){
			log.warning("Consumer '" + streamConsumer.getName() +"' missed offer.");
		}else{
			synchronized (consumerInputQueue) {
				consumerInputQueue.notifyAll();
			}
		}

	}


	@Override
	public void addStreamConsumer(IStreamConsumer streamConsumer) {
		synchronized (this) {
			if(streamHeaderElements!=null){	
				for(EBMLElement ebmlElement : (EBMLElement[]) streamHeaderElements.toArray()){
					sendEBMLElement(ebmlElement, streamConsumer);

				}
			}
			synchronized (this.streamConsumers) {
				this.streamConsumers.add(streamConsumer);
				this.streamConsumers.notifyAll();
			}
			log.log(Level.INFO, "Added consumer " +streamConsumer.getName() + ".");		
		}		
	}

}
