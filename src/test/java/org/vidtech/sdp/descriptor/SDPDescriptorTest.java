package org.vidtech.sdp.descriptor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;
import org.vidtech.sdp.descriptor.Originator.AddressType;

@Test
public class SDPDescriptorTest
{
	
	public void testCanBuildSDPWithMinimalData()
	{
		SDPDescriptor s = 
				SDPDescriptor.builder()
				.withOriginator(
					Originator.builder()
							.withId("dave")
							.withVersion("bob")
							.withAddrType(AddressType.IP4)
							.withUnicastAddress("streaming.awesome.com")
							.build()
				)
				.withSessionName("alice")
				.build();
		
		assertNotNull(s.getOriginator(), "unexpected null originator");
		assertEquals(s.getSessionName(), "alice", "session name not set properly.");
	}
	
	public void testCanBuildSDPWithFullData()
	{
		SDPDescriptor s = 
				SDPDescriptor.builder()
				.withOriginator(
					Originator.builder()
							.withId("dave")
							.withVersion("bob")
							.withAddrType(AddressType.IP4)
							.withUnicastAddress("streaming.awesome.com")
							.build()
				)
				.withSessionName("alice")
				.withSessionInfo("emma")
				.build();
		
		assertNotNull(s.getOriginator(), "unexpected null originator");
		assertEquals(s.getSessionName(), "alice", "session name not set properly.");
		assertTrue(s.getSessionInfo().isPresent(), "session info not set properly.");
		assertEquals(s.getSessionInfo().get(), "emma", "session info not set properly.");
	}
	
	
	
	
	// todo validation
	
	// session name null ?
	//originator null ??
	
	
	
	
	
	
}