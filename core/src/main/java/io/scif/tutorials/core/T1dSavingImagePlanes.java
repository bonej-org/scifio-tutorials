/*
 * #%L
 * SCIFIO tutorials for core and plugin use.
 * %%
 * Copyright (C) 2011 - 2016 Open Microscopy Environment:
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

package io.scif.tutorials.core;

import io.scif.FormatException;
import io.scif.Reader;
import io.scif.SCIFIO;
import io.scif.Writer;
import io.scif.io.Location;

import java.io.IOException;

/**
 * Tutorial demonstrating use of the Writer component.
 * 
 * @author Mark Hiner
 */
public class T1dSavingImagePlanes {

	public static void main(final String... args) throws FormatException,
		IOException
	{
		// In this tutorial, we're going to make our .fake sample image
		// "real". If you look at the FakeFormat source code, you'll notice that
		// it doesn't have a functional Writer, so we'll have to translate
		// to a different Format that can write our fake planes to disk.

		final SCIFIO scifio = new SCIFIO();
		final String sampleImage =
			"8bit-signed&pixelType=int8&lengths=50,50,3,5,7&axes=X,Y,Z,Channel,Time.fake";

		// We'll need a path to write to. By making it a ".png" we are locking in
		// the final format of the file on disk.
		final String outPath = "SCIFIOTutorial.png";

		// Clear the file if it already exists.
		final Location l = new Location(scifio.getContext(), outPath);
		if (l.exists()) l.delete();

		// We'll need a reader for the input image
		final Reader reader = scifio.initializer().initializeReader(sampleImage);

		// .. and a writer for the output path
		final Writer writer =
			scifio.initializer().initializeWriter(sampleImage, outPath);

		// Note that these initialize methods are used for convenience.
		// Initializing a reader and a writer requires that you set the source
		// and metadata properly. Also note that the Metadata attached to a writer
		// describes how to interpret the incoming Planes, but may not reflect
		// the image on disk - e.g. if planes were saved in a different order
		// than on the input image. For accurate Metadata describing the saved
		// image, you need to re-parse it from disk.

		// Anyway, now that we have a reader and a writer, we can save all the
		// planes. We simply iterate over each image, and then each plane, writing
		// the planes out in order.
		for (int i = 0; i < reader.getImageCount(); i++) {
			for (int j = 0; j < reader.getPlaneCount(i); j++) {
				writer.savePlane(i, j, reader.openPlane(i, j));
			}
		}
		// Note that this code is for illustration purposes only.
		// A more general solution would need higher level API that could account
		// for larger planes, etc..

		// close our components now that we're done. This is a critical step, as
		// many formats have footer information that is written when the writer is
		// closed.
		reader.close();
		writer.close();
		scifio.getContext().dispose();

		// That's it! There should be a new SCIFIOTutorial image in whichever
		// directory you ran this tutorial from.
	}
}
