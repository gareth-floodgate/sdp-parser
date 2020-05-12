package org.vidtech.sdp;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.vidtech.sdp.descriptor.Originator;
import org.vidtech.sdp.descriptor.SDPDescriptor;

/**
 * Utility methods for reading (parsing) and writing SDP descriptors.
 */
public class SDPUtils 
{
	
	/** Constant string for new line (windows format. */
	private static final String CRLF = "\r\n";

	
	/**
	 * Private Constructor, no class instances needed.
	 */
	private SDPUtils() { }
	
	
	/**
	 * Write an SDP descriptor in RFC 4566 compliant output.
	 * 
	 * @param sdp A valid SDPDescriptor instance to write.
	 * @param out The writer to write to.
	 * @throws IOException If there is a problem writing the data.
	 */
	public static void write(final SDPDescriptor sdp, final Writer out)
	throws IOException
	{
		StringBuilder b = new StringBuilder();

		out.write("v=0" + CRLF);
		
		final Originator o = sdp.getOriginator();
		b.append("o=").append(o.getUsername())
			.append(" ").append(o.getId())
			.append(" ").append(o.getVersion())
			.append(" ").append(o.getNetType())
			.append(" ").append(o.getAddrType())
			.append(" ").append(o.getUnicastAddress());
		out.write(b.toString() + CRLF);

		out.write("s=" + sdp.getSessionName() + CRLF);
		
		if (sdp.getSessionInfo().isPresent()) { out.write("i=" + sdp.getSessionInfo().get() + CRLF); }
	}
	
	
	/**
	 * Read a RFC 4566 data stream into an SDP descriptor 
	 * 
	 * @param in The reader to read the SDP from.
	 * @return A valid SDPDescriptor instance.
	 * 
	 * @throws IOException If there is a problem reading the data.
	 * @throws IllegalStateException If there is a problem with the data validity.
	 */
	public static SDPDescriptor read(final Reader in) 
	throws IOException
	{	
		return new SDPDescriptorParser().parse(in);
	}
	
}
