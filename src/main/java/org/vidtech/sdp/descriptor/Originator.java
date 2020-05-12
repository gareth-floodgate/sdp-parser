package org.vidtech.sdp.descriptor;

/**
 * The SDP originator field.
 * 
 * See https://tools.ietf.org/html/rfc4566#page-11 
 * 5.2.  Origin ("o=")
 *
 */
public class Originator 
{
	
	/** The user name. */
	private final String username;
	
	/** The session identifier. */
	private final String id;
	
	/** The session version. */
	private final String version;
	
	/** The network type. */
	private final NetworkType netType;
	
	/** The address type. */
	private final AddressType addrType;
	
	/** The network address of the source. */
	private final String unicastAddress;


	private Originator(final Builder builder) 
	{
		this.username = builder.username;
		this.id = builder.id;
		this.version = builder.version;
		this.netType = builder.netType;
		this.addrType = builder.addrType;
		this.unicastAddress = builder.unicastAddress;
	}
	
	
	
	
	


	public String getUsername() {
		return username;
	}


	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}


	public NetworkType getNetType() {
		return netType;
	}


	public AddressType getAddrType() {
		return addrType;
	}


	public String getUnicastAddress() {
		return unicastAddress;
	}







	/**
	 * Creates builder to build {@link Originator}.
	 * @return created builder
	 */
	public static Builder builder() 
	{
		return new Builder();
	}


	/**
	 * Builder to build {@link Originator}.
	 */
	public static final class Builder {
		private String username;
		private String id;
		private String version;
		private NetworkType netType = NetworkType.IN;
		private AddressType addrType;
		private String unicastAddress;

		private Builder() {
		}

		public Builder withUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withVersion(String version) {
			this.version = version;
			return this;
		}


		public Builder withAddrType(AddressType addrType) {
			this.addrType = addrType;
			return this;
		}

		public Builder withUnicastAddress(String unicastAddress) {
			this.unicastAddress = unicastAddress;
			return this;
		}

		public Originator build() 
		{
			if (username == null)
			{
				username = "-";
			}
			
			validate();
			
			return new Originator(this);
		}
		
		private void validate()
		{
			if (id == null) 			{ throw new IllegalStateException("id value not set"); }
			if (version == null)	 	{ throw new IllegalStateException("version value not set"); }
			if (addrType == null) 		{ throw new IllegalStateException("address-type value not set"); }
			if (unicastAddress == null)	{ throw new IllegalStateException("address value not set"); }
		}
		
	}
	
	
	
	/**
	 * rfc4566 defined network types enumeration.
	 */
	public enum NetworkType
	{
		/** Currently only defined as "Internet" */
		IN
	}

	
	/**
	 * rfc4566 defined address types enumeration.
	 */
	public enum AddressType
	{
		/** Currently only defined as "IP4" and "IP6" */
		IP4, IP6
	}

}
