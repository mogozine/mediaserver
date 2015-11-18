package com.mgz.mediaserver.ebml;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;


public enum EBMLDataType{
    EBMLSignedInteger, // Signed Integer - Big-endian, any size from 1 to 8 octets
    EBMLUnsignedInteger, // Unsigned Integer - Big-endian, any size from 1 to 8 octets
    EBMLFloat, // Float - Big-endian, defined for 4 and 8 octets (32, 64 bits)
    EBMLString, // String - Printable ASCII (0x20 to 0x7E), zero-padded when needed
    EBMLUTF8, // UTF-8 - Unicode string, zero padded when needed (RFC 2279)
    EBMLDate, // Date - signed 8 octets integer in nanoseconds with 0 indicating the precise beginning of the millennium (at 2001-01-01T00:00:00,000000000 UTC)
    EBMLMasterElement, // Master-Element - contains other EBML sub-elements of the next lower level
    EBMLBinary, // Binary - not interpreted by the parser
    
    Undefined;

    public Object decode(byte[] payload){
    	Object obj = null;
    	if(this==EBMLSignedInteger){
    		long i = 0;
    		for(byte b : payload) {i<<=8; i += (b & 0xFF);}
    		if(payload.length<8){
    			i<<=(8*(8-payload.length));
    			i>>=(8*(8-payload.length));
    		}
    		obj = Long.valueOf(i);
    	}else if(this==EBMLUnsignedInteger){
    		BigInteger bigI = new BigInteger(1, payload);
    		obj = bigI;
    	}else if(this == EBMLFloat){
    		if(payload.length==4) obj = Float.valueOf(ByteBuffer.wrap(payload).asFloatBuffer().get());
    		else if(payload.length==8) obj = Double.valueOf(ByteBuffer.wrap(payload).asDoubleBuffer().get());
    		else obj = Float.NaN;
    		
    	}else if(this==EBMLString){
    		obj = new String(payload);
    	}else if(this==EBMLUTF8){
    		try {
				obj = new String(payload,"utf-8");
			} catch (UnsupportedEncodingException e) {
				obj = new String(payload);
			}
    	}else if(this==EBMLDate){
    		long i = 0;
    		for(byte b : payload) {i<<=8; i += b;}
    		obj = Long.valueOf(i);
    		
    	}else if(this==EBMLMasterElement){
    		obj = null;
    	}else if(this==EBMLBinary){
    		obj = payload;
    	}
    	
    	
    	return obj;
    }
}

