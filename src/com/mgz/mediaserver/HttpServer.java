package com.mgz.mediaserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mgz.mediaserver.ebml.EBMLElement;
import com.mgz.mediaserver.exception.MediaStreamManagerException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpServer {
	private static final Logger log = Logger.getLogger( HttpServer.class.getName() );

	public HttpServer(int port) throws Exception {
		
		log.log(Level.INFO,"Start HTTP server on port " + port);

		com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/register", new StreamRegistrationHttpHandler());
		server.createContext("/admin", new AdministrationHttpHandler()); 
		server.createContext("/content", new FileHttpHandler()); // Make sure directory ./content exists.
		server.setExecutor(null); // creates a default executor
		server.start();
		
		log.log(Level.INFO,"HTTP server listen on port " + port);
	}
	


	/**
	 * Implementation of registration of media streams.
	 */
	static class StreamRegistrationHttpHandler implements HttpHandler {

		public void handle(HttpExchange t) throws IOException {

			InputStreamReader inr = null;

			try{
				inr = new InputStreamReader(t.getRequestBody());

				MediaStreamParameters paramsRequest = MediaStreamParameters.jsonToParams(inr);
				paramsRequest.setIp(t.getRemoteAddress().getAddress());

				MediaStreamManager.request(paramsRequest);

				byte[] response = "TODO: Implement".getBytes("utf-8");

				Headers h = t.getResponseHeaders();
				h.set("Content-Type","application/json");

				t.sendResponseHeaders(200, response.length);
				t.getResponseBody().write(response);

			}catch (MediaStreamManagerException mex){

				log.warning(StreamRegistrationHttpHandler.class.getSimpleName()+"; exception; "+ mex.getMessage());

				if(mex.getResponseMessage()!=null){
					byte[] msg = mex.getResponseMessage().getBytes("utf-8");
					t.getResponseBody().write(msg);
					t.sendResponseHeaders(1000, msg.length);
				}else{
					t.sendResponseHeaders(1000, 0);
				}

				mex.printStackTrace();

			}catch(Throwable ex){

				ex.printStackTrace();

				t.sendResponseHeaders(1000, 0);

			}finally{
				// NOP;
			}
		}
	}

	/**
	 * Implementes the adminstration tasks.
	 *
	 */
	static class AdministrationHttpHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			t.sendResponseHeaders(200, 0);
			
			// TODO: implement admin interface.
			
			t.close();
		}
	}




	/**
	 * Serves static content stored in directory ./content.
	 */
	static class FileHttpHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange t) throws IOException {
			long time = System.currentTimeMillis();
			String root = ".";
			URI uri = t.getRequestURI();
			String path = uri.getPath();
			File file = new File(root + path).getCanonicalFile();


			if (!file.isFile()) {
				// Object does not exist or is not a file: reject with 404 error.
				byte[] response = "404 (Not Found)\n".getBytes();
				t.sendResponseHeaders(404, response.length);
				OutputStream os = t.getResponseBody();
				os.write(response);
				os.close();
			} else {

				String mime = Tool.guessMimeTypeForPath(path);

				Headers h = t.getResponseHeaders();
				h.set("Content-Type", mime);
				t.sendResponseHeaders(200, file.length());              

				OutputStream os = t.getResponseBody();
				BufferedInputStream fs = new BufferedInputStream(new FileInputStream(file),(int)file.length());
				byte[] buffer = new byte[10000];
				int count = 0;
				while ((count = fs.read(buffer)) >= 0) {
					os.write(buffer,0,count);
				}
				fs.close();
				os.close();
				t.close();
			}
			log.log(Level.INFO,"file request; " + t.getRemoteAddress().getHostString() + "; " + root + uri.getPath() +"; " + (System.currentTimeMillis()-time));
		}
	}

	protected boolean isRunnig() {
		// TODO Auto-generated method stub
		return false;
	}	
}
