/*
 * #%L
 * SCIFIO tutorials for core and plugin use.
 * %%
 * Copyright (C) 2011 - 2014 Open Microscopy Environment:
 * 	- Board of Regents of the University of Wisconsin-Madison
 * 	- Glencoe Software, Inc.
 * 	- University of Dundee
 * %%
 * To the extent possible under law, the SCIFIO developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 * 
 * See the CC0 1.0 Universal license for details:
 * http://creativecommons.org/publicdomain/zero/1.0/
 * #L%
 */

package io.scif.tutorials;

import io.scif.Checker;
import io.scif.Format;
import io.scif.FormatException;
import io.scif.Metadata;
import io.scif.Parser;
import io.scif.Reader;
import io.scif.SCIFIO;

import java.io.IOException;

/**
 * Demonstrates how individual components can be used together instead of the
 * convenience method in IntroToSCIFIO.
 * 
 * @author Mark Hiner
 */
public class T2aUntypedComponents {

	public static void main(final String... args) throws FormatException,
		IOException
	{
		// In IntroToSCIFIO we used a convenience method to obtain an initialized
		// reader. This glossed over the individual steps of opening an image, which
		// can also be accomplished manually through the SCIFIO components.

		// As always, we create a context and sample image path first.
		final SCIFIO scifio = new SCIFIO();
		final String sampleImage =
			"8bit-signed&pixelType=int8&lengths=50,50,3,5,7&axes=X,Y,Z,Channel,Time.fake";

		// This time we'll get a handle on the Format itself, which will allow us
		// to create the additional components.
		final Format format = scifio.format().getFormat(sampleImage);

		// If we had been given a Format instead of the context, we can manually
		// check it's compatibility through a Checker component.
		final Checker checker = format.createChecker();
		System.out.println("Is compatible: " + checker.isFormat(sampleImage));

		// Typically the first thing we want to do, after confirming we have a
		// Format that can support an image, is parse the Metadata of that image.
		final Parser parser = format.createParser();
		final Metadata meta = parser.parse(sampleImage);

		// Metadata is used by other components, such as Readers, Writers, and
		// Translators to open, save, and convert -- respectively -- image
		// information. Assuming we're going to open an image, we'll need to
		// initialize a reader now.
		Reader reader = format.createReader();

		// Tells the reader which metadata object to use while reading.
		reader.setMetadata(meta);

		// Tells the reader which image source to read from.
		reader.setSource(sampleImage);

		// It is important to note that by using components all originating from
		// a single Format instance, we can be sure that these components are
		// compatible with each other.
		// A method that accepted multiple individual components and expected them
		// to be compatible may not be particularly useful. But note that it would
		// also be unnecessary -- any component can find its parent Format:

		// Both of these paths lead to the same Format, and will create a Reader
		// capable of reading the parsed Metadata.
		reader = parser.getFormat().createReader();
		reader =
			scifio.format().getFormatFromParser(parser.getClass()).createReader();

		// Unlike Formats within each context, component are never singletons and
		// thus we must re-initialize the reader.
		reader.setMetadata(meta);
		reader.setSource(sampleImage);

		// At this point we've caught up to the IntroToSCIFIO, and could begin
		// opening planes with our reader.
	}
}
