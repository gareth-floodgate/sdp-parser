package org.vidtech.sdp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.vidtech.sdp.descriptor.SDPDescriptor;

import com.google.common.base.Charsets;

@Test
public class SDPReadWriteTest
{
	
	// max depth for file searching.
	private static int MAX_DEPTH = 20;
	
	
	@DataProvider
	public Object[][] successFiles()
	{
	
		final List<Path> paths = new ArrayList<>();;
		
		// find all the files relative to the success location.
		final Path source = Paths.get("src","test","resources","sdp","examples","should-pass");
		try
		( 
		    final Stream<Path> locations = Files.find(source, MAX_DEPTH, 
				                     (p,a) -> a.isRegularFile() 
				                           && p.toString().endsWith(".sdp") 
				                           && !p.toString().endsWith("-expected.sdp")) 
		)
		{
			paths.addAll(locations.collect(Collectors.toList()));
		}
		catch (IOException ie)
		{
			fail("Unexpected IO error");
		}
		
		// Create 2d array
		Object[][] data = new Object[paths.size()][2];
		int i = 0;
		
		// for each file, also add -execpted.sdp
		for (Path p : paths)
		{
			final String rawName = p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4);
			
			Object[] row = new Object[2];
			row[0] = p;
			row[1] = p.getParent().resolve(Paths.get(rawName + "-expected.sdp"));
			data[i++] = row;
		};
		
		return data;
	}
	
	
	
	@Test(dataProvider="successFiles")
	public void testCanReadAndWriteSDPDescriptorsThatShouldSucceed(final Path source, final Path expected)
	{
		try
		(
		    Reader srcIn = new FileReader(source.toFile(), Charsets.UTF_8); 
			StringWriter w = new StringWriter();
		)
		{
			// Parse SDP
			SDPDescriptor sdp = SDPUtils.read(srcIn);
		
			// write SDP
			SDPUtils.write(sdp, w);
			
			// Assert that what was now written is the same as that read.
			assertEquals(w.toString(), Files.readString(expected, Charsets.UTF_8), "Unexpected difference between source and expected");
		}
		catch (IOException e)
		{
			fail("Unexpected IO error", e);
		}
	}
	
	
	@DataProvider
	public Object[][] failFiles()
	{
	
		final List<Path> paths = new ArrayList<>();;
		
		// find all the files relative to the success location.
		final Path source = Paths.get("src","test","resources","sdp","examples","should-fail");
		try
		( 
		    final Stream<Path> locations = Files.find(source, MAX_DEPTH, 
				                     (p,a) -> a.isRegularFile() 
				                           && p.toString().endsWith(".sdp")) 
		)
		{
			paths.addAll(locations.collect(Collectors.toList()));
		}
		catch (IOException ie)
		{
			fail("Unexpected IO error");
		}
		
		// Create 2d array
		Object[][] data = new Object[paths.size()][2];
		int i = 0;
		
		// for each file, also add -execpted.sdp
		for (Path p : paths)
		{
			final String rawName = p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4);
			
			Object[] row = new Object[2];
			row[0] = p;
			row[1] = p.getParent().resolve(Paths.get(rawName + "-expected.err"));
			data[i++] = row;
		};
		
		return data;
	}
	
	
	@Test(dataProvider="failFiles")
	public void testCorrectlyFailsToReadSDPDescriptorsThatShouldFail(final Path source, final Path expected)
	{
		try
		(
		    Reader srcIn = new FileReader(source.toFile(), Charsets.UTF_8); 
		)
		{
			// Parse SDP
			SDPUtils.read(srcIn);
	
			// Should cause exception.
			fail("Unexpected failure - EXPECTED exception but got none ...");
		}
		catch (Exception t)
		{
			try
			{
				final String[] parts = Files.readString(expected, Charsets.UTF_8).split("=", 2);
				
				assertEquals(t.getClass().getSimpleName(), parts[0], "Unexpected exception");
				assertEquals(t.getMessage(), parts[1], "Unexpected exception message");
			}
			catch (IOException e)
			{
				fail("Unexpected IO error");
			}
		}
	}
	
}