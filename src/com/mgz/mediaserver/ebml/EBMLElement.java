
package com.mgz.mediaserver.ebml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.BitSet;

import com.mgz.mediaserver.IMediaChunk;

public class EBMLElement implements IMediaChunk{
	public static EBMLElement[] ARRTYPE = {};
	private byte[] elementTypeCode;
	private byte[] elementSizeCode;
	private byte[] elementData;
	private EBMLElementType elementType;

	/**
	 * Creates a {@link EBMLElement} of type {@link EBMLElementType#Timecode} with given timecode as inner value.
	 * @param timecode
	 * @return {@link EBMLElement} of type {@link EBMLElementType#Timecode} with given timecode as inner value.
	 */
	public static EBMLElement createTimeCodeEBMLElement(BigInteger timecode)  {
		byte[] payload = timecode.toByteArray();
		return new EBMLElement(EBMLElementType.Timecode.getElementTypeCode(), new byte[] {(byte)(0x80 + payload.length)}, payload);
	}

	
	
	/**
	 * Constructor to manually create an {@link EBMLElement}.
	 * To read in EBMLElements from a InputStream use the static method {@link EBMLElement#readNextElement(InputStream)}.
	 * @param elementTypeCode
	 * @param elementSizeCode
	 * @param elementData
	 */
	public EBMLElement(byte[] elementTypeCode, byte[] elementSizeCode, byte[] elementData) {
		this.elementTypeCode = elementTypeCode;
		this.elementSizeCode = elementSizeCode;
		this.elementData = elementData;
	}
	
	public EBMLElementType getElementType(){
		if(elementType!=null) return elementType;
		return elementType = EBMLElementType.valueOf(elementTypeCode);
		
	}
	
	/**
	 * Returns the size of this element as it is in bytes.<br>
	 * 	<code>
	 * 	if(elementTypeCode!=null) size += elementTypeCode.length;<br>
		if(elementSizeCode!=null) size += elementSizeCode.length;<br>
		if(elementData!=null) size += elementData.length;<br>
		<code>
	 * @return
	 */
	public int getActualSize(){
		int size = 0;
		if(elementTypeCode!=null) size += elementTypeCode.length;
		if(elementSizeCode!=null) size += elementSizeCode.length;
		if(elementData!=null) size += elementData.length;
		return size;
	}
	
	/**
	 * Creates a new {@link EBMLElement} and reads in the EBML type code and the EBML size code.
	 * However it doesn't read in  the element data.
	 * To explicitly read in the element data, call the method {@link EBMLElement#readElementData(Input Stream)}. 
	 *  
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static EBMLElement readNextElement(InputStream source) throws IOException{
		EBMLElement newElement = null;

		byte[] elementTypeCode = EBMLElement.readEBMLCodeAsIs(source);
		if(elementTypeCode==null) return null;
		byte[] elementSizeCode = EBMLElement.readEBMLCodeAsIs(source);
		if(elementSizeCode==null) return null;

		newElement = new EBMLElement(elementTypeCode,elementSizeCode,null);

		return newElement;
	}

	public int readElementData(InputStream source) throws IOException{
		if(elementSizeCode==null || isSizeUndefined()){
			throw new RuntimeException("EBML size code not set or element size is undefined.");
		}else{
			long originalsize = parseEBMLCodeToLong(elementSizeCode);
			
			if(originalsize>Integer.MAX_VALUE){
				throw new IOException("EBML element size to big.");
			}
			
			int size = (int) originalsize;
			elementData = (new byte[size]);
			int bytesToRead = size;
			int pos = 0;
			while(bytesToRead>0){
				int readBytes = source.read(elementData,pos,bytesToRead);
				if(readBytes<0){
					throw new IOException("Input stream closed before all EBML data has been read.");
				}else{
					bytesToRead -= readBytes;
					pos += readBytes;
				}
			}
			
			return size;
		}		
	}
	
	
	/**
	 *	Reads an EBML code from given InputStream.
	 *
	 * @return array of byte containing EBML code, size bits included untouched.
	 * @throws IOException 
	 */
	static private byte[] readEBMLCodeAsIs(InputStream source) throws IOException {
		byte firstByte = (byte) source.read();
		if (firstByte==-1) return null;


		int numBytes = countEBMLLengthBits(firstByte);
		byte[] code = new byte[numBytes];
		code[0] = (byte)((firstByte));
		if (numBytes > 1) {
			source.read(code, 1, numBytes - 1);
		}

		return code;
	}

	/**
	 * Returns true if the size of this elements data is undefined.
	 * @return true if the size of this elements data is undefined.
	 */
	public boolean isSizeUndefined(){
		if(elementSizeCode==null) return true;
		else{
			int a = BitSet.valueOf(elementSizeCode).cardinality() + countEBMLLengthBits(elementSizeCode[0]) -1;
			return a == elementSizeCode.length*8;
		}
	}

	/**
	 * Writes the Element as it is to the given OutputStream.
	 * It writes the element's type code, the elements size code and, if already read in, the element's data.
	 * 
	 * 
	 * @param writer
	 * @return the length of written data in bytes
	 * @throws IOException
	 */
	@Override
	public long write(OutputStream writer) throws IOException{
		writer.write(elementTypeCode);
		writer.write(elementSizeCode);

		long len = elementTypeCode.length + elementSizeCode.length;
		if(elementData!=null){
			writer.write(elementData);
			len+=elementData.length;
		}

		return len;
	}

	/**
	 * Returns the size of the element data.
	 * 
	 * @return size of the element data ("the payload") or -1 if the size of the data undefined.
	 */
	public long getDataSize() {
		if(elementSizeCode==null && elementData==null) throw new RuntimeException("No element size code set.");
		if(elementData==null){
			if(isSizeUndefined()) return -1;
			else return parseEBMLCodeToLong(elementSizeCode);
		}
		else return (long) elementData.length;
	}

	public static long parseEBMLCodeToLong(byte[] data) {
		if(data==null)
			return 0;

		int nrOfLengthBits = countEBMLLengthBits(data[0]);

		int size = 0;
		if(nrOfLengthBits<8){
			size = ((int)data[0]);
			size <<=  (Integer.SIZE - 8 + nrOfLengthBits);
			size >>>= (Integer.SIZE - 8 + nrOfLengthBits);
		}
		
		for (int i = 1; i < data.length; i++) {
			size <<=8;
			size += ((int)data[i]) & 0xFF;
		}

		return size;
	}


	private static int countEBMLLengthBits(byte firstByte){
		short mask = 0b10000000;

		int numBytes = 1;
		while ((firstByte & mask) == 0 && mask!=0) {
			numBytes++;
			mask >>= 1;
		}
		return numBytes;
	}



	public byte[] getElementTypeCode() {
		return elementTypeCode;
	}

	public void setElementTypeCode(byte[] elementTypeCode) {
		this.elementTypeCode = elementTypeCode;
		this.elementType = null;
	}

	public byte[] getElementSizeCode() {
		return elementSizeCode;
	}

	public void setElementSizeCode(byte[] elementSizeCode) {
		this.elementSizeCode = elementSizeCode;
	}

	public byte[] getElementData() {
		return elementData;
	}

	public void setElementData(byte[] elementData) {
		this.elementData = elementData;
	}

	public String toString(){
		EBMLElementType type = getElementType();
		if(type==null) return "null";
		else{
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<type.level; i++) sb.append("\t");
			sb.append(type.name())
			.append("(").append(type.getDataType().name());
			Object obj = this.getInnerValue();
			if(obj != null && !obj.getClass().isArray()){
				sb.append(" = ").append(obj);
			}
			sb.append(")")
			.append("[0x").append(bytesToHex(elementTypeCode))
			.append("][0x").append(bytesToHex(elementSizeCode)).append("] len = ")
			.append(getDataSize());
			
			return sb.toString();
		}
	}

	
	public Object getInnerValue() {
		if(this.elementData!=null){ 
			return getElementType().getDataType().decode(elementData);
		}
		return null;
	}


	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}


}
