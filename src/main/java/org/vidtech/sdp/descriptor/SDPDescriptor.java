package org.vidtech.sdp.descriptor;

import java.net.URI;
import java.util.Optional;

/**
 * The SDP descriptor.
 * 
 * See https://tools.ietf.org/html/rfc4566#page-10
 * 5.
 */
public class SDPDescriptor 
{
	// Version is always 0, v=0 - not stored as data in object.
	
	/** The originator record for this descriptor. */
	private final Originator originator;
	
	/** The session name. */
	private final String sessionName;

	/** The (optional) session info. */
	private final Optional<String> sessionInfo;
	
	/** The (optional) session description (URI). */
	private final Optional<URI> sessionDescription;
	
	
	/**
	 * Private constructor to instantiate a SDPDescriptor from a builder object.
	 * NB: Data validation occurs inside the builder instance, not here.
	 * 
	 * @param builder The builder instance to create an SDP descriptor from.
	 */
	private SDPDescriptor(final Builder builder) 
	{
		this.originator = builder.originator;
		this.sessionName = builder.sessionName;
		this.sessionInfo = Optional.ofNullable(builder.sessionInfo);
		this.sessionDescription = builder.sessionDescription == null ? Optional.empty() : Optional.of(URI.create(builder.sessionDescription));
	}



	
	public Originator getOriginator()
	{
		return originator;
	}
	
	public String getSessionName()
	{
		return sessionName;
	}
	
	public Optional<String> getSessionInfo()
	{
		return sessionInfo;
	}
	
	public Optional<URI> getSessionDescription()
	{
		return sessionDescription;
	}
	
	
	/**
	 * Creates builder to build {@link SDPDescriptor}.
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link SDPDescriptor}.
	 */
	public static final class Builder {
		private Originator originator;
		private String sessionName;
		private String sessionInfo;
		private String sessionDescription;

		private Builder() {
		}

		
		public Builder withOriginator(Originator originator) {
			this.originator = originator;
			return this;
		}

		public Builder withSessionName(String sessionName) {
			this.sessionName = sessionName;
			return this;
		}

		public Builder withSessionInfo(String sessionInfo) {
			this.sessionInfo = sessionInfo;
			return this;
		}
		
		public Builder withSessionDescription(String sessionDescription) {
			this.sessionDescription = sessionDescription;
			return this;
		}
				
		public SDPDescriptor build() {
			validate();

			return new SDPDescriptor(this);
		}
		
		private void validate()
		{
			if (originator == null) 				{ throw new IllegalStateException("originator value not set"); }
			if (sessionName == null)	 			{ throw new IllegalStateException("session name value not set"); }
			try 
			{ 
				if (sessionDescription != null) URI.create(sessionDescription); 
			} 
			catch (IllegalArgumentException e) {  throw new IllegalStateException("session description invalid"); }
		}
	}
	
	
	

}
