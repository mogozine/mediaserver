package com.mgz.mediaserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The newest approach.
 * @author rf
 *
 */
public class MediaServerBase implements IMediaServer,Runnable {
	int instanceCounter;
	protected Logger log;

	MediaStreamParameters params;
	boolean isMediaProducer;
	boolean isRunning;
	String name;
	
	Thread thread;
	private List<InetAddress> acceptableHostAddresses = new ArrayList<InetAddress>();
	ServerSocket serverSocket;
	
	public MediaServerBase(){
		instanceCounter++;
		this.name = this.getClass().getName() + instanceCounter;
		log = Logger.getLogger(this.getClass().getName() + instanceCounter);
	}
	

	@Override
	public void init() throws IOException {
		serverSocket = new ServerSocket(params.getPort());
		serverSocket.setSoTimeout(ApplicationMediaServer.TIMEOUT_MEDIASERVERTCP);
		log.log(Level.INFO, name + " listen on port " + serverSocket.getLocalPort());
	}	
	
	@Override
	public void run() {
		if(params==null) log.log(Level.SEVERE, "No streamparameters set.");
		
		isRunning=true;

		try{
			
			while(isRunning){
				try{
					Socket clientSocket = serverSocket.accept();
					String remoteAddress = clientSocket.getInetAddress().getHostAddress();

					
					InputStream is = clientSocket.getInputStream();
					OutputStream os = clientSocket.getOutputStream();

					
					if(acceptableHostAddresses.contains(remoteAddress)){

						IMediaStream stream = MediaStreamManager.lookupStream(params);
						
						if(isMediaProducer){
							IStreamProducer producer = MediaStreamFactory.buildMediaStreamProducer(params);
							producer.setCloseable(clientSocket);
							producer.setInputStream(is);
							producer.start();
							stream.setStreamProducer(producer);
							
							// We listening only for the first producer.
							break;
							
						}else{
							IStreamConsumer consumer = MediaStreamFactory.buildMediaStreamConsumer(params);
							consumer.setCloseable(clientSocket);
							consumer.setOutputStream(os);
							consumer.start();
							stream.addStreamConsumer(consumer);
						}
						
						
					}else{

						log.log(Level.WARNING, "Rejected consumer connection from unknown remote address " + remoteAddress + ".");

					}
				}catch(SocketTimeoutException stex){
					log.log(Level.WARNING, "Closing after socket timeout.");
				}
			}

		} catch (Throwable th) {
			th.printStackTrace();
		}finally{
			isRunning = false;
			if(serverSocket!=null){
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			log.log(Level.FINE, "End processing.");
		}
	}

	@Override
	public void start() {
		this.thread = new Thread(this,name);
		this.thread.start();
	}

	@Override
	public void stop() {
		this.isRunning = false;
		
	}

	@Override
	public void join(long milliseconds) throws InterruptedException {
		if(this.thread!=null ) thread.join(milliseconds);
	}	
	
	@Override
	public void setMediaStreamParams(MediaStreamParameters params) {
		this.params = params;
		
	}

	@Override
	public void setIsMediaProducer(boolean isMediaProducer) {
		this.isMediaProducer = isMediaProducer;
		
	}

	@Override
	public boolean isMediaProducer() {
		return isMediaProducer;
	}

	public MediaStreamParameters getParams() {
		return params;
	}

	public void setParams(MediaStreamParameters params) {
		this.params = params;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public int getInstanceCounter() {
		return instanceCounter;
	}

	public String getName() {
		return name;
	}

	public void addAcceptableHostAddress(InetAddress hostAddress) {
		this.acceptableHostAddresses.add(hostAddress);
	}

	public void removeAcceptableHostAddress(InetAddress hostAddress) {
		this.acceptableHostAddresses.remove(hostAddress);
	}


}
