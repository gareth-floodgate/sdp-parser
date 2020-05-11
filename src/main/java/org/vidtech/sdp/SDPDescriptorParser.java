package org.vidtech.sdp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.vidtech.sdp.descriptor.Originator;
import org.vidtech.sdp.descriptor.Originator.AddressType;
import org.vidtech.sdp.descriptor.Originator.NetworkType;
import org.vidtech.sdp.descriptor.SDPDescriptor;

/**
 * A class that is responsible for parsing input data that represents 
 * an SDP descriptor as per RFC 4566. 
 */
public class SDPDescriptorParser 
{

	/** The allowed type values. */
	private static String VALID_TYPE_CHARACTERS = "vosiuepcbzkatrm";
	
	/** The parser states. */
	private static enum PARSE_STATE { GENERAL, TIME, MEDIA };
	
	
	/**
	 * Internal class for parse state mapping.
	 */
	private static class TypeStateMapping
	{
		private Character type;
		private boolean mandatory;
		private PARSE_STATE tranisiton;

		public TypeStateMapping(final Character type, final boolean mandatory, final PARSE_STATE transition)
		{
			this.type = type;
			this.mandatory = mandatory;
			this.tranisiton = transition;
		}

		public Character getType() 
		{
			return type;
		}

		public boolean isMandatory() 
		{
			return mandatory;
		}

		public PARSE_STATE getTranisiton() 
		{
			return tranisiton;
		}
	}
	
	
	/** The collection of types that are allowed. */
	private static Set<Character> ALLOWED_TYPES = new HashSet<>();
	
	/** The parsing map. */
	private static Map<PARSE_STATE, List<TypeStateMapping>> PARSER_STATES_AND_TRANSITIONS = new HashMap<>();
	
	
	static
	{
		// Build the set of allowed types.
		ALLOWED_TYPES.addAll(
		    VALID_TYPE_CHARACTERS.chars()
		    	.mapToObj(x -> Character.valueOf((char) x))
		        .collect(Collectors.toSet())
		);
		
		// Add transitions for general state. 
		PARSER_STATES_AND_TRANSITIONS.put(PARSE_STATE.GENERAL, 
				Arrays.asList( 
						new TypeStateMapping(Character.valueOf('v'), true, PARSE_STATE.GENERAL),
						new TypeStateMapping(Character.valueOf('o'), true, PARSE_STATE.GENERAL),
					    new TypeStateMapping(Character.valueOf('s'), true, PARSE_STATE.GENERAL)
			)
		);
		
	}

	
	// status
	//map state -> list locator
	
	

	private PARSE_STATE currentState = PARSE_STATE.GENERAL;
	
	private int currentstep = 0;
	

	
	

	// map state -> list < type, card, state >
	
	
	
	/**
	 * Parse the SDP descriptor as per specification.
	 * https://tools.ietf.org/html/rfc4566#page-6 - Section 5
	 * 
	 * NB: All invalid lines are ignored and silently dropped as per specification
	 * but any out of order, missing mandatory, or invalid content lines
	 * are found, the parser will throw an IllegalStateException
	 * 
	 * @param in The reader to extract data from.
	 * @return An SDPDescriptor object for the given data.
	 * 
	 * @throws IOException If there is an I/O error during the read.
	 * @throws IllegalStateException if the data is invalid wrt. specification.
	 */
	public SDPDescriptor parse(final Reader in)
	throws IOException
	{
		try ( final BufferedReader r = new BufferedReader(in); )
		{
			return parseSDP(r).build();
		}
	}
	
	
	/**
	 * Parse the SDP descriptor as per specification.
	 * https://tools.ietf.org/html/rfc4566#page-6 - Section 5
	 * 
	 * NB: All invalid lines are ignored and silently dropped as per specification
	 * but any out of order, missing mandatory, or invalid content lines
	 * are found, the parser will throw an IllegalStateException
	 * 
	 * @param r The buffered reader to extract data from.
	 * @return An SDPDescriptor object for the given data.
	 * 
	 * @throws IOException If there is an I/O error during the read.
	 * @throws IllegalStateException if the data is invalid wrt. specification.
	 */
	private SDPDescriptor.Builder parseSDP(final BufferedReader r)
	throws IOException
	{
		final SDPDescriptor.Builder builder = SDPDescriptor.builder();

		String line = null;
		while ( (line = r.readLine()) != null )
		{
			if (isValidLine(line))
			{
				final char type = line.charAt(0);
				TypeStateMapping mapping = null;
				
				// Determine if this line is valid in the context of the parse.
				if ((mapping = moveMarkerAndGetMappingForType(type)) != null)
				{
					// The current line type is valid and in-order, so parse it.
					final String remainingLine = line.substring(2);
					switch (type)
					{
						case 'v': 
						{
							// version check.
							try
							{
								if (Integer.parseInt(remainingLine) != 0)
								{
									throw new IllegalStateException("Unexpected or incorrect version - should be v=0");
								}								
							}
							catch (NumberFormatException nfe)
							{
								throw new IllegalStateException("invalid version - should be v=0");
							}

							break; 
						}
						case 'o': 
						{ 
							// Parse the originator content
							builder.withOriginator(parseOriginator(remainingLine).build());
							break; 
						}
						case 's': 
						{
							// Session name is merely text
							builder.withSessionName(remainingLine);
							break; 
						}
						
						default: 
						{ 
							break; 
						}
					}
				}
			}
			else
			{
				// This is not a valid line for SDP, so skip it.
				continue;
			}
		}
		
		return builder;
	}


	/**
	 * Parse the SDP originator line (o=.....) as per specification
	 * https://tools.ietf.org/html/rfc4566#page-10 - section 5.2
	 * 
	 * @param line The line data to parse (without o=).
	 * @return A builder object populated with originator data.
	 * 
	 * @throws IllegalStateException if the data is invalid wrt. specification.
	 */
	private Originator.Builder parseOriginator(final String line) 
	{
		final Originator.Builder builder = Originator.builder();

		String[] parts = line.split("[ \t]+");
		if (parts.length != 6)
		{
			// https://tools.ietf.org/html/rfc4566#page-10 - section 5.2
			throw new IllegalStateException("Originator entry MUST contain 6 parts.");  
		}
		if (!NetworkType.IN.toString().equals(parts[3]))
		{
			// https://tools.ietf.org/html/rfc4566#page-10 - section 5.2
			throw new IllegalStateException("nettype MUST equal IN.");  
		}
		if (!AddressType.IP4.toString().equals(parts[4]) && 
			!AddressType.IP6.toString().equals(parts[4]))
		{
			// https://tools.ietf.org/html/rfc4566#page-10 - section 5.2
			throw new IllegalStateException("addrtype MUST equal IP4 or IP6.");  
		}

		builder.withUsername(parts[0]);
		builder.withId(parts[1]);
		builder.withVersion(parts[2]);
		builder.withAddrType(AddressType.valueOf(parts[4]));
		builder.withUnicastAddress(parts[5]);
		
		return builder;
	}

	
	

	private TypeStateMapping moveMarkerAndGetMappingForType(final char type) 
	{
		List<TypeStateMapping> possibles = PARSER_STATES_AND_TRANSITIONS.get(currentState);
		TypeStateMapping current = possibles.get(currentstep);
		
		if (!current.getType().equals(Character.valueOf(type)))
		{
			if (current.isMandatory())
			{
				// The current step is mandatory, but the current line is mismatched.
				throw new IllegalStateException("Missing mandatory line from SDP, " + current.getType() + "=");
			}
			
			TypeStateMapping next = null;
			int lookahead = currentstep;

			// loop until next found - or len of list reached
			while (++lookahead < possibles.size())
			{
				next = possibles.get(lookahead);
				if (next.getType().equals(Character.valueOf(type)))
				{
					currentstep = lookahead;
					break;
				}
			}
			
			return next;
		}
		else
		{
			currentstep++;
			return current;
		}
	}


//	private boolean isValidEntryForStateAndLocation(final char type) 
//	{
//		boolean isValid = false;
//		List<TypeStateMapping> possibles = PARSER_STATES_AND_TRANSITIONS.get(currentState);
//		TypeStateMapping next = possibles.get(currentstep);
//		
//		if (!next.getType().equals(Character.valueOf(type)))
//		{
//			int lookahead = currentstep;
//
//				// loop until next found - or len of list reached
//				while (++lookahead < possibles.size())
//				{
//					next = possibles.get(lookahead);
//					if (next.getType().equals(Character.valueOf(type)))
//					{
//						isValid = true;
//						break;
//					}
//				}
//			
//			
//		}
//		
//		return isValid;
//	}
	
	
	/**
	 * A helper method to determine if a line meets the minimum requirements 
	 * for being considered as valid SDP.
	 * 
	 * @param line The string to test.
	 * @return true if this is basically SDP valid, false otherwise.
	 */
	private boolean isValidLine(final String line)
	{
		// A valid line is at least 3 characters, type=value as per RFC 4566
		if (line.length() >= 3)
		{
			final char type = line.charAt(0);
			if (line.charAt(1) == '=' && ALLOWED_TYPES.contains(Character.valueOf(type)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	
}
