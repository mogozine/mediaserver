package com.mgz.mediaserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.mgz.mediaserver.ebml.EBMLElementType;


public class ApplicationMediaServer {
	public static final Integer TIMEOUT_MEDIASERVERTCP = 5000;
	public static final File DIR_VIDEOSTORE = new File("./videos/");
	public static final int HTTPPORT = 8080;

	public static void main(String[] args) throws Exception {
		EBMLElementType.prepareElementTypCode2ElementTypeMap();

		Map<String, Object> props = parseComandLineParams(args);

		new HttpServer((Integer)props.get("HTTPPORT"));

		if(props.containsKey("test")) {
			int port = Integer.parseInt((String)props.get("test_port"));
			MediaStreamManager.openTestStream(port);;
		}
	}

	private static Map<String, Object> parseComandLineParams(String[] args) {
		Map<String, Object> params = new HashMap<String, Object>();

		// Set default values:
		params.put("HTTPPORT", Integer.valueOf(HTTPPORT));
		params.put("DIR_VIDEOSTORE", DIR_VIDEOSTORE);

		String paramName,paramValue;
		int i=0;
		while(i<args.length){
			paramValue="";
			paramName = args[i].trim();
			while(++i<args.length && !args[i].startsWith("-")) paramValue+= (args[i] + " ");
			paramValue = paramValue.trim();

			if("-port".equals(paramName)){
				params.put("HTTPPORT", Integer.parseInt(paramValue));

			}else if("-content".equals(paramName)){
				File mediaStore = new File(paramValue);
				if(!mediaStore.exists()) mediaStore.mkdirs();
				params.put("DIR_VIDEOSTORE", mediaStore);

			}else if("-help".equals(paramName) || "--help".equals(paramName)){
				System.out.println("USAGE:\n\tjava -jar MediaServer.jar -port <portnr> -content <content_dir>\n");

			}else{
				if(paramName.contains("=")){
					params.put(paramName.substring(1,paramName.indexOf("=")), paramName.substring(paramName.indexOf("=")+1));
				}else{
					params.put(paramName.substring(1), paramValue);
				}
			}
		}

		return params;
	}






}
