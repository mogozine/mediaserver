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


public class MediaStreamServerTCP extends Thread{
	private Logger log;

	private static int instanceCounter=0;
	private ServerSocket serverSocket;
	private IMediaStream stream;
	private boolean isRunning;
	private int port;
	private MediaStreamParameters params;
	
	private List<InetAddress> acceptableHostAddresses = new ArrayList<InetAddress>();

	private boolean isProducerServer;

	public MediaStreamServerTCP(boolean isInputServer) {
		super(MediaStreamServerTCP.class.getSimpleName() + instanceCounter++);
		log = Logger.getLogger(this.getName());

	}

	public void run() {
		if(params==null) log.log(Level.SEVERE, "No streamparameters set.");
		
		isRunning=true;

		try{
			serverSocket = new ServerSocket(port);
			log.log(Level.INFO, this.getName() + " listen on port " + serverSocket.getLocalPort());
			
			while(isRunning && stream.isRunning()){
				try{

					serverSocket.setSoTimeout(ApplicationMediaServer.TIMEOUT_MEDIASERVERTCP);
					Socket clientSocket = serverSocket.accept();
					String remoteAddress = clientSocket.getInetAddress().getHostAddress();

					if(acceptableHostAddresses.contains(remoteAddress)){

						if(isProducerServer){
							InputStream is = clientSocket.getInputStream();
							IStreamProducer producer = MediaStreamFactory.buildMediaStreamProducer(params);
							producer.setCloseable(clientSocket);
							producer.setInputStream(is);
							producer.start();
							stream.setStreamProducer(producer);
							
							// We listening only for the first producer.
							break;
							
						}else{
							OutputStream os = clientSocket.getOutputStream();
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



	public int getPort(){
		return serverSocket.getLocalPort();
	}

	public synchronized void shutDown() {
		log.log(Level.FINE, "Shut down.");
		this.isRunning=false;
		try {
			this.join(1000);
		} catch (InterruptedException e) {
			// NOP;
		}
	}

	public void addAcceptableHostAddress(InetAddress hostAddress) {
		this.acceptableHostAddresses.add(hostAddress);
	}


	public void setPort(int port) {
		this.port = port;
	}
}
