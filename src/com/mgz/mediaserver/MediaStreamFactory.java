package com.mgz.mediaserver;


/**
 * Factory for implementations of IMediaStream;
 */
public class MediaStreamFactory {
	
	public static IMediaStream buildMediaStream(MediaStreamParameters params){
		String mimeType = params.getMimType();
		String name = params.getStreamName();
		
		IMediaStream newStream = null;
		
		for(String knownEBMLMimeType : Tool.EBML_MIMETYPES){
			if(knownEBMLMimeType.equals(mimeType)){
				newStream = new EBMLMediaStream(name, mimeType);
				if(name==null) params.setStreamName(newStream.getName());
				
			}
		}
		
		return newStream;
	}


	public static IStreamProducer buildMediaStreamProducer(MediaStreamParameters params) {
		String mimeType = params.getMimType();

		IStreamProducer producer = null;
		
		for(String knownEBMLMimeType : Tool.EBML_MIMETYPES){
			if(knownEBMLMimeType.equals(mimeType)){
				producer = new EBMLMediaStreamProducer();
			}
		}

		
		return producer;
	}

	public static IStreamConsumer buildMediaStreamConsumer(MediaStreamParameters params) {
		return new StreamConsumerGeneric();
	}	
	
	
}
