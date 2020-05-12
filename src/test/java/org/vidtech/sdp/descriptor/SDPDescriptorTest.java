package org.vidtech.sdp.descriptor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;

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
		assertTrue(!s.getSessionInfo().isPresent(), "uri set incorrectly.");
		assertTrue(!s.getSessionDescription().isPresent(), "uri set incorrectly.");
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
				.withSessionDescription("www.awesome.com")
				.build();
		
		assertNotNull(s.getOriginator(), "unexpected null originator");
		assertEquals(s.getSessionName(), "alice", "session name not set properly.");
		assertTrue(s.getSessionInfo().isPresent(), "session info not set properly.");
		assertEquals(s.getSessionInfo().get(), "emma", "session info not set properly.");
		assertEquals(s.getSessionDescription().get(), URI.create("www.awesome.com"), "session info not set properly.");
	}
	
	
	public void testValidatesAndRejectsBadDataCorrectly()
	{
		try
		{
			// no originator
			SDPDescriptor.builder().build();
			fail("Exepcted exception");
		}
		catch (IllegalStateException e) { /* do nothing - expected. */ }
		try
		{
			// no session name
			SDPDescriptor.builder()
			.withOriginator(
					Originator.builder()
							.withId("dave")
							.withVersion("bob")
							.withAddrType(AddressType.IP4)
							.withUnicastAddress("streaming.awesome.com")
							.build())
			.build();
			fail("Exepcted exception");
		}
		catch (IllegalStateException e) { /* do nothing - expected. */ }
		try
		{
			// bad uri
			SDPDescriptor.builder()
			.withOriginator(
					Originator.builder()
							.withId("dave")
							.withVersion("bob")
							.withAddrType(AddressType.IP4)
							.withUnicastAddress("streaming.awesome.com")
							.build())
			.withSessionName("bob")
			.withSessionDescription("x|xx::::;;;;")
			.build();
			fail("Exepcted exception");
		}
		catch (IllegalStateException e) { /* do nothing - expected. */ }
		
	
	}
	
	
	
	
	
	
	
}