package com.mgz.mediaserver;

import java.io.File;
import java.io.Reader;
import java.net.InetAddress;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;



public class MediaStreamParameters {
	public static final String PARAM_streamName = "streamName";
	public static final String PARAM_mimType = "mimeType";
	public static final String PARAM_token = "token";
	public static final String PARAM_appKey = "appKey";
	public static final String PARAM_custumerCode = "customerCode";
	public static final String PARAM_password = "password";
	public static final String PARAM_timeout =  "timeout";
	public static final String PARAM_exception = "exception";	
	public static final String PARAM_port = "port";
	public static final String PARAM_directoryForVideo = "dirVideo";
	public static final String PARAM_ip = "ip";
	public static final String PARAM_command = "command";
	public static final String PARAM_eTag ="eTag";
		
	String streamName;
	String mimType;
	String token;
	String appKey;
	String custumerCode;
	String password;
	
	long timeout =  Long.valueOf(1000 * 60 * 5); // 5 minutes.;
	Throwable exception;	
	int port;
	File directoryForVideo;
	InetAddress ip;
	String command;
	String eTag;
	
	
	public static MediaStreamParameters parsParamsFromHttpFirstLine(String strHttpFirstLine) throws Throwable{
		MediaStreamParameters paramMap = new MediaStreamParameters();
			
		String[] requestParam = strHttpFirstLine.split(" ");
		requestParam = requestParam[1].split("\\?");
		requestParam = requestParam[1].split("&");

		for(String param : requestParam){
			String[] keyVal = param.split("=");

			if(keyVal[0].equals(PARAM_streamName)) paramMap.setStreamName(keyVal[1]); //
			else if(keyVal[0].equals(PARAM_mimType)) paramMap.setMimType(keyVal[1]); //mimeType";
			else if(keyVal[0].equals(PARAM_token)) paramMap.setToken(keyVal[1]); //token";
			else if(keyVal[0].equals(PARAM_appKey)) paramMap.setAppKey(keyVal[1]); //appKey";
			else if(keyVal[0].equals(PARAM_custumerCode)) paramMap.setCustumerCode(keyVal[1]); //customerCode";
			else if(keyVal[0].equals(PARAM_password)) paramMap.setPassword(keyVal[1]); //password";
			else if(keyVal[0].equals(PARAM_timeout)) paramMap.setTimeout(Long.parseLong(keyVal[1])); //password";
			else if(keyVal[0].equals(PARAM_exception)) paramMap.setException(new Exception(keyVal[1])); //exception";	
			else if(keyVal[0].equals(PARAM_port)) paramMap.setPort(Integer.parseInt(keyVal[1])); //port";
			else if(keyVal[0].equals(PARAM_directoryForVideo)) paramMap.setDirectoryForVideo(null); //dirVideo";
			else if(keyVal[0].equals(PARAM_ip)) paramMap.setIp(InetAddress.getByName(keyVal[1])); //ip";
			else if(keyVal[0].equals(PARAM_command)) paramMap.setCommand(keyVal[1]); //command";
			else if(keyVal[0].equals(PARAM_eTag)) paramMap.seteTag(keyVal[1]); //
		
		}
		

		return paramMap;
	}
	
	public static MediaStreamParameters jsonToParams(Reader jsonData) throws Exception{
		Gson gson = new Gson();
		JsonObject json = gson.fromJson(jsonData, JsonObject.class);

		MediaStreamParameters paramMap = new MediaStreamParameters();
		for(Entry<String,JsonElement> entry : json.entrySet()){
			if(entry.getKey().equals(PARAM_streamName)) paramMap.setStreamName(entry.getValue().getAsString()); //
			else if(entry.getKey().equals(PARAM_mimType)) paramMap.setMimType(entry.getValue().getAsString()); //mimeType";
			else if(entry.getKey().equals(PARAM_token)) paramMap.setToken(entry.getValue().getAsString()); //token";
			else if(entry.getKey().equals(PARAM_appKey)) paramMap.setAppKey(entry.getValue().getAsString()); //appKey";
			else if(entry.getKey().equals(PARAM_custumerCode)) paramMap.setCustumerCode(entry.getValue().getAsString()); //customerCode";
			else if(entry.getKey().equals(PARAM_password)) paramMap.setPassword(entry.getValue().getAsString()); //password";
			else if(entry.getKey().equals(PARAM_timeout)) paramMap.setTimeout(Long.parseLong(entry.getValue().getAsString())); //password";
			else if(entry.getKey().equals(PARAM_exception)) paramMap.setException(new Exception(entry.getValue().getAsString())); //exception";	
			else if(entry.getKey().equals(PARAM_port)) paramMap.setPort(Integer.parseInt(entry.getValue().getAsString())); //port";
			else if(entry.getKey().equals(PARAM_directoryForVideo)) paramMap.setDirectoryForVideo(null); //dirVideo";
			else if(entry.getKey().equals(PARAM_ip)) paramMap.setIp(InetAddress.getByName(entry.getValue().getAsString())); //ip";
			else if(entry.getKey().equals(PARAM_command)) paramMap.setCommand(entry.getValue().getAsString()); //command";
			else if(entry.getKey().equals(PARAM_eTag)) paramMap.seteTag(entry.getValue().getAsString()); //
		}
		return paramMap;
	}	
	
	public String getStreamName() {
		return streamName;
	}
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}
	public String getMimType() {
		return mimType;
	}
	public void setMimType(String mimType) {
		this.mimType = mimType;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getCustomerCode() {
		return custumerCode;
	}
	public void setCustumerCode(String custumerCode) {
		this.custumerCode = custumerCode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public File getDirectoryForVideo() {
		return directoryForVideo;
	}
	public void setDirectoryForVideo(File directoryForVideo) {
		this.directoryForVideo = directoryForVideo;
	}
	public InetAddress getIp() {
		return ip;
	}
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String geteTag() {
		return eTag;
	}
	public void seteTag(String eTag) {
		this.eTag = eTag;
	}	
}
