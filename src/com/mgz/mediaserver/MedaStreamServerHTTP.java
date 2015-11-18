package com.mgz.mediaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mgz.mediaserver.exception.MediaStreamManagerException;

public class MedaStreamServerHTTP implements Runnable{
	private static final int FAKE_CONTENTLENGTH = 999999999;
	int inttanceCounter;
	private Logger log;
	private volatile boolean isRunning;
	private int port=0;
	private boolean isProducerServer;

	public MedaStreamServerHTTP(boolean isProducerServer){
		
	}

	public void run(){
		isRunning=true;

		ServerSocket serverSock = null;
		Socket clientSocket = null;

		try {

			log.log(Level.INFO, "Open ServerSocket on port " + port + ".");

			serverSock = new ServerSocket(port);
			serverSock.setSoTimeout(ApplicationMediaServer.TIMEOUT_MEDIASERVERTCP);
			while(isRunning && clientSocket==null){
				try{
					clientSocket = serverSock.accept();
				}catch(SocketTimeoutException ste){
					continue;
				}

				if(isRunning && clientSocket!=null){
					log.log(Level.INFO, "Incoming connection.");

					InputStream sis = null;
					OutputStream os = null;
					IMediaStream stream = null;
					MediaStreamParameters params = null;
					
					try{
						sis = clientSocket.getInputStream();
						os = clientSocket.getOutputStream();

						BufferedReader br = new BufferedReader(new InputStreamReader(sis));
						String strHttpFirstLine = br.readLine(); // Now you get "GET index.html HTTP/1.1"
						params = MediaStreamParameters.parsParamsFromHttpFirstLine(strHttpFirstLine);

						// Determine Destination without reverse DNS lookup.
						String destination = "HTTP;"+clientSocket.getInetAddress().getHostAddress();

						stream = MediaStreamManager.lookupStream(params);

						if(stream!=null){
							StreamConsumerGeneric consumer=null;// = new StreamConsumerGeneric(stream, os, clientSocket, destination);
							stream.addStreamConsumer(consumer);

							writeHeader(stream, os);
							consumer.start();

							// Determine Destination by best efford reverse DNS lookup.
							destination = "HTTP;" + clientSocket.getInetAddress().getCanonicalHostName();
							consumer.setDestination(destination);
						}else{
							throw new Exception("Invalid params.");
						}
					
					}catch(Throwable msmExc){
						log.log(Level.WARNING, "Reception of an incoming connection failed: " + msmExc.getClass().getSimpleName() + ":" + msmExc.getMessage());
						msmExc.printStackTrace();
						
						try{
							writeErrorHeader(params, os,msmExc);
						}finally{
							try{os.close();}catch(IOException iox){};
							try{clientSocket.close();}catch(IOException iox){};
						}
						log.log(Level.WARNING, "Continue listening after reception of an incoming connection failed.");
					}finally{
						clientSocket=null;
					}
				}		
			}

		} catch (Throwable e) {
			log.log(Level.SEVERE, "Shut down, after Unhandled Exception:\n"+ e.getMessage());
			e.printStackTrace();

		}finally{
			try {
				if(clientSocket!=null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if(serverSock!=null)serverSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}



	private void writeHeader(IMediaStream stream, OutputStream os) throws IOException {

		String mimeType = stream.getMimeType();
		String eTag = "" + System.currentTimeMillis();
		String serverTime = getServerTime();

		String header = "HTTP/1.1 200 OK\r\n"
				+"Date: " + serverTime + "\r\n"
				+"Server: MediaServer\n"
				+"Last-Modified: "+ serverTime + "\r\n"
				+"ETag: \""+eTag+"\"\r\n"
				+"Content-Type: " + mimeType + "\r\n"
				+"Content-Length: " + FAKE_CONTENTLENGTH + "\r\n"
				+"Accept-Ranges: bytes\r\n"
				+"Connection: close\r\n\r\n";

		try {
			os.write(header.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			os.write(header.getBytes());
		}
	}

	private void writeErrorHeader(MediaStreamParameters params, OutputStream os, Throwable exc) {

		try{

			String mimeType = params!=null && params.getMimType()!=null ? params.getMimType() : "text/html";
			String eTag = params!=null && params.geteTag()!=null ? params.geteTag(): "1101010101110101";
			String serverTime = getServerTime();

			String header = "HTTP/1.1 403 Forbidden\r\n"
					+"Date: " + serverTime + "\r\n"
					+"Server: MediaServer\r\n"
					+"Last-Modified: "+ serverTime + "\r\n"
					+"ETag: \""+eTag+"\"\r\n"
					+"Content-Type: " + mimeType + "\r\n"
					+"Content-Length: " + FAKE_CONTENTLENGTH + "\r\n"
					+"Accept-Ranges: bytes\r\n"
					+"Connection: close\r\n\r\n";

			try {
				os.write(header.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				os.write(header.getBytes());
			}

			byte[] msg = null;
			if (exc instanceof MediaStreamManagerException) {
				msg = ((MediaStreamManagerException)exc).getResponseMessage().getBytes();
			}else{
				msg = exc.getMessage().getBytes();
			}
			os.write(msg);
		}catch(Throwable th){
			log.log(Level.WARNING, th.getMessage());
		}
	}



	String getServerTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
