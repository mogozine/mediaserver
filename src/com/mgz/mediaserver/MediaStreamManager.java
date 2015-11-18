package com.mgz.mediaserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mgz.mediaserver.exception.MediaStreamManagerException;

public class MediaStreamManager {
	private static final Logger log = Logger.getLogger( MediaStreamManager.class.getName() );

	// Known commands:
	public static String COMMAND_REGISTER = "register";
	public static String COMMAND_UNREGISTER = "unregister";
	public static String COMMAND_RECORDING = "recording";
	public static String COMMAND_PRODUCE = "produce";
	public static String COMMAND_CONSUME = "consume";
	public static String COMMAND_STOP = "stop"; 



	private static Map<String,IMediaStream>mapStreamName2MediaStream = new HashMap<>();
	private static Map<IMediaStream,MediaStreamParameters> mapStream2Params = new HashMap<>();

	public static void request(MediaStreamParameters params) throws MediaStreamManagerException{
		String command = params.getCommand();


		if(COMMAND_REGISTER.equals(command)){
			// Registers a new Stream.
			registerMediaStream(params);
		}else if(COMMAND_UNREGISTER.equals(command)){
			// Unregisters a existing Stream (shuts stream down).
			unregisterMediaStream(params);

		}else if(COMMAND_PRODUCE.equals(command)){
			// Open port for incoming stream data.
			openProducerChannel(params);

		}else if(COMMAND_CONSUME.equals(command)){
			// Open port for outgoing stream data.
			openConsumerChannel(params);

		}else if(COMMAND_RECORDING.equals(command)){
			startRecording(params);

		}
	}

	public static synchronized void registerMediaStream(MediaStreamParameters params) throws MediaStreamManagerException{
		if(params.getPassword()==null) params.setPassword(Tool.createPassword());
		if(params.getMimType()==null) params.setMimType(Tool.MIMETYPE_WEBM_VIDEO);

		IMediaStream mediaStream = lookupStream(params);
		if(mediaStream == null){
			// Stream doesn't exist --> Create new one.
			params.setToken(createToken());
			mediaStream = MediaStreamFactory.buildMediaStream(params);
			mapStreamName2MediaStream.put(mediaStream.getName(), mediaStream);
			mapStream2Params.put(mediaStream, params);

		}else{
			throw new MediaStreamManagerException("Stream already exists.");
		}
	}	

	public static void unregisterMediaStream(MediaStreamParameters params) throws MediaStreamManagerException {
		IMediaStream stream = lookupStream(params);
		if(stream==null) throw new MediaStreamManagerException("Stream is unknown.");
		stream.stop();
		try {
			stream.join(1000L);
		} catch (Throwable th) {
			th.printStackTrace();
		}

	}

	public static void openProducerChannel(MediaStreamParameters params) throws MediaStreamManagerException {
		IMediaStream stream = lookupStream(params);
		if(stream==null) throw new MediaStreamManagerException("Stream is unknown.");
		if(stream.getStreamProducer()!=null) throw new MediaStreamManagerException("Stream " + stream.getName() + " already has a producer channel.");

		try {
			params.setToken(createToken());
			IStreamProducer producer = MediaStreamFactory.buildMediaStreamProducer(params);
			stream.setStreamProducer(producer);
			producer.start();

			log.log(Level.FINE,"Open producer channel on port " + params.getPort());


		} catch (Throwable e) {
			throw new MediaStreamManagerException("Open producer channel failed:" + e.getMessage());
		}
	}

	public static void openConsumerChannel(MediaStreamParameters params) throws MediaStreamManagerException {
		IMediaStream stream = lookupStream(params);
		if(stream==null) throw new MediaStreamManagerException("Stream is unknown.");

		InetAddress hostAddress = params.getIp();
		if(hostAddress==null) throw new MediaStreamManagerException("Missing host address.");

		// Create new consumer server.
		MediaServerBase consumerServer = new MediaServerBase();
		consumerServer.setIsMediaProducer(false);
		
		consumerServer.addAcceptableHostAddress(hostAddress);
		try {
			consumerServer.init();
		} catch (IOException e) {
			throw new MediaStreamManagerException("Failed to open MediaServer:"+e.getMessage(),e);
		}
		consumerServer.start();

		log.log(Level.FINE,"Added accaptable host adress " + hostAddress + ".");
	}

	public static StreamConsumerGeneric openConsumer(MediaStreamParameters params, OutputStream os) throws MediaStreamManagerException {
		IMediaStream stream = lookupStream(params);
		if(stream==null) throw new MediaStreamManagerException("Stream is unknown.");

		StreamConsumerGeneric consumer = new StreamConsumerGeneric();
		stream.addStreamConsumer(consumer);

		return consumer;
	}

	public static void startRecording(MediaStreamParameters params) throws MediaStreamManagerException {
		try{
			IMediaStream stream = lookupStream(params);

			File videoFile = new File(ApplicationMediaServer.DIR_VIDEOSTORE, params.getCustomerCode());
			OutputStream os = Tool.createPathAndOpenOutputStream(videoFile);

			IStreamConsumer streamConsumer = new StreamConsumerGeneric();
			streamConsumer.start();

			stream.addStreamConsumer(streamConsumer);

			log.log(Level.FINE, "Created recorder.");

		} catch (FileNotFoundException e) {
			MediaStreamManagerException msmex = new MediaStreamManagerException("Failed to create recorder: " +  e.getMessage());
			throw msmex;
		}

	}

	public static IMediaStream lookupStream(MediaStreamParameters params) throws MediaStreamManagerException{
		String streamName = params.getStreamName();
		String streamPassword = params.getPassword();
		String token = params.getToken();
		if(streamName==null || streamPassword==null || token==null){
			throw new MediaStreamManagerException("Invalid parameter.");
		}

		IMediaStream stream = mapStreamName2MediaStream.get(streamName);

		if(stream==null) return null;
		
		MediaStreamParameters streamParams = mapStream2Params.get(stream);
		
		if(!token.equals(streamParams.getToken())) throw new MediaStreamManagerException("Token missmatch.");
		if(!streamPassword.equals(streamParams.getPassword())) throw new MediaStreamManagerException("Authorization failed.");

		return stream;
	}

	public static void openTestStream(int port) throws MediaStreamManagerException{
		MediaStreamParameters params = new MediaStreamParameters();
		
		params.setStreamName("test");
		params.setAppKey("test");
		params.setToken("test");
		params.setCustumerCode("test");
		params.setDirectoryForVideo(new File("./videos"));
		params.seteTag("test");
		params.setMimType(Tool.MIMETYPE_WEBM_VIDEO);
		
		EBMLMediaStream stream = new EBMLMediaStream(params.getStreamName(),params.getMimType());
		mapStreamName2MediaStream.put("test", stream);
		mapStream2Params.put(stream, params);
/*
		try{
			IStreamProducer producer = MediaStreamFactory.buildMediaStreamProducer(params);
			stream.setStreamProducer(producer);
			producer.start();
			port = producer.getPort();
		} catch (IOException e) {
			throw new MediaStreamManagerException("Failed to create " + EBMLMediaStreamProducer.class.getSimpleName() + ": " + e.getMessage());
		}

		try{
			MediaStreamServerTCP consumerServer = new MediaStreamServerTCP(stream,port+1);
			consumerServer.start();
			port = consumerServer.getPort();
		} catch (IOException e) {
			throw new MediaStreamManagerException("Failed to create " + MediaStreamServerTCP.class.getSimpleName() + ": " + e.getMessage());
		}

		MedaStreamServerHTTP consumerHttp = new MedaStreamServerHTTP(port+1);
		consumerHttp.start();

		Date now = new Date();
		String time = "" + now.getDate() + "_" + now.getHours() + ":" + now.getMinutes();

		File videoFile = new File(ApplicationMediaServer.DIR_VIDEOSTORE, "test_" + time + ".webm");
		OutputStream os;
		try {
			os = Tool.createPathAndOpenOutputStream(videoFile);
			StreamConsumerGeneric streamConsumer = new StreamConsumerGeneric(stream, os, os, videoFile);
			stream.addStreamConsumer(streamConsumer);
			streamConsumer.start();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
*/

	}

	private static String createToken(){
		String token = null;
		token = UUID.randomUUID().toString();
		return token;
	}

}
