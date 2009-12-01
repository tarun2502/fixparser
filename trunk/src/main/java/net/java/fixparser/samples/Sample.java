package net.java.fixparser.samples;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import net.java.fixparser.SimpleFixMessage;
import net.java.fixparser.SimpleFixParser;
import net.java.util.IoUtils;

/**
 * FIX Message Parser sample.
 * 
 * @author Leonid Shlyapnikov
 */
public class Sample {

    public static void main(String argv[]) {

        final File file = getFile(argv);

        System.out.println("Parsing file: " + file);

        long elapsedTime = -1;
        long numOfMessages = 0;

        InputStream inputStream = null;

        try {
            // initialize an input stream
            inputStream = new BufferedInputStream(new FileInputStream(file));

            // initialize the parser
            SimpleFixParser parser = new SimpleFixParser(inputStream);

            final long t0 = System.currentTimeMillis();

            // main loop
            while (true) {

                // parse a FIX Message
                SimpleFixMessage msg = parser.readFixMessage();

                if (null == msg) {
                    elapsedTime = System.currentTimeMillis() - t0;
                    break; // exit the loop, end of the stream reached
                }

                numOfMessages++;

                // process FIX message
                // System.out.println(msg);
            }
        } catch (Exception e) {
            // handle exception
            e.printStackTrace();
        } finally {
            // close the stream if it is not null
            IoUtils.closeSilently(inputStream);
        }

        // print statistics
        if (0 == numOfMessages) {
            System.out.println("No FIX messages were found/parsed.");
            System.exit(100);
        } else {
            System.out.println("Done.");
            System.out.println("Elapsed time, milliseconds: " + elapsedTime);
            System.out.println("Number of parsed FIX messages: " + numOfMessages);
            System.out.println("Milliseconds per message: " + (elapsedTime / (double) numOfMessages));
        }
    }

    private static File getFile(String argv[]) {
        if (1 != argv.length) {
            throw new IllegalArgumentException("Please specify FIX message file name");
        }

        final String fileName = argv[0];

        final File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException("File: " + file + " does  not exist");
        }

        return file;
    }
}
