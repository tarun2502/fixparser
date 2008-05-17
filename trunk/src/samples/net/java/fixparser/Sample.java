package net.java.fixparser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
                    // end of the stream reached
                    elapsedTime = System.currentTimeMillis() - t0;
                    break;
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

        System.out.println("Done. Elapsed time, milliseconds: " + elapsedTime + "; number of messages: "
                + numOfMessages + "; milliseconds per message: " + (elapsedTime / (double) numOfMessages));

    }

    private static File getFile(String argv[]) {
        if (1 != argv.length) {
            throw new IllegalArgumentException("Please specify name of the FIX message file");
        }

        final String fileName = argv[0];

        final File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException("File: " + file + " does  not exist");
        }

        return file;
    }
}
