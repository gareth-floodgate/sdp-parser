package org.vidtech.sdp.descriptor;

/**
 * The SDP descriptor file.
 *
 */
public class SDPDescriptor 
{

	
	private final Originator originator;
	
	private final String sessionName;


	private SDPDescriptor(Builder builder) {
		this.originator = builder.originator;
		this.sessionName = builder.sessionName;
	}

	
//	private final String sessionTitle;
	
//	private final String uri;
	
	
	
	public Originator getOriginator()
	{
		return originator;
	}
	
	public String getSessionName()
	{
		return sessionName;
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

		public SDPDescriptor build() {
			return new SDPDescriptor(this);
		}
	}
	
	
	

}
