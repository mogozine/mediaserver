package com.mgz.mediaserver;

import java.io.IOException;
import java.io.OutputStream;

public interface IMediaChunk {
	/**
	 * Writes this {@link IMediaChunk} to given {@link OutputStream}.
	 * @param os {@link OutputStream} to write to.
	 * @return length of written data in bytes.
	 * @throws IOException if writing fails.
	 */
	public long write(OutputStream os) throws IOException;
}
