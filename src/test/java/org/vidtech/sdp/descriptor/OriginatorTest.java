package org.vidtech.sdp.descriptor;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;
import org.vidtech.sdp.descriptor.Originator.AddressType;
import org.vidtech.sdp.descriptor.Originator.NetworkType;

@Test
public class OriginatorTest
{
	
	public void testCanBuildOriginatorWithMinimalData()
	{
		Originator o = Originator.builder()
						.withId("dave")
						.withVersion("bob")
						.withAddrType(AddressType.IP4)
						.withUnicastAddress("streaming.awesome.com")
						.build();
		
		assertEquals(o.getUsername(), "-", "username should be - if not specified.");
		assertEquals(o.getId(), "dave", "id should be set properly");
		assertEquals(o.getVersion(), "bob", "version should be set properly");
		assertEquals(o.getNetType(), NetworkType.IN, "network type should be set properly");
		assertEquals(o.getAddrType(), AddressType.IP4, "address type should be set properly");
		assertEquals(o.getUnicastAddress(), "streaming.awesome.com", "address should be set properly");
	}
	
	public void testCanBuildOriginatorWithFullData()
	{
		Originator o = Originator.builder()
				        .withUsername("fred")
						.withId("dave")
						.withVersion("bob")
						.withAddrType(AddressType.IP4)
						.withUnicastAddress("streaming.awesome.com")
						.build();
		
		assertEquals(o.getUsername(), "fred", "username should be set properly.");
		assertEquals(o.getId(), "dave", "id should be set properly");
		assertEquals(o.getVersion(), "bob", "version should be set properly");
		assertEquals(o.getNetType(), NetworkType.IN, "network type should be set properly");
		assertEquals(o.getAddrType(), AddressType.IP4, "address type should be set properly");
		assertEquals(o.getUnicastAddress(), "streaming.awesome.com", "address should be set properly");
	}
	
	
}