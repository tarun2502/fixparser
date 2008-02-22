package net.java.fixparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;

public class IoUtils {

    private IoUtils() {
    }

    public static String readAll(Reader reader) throws IOException {

        StringBuilder result = new StringBuilder();
        CharBuffer buffer = CharBuffer.allocate(128);

        while (true) {
            buffer.clear();
            final int charNum = reader.read(buffer);

            if (-1 == charNum) {
                break;
            }

            buffer.flip();
            result.append(buffer.toString());
        }

        return result.toString();
    }

    public static void close(InputStream inputStream) throws IOException {
        if (null != inputStream) {
            inputStream.close();
        }
    }

    public static void close(Reader reader) throws IOException {
        if (null != reader) {
            reader.close();
        }
    }

}
