package net.java.fixrouterparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.java.fixparser.IoUtils;
import net.java.util.TagValue;

import org.junit.Assert;
import org.junit.Test;

public class FixRouterParserTest {
    private static final String FIX_MSG =
            "8=FIX.4.2|9=99|35=D|49=SenderCompId|56=TargetCompId|115=BROK|128=INST|11=ClOrdId1_1010|15=USD|21=3|38=999|40=1|54=1|55=IBM|60=20050611-00:43:34|10=999|";

    private static final String EXPECTED_FIELDS[] =
            new String[] { "8", "9", "35", "49", "56", "115", "128", "11", "15", "21", "38", "40", "54", "55", "60",
                    "10" };

    private static final String EXPECTED_VALUES[] =
            new String[] { "FIX.4.2", "99", "D", "SenderCompId", "TargetCompId", "BROK", "INST", "ClOrdId1_1010",
                    "USD", "3", "999", "1", "1", "IBM", "20050611-00:43:34", "999" };

    private static final String SAMPLE_LOG_FILE = "data/sample_fix_messages.fix";

    private static final String FIX_MESSAGE_WITH_MULTILINE_VALUE = "data/fixmessage_with_multiline_value.fix";

    @Test
    public void tesShouldParseFixRouterMessage() throws Exception {
        FixRouterParser parser = new FixRouterParser(new StringReader(replacePipe(FIX_MSG)));
        FixRouterMessage fixMessage = parser.readFixMessage();

        System.out.println("fixMessage: " + fixMessage);

        Assert.assertNotNull(fixMessage);
        Assert.assertEquals(EXPECTED_FIELDS.length, EXPECTED_VALUES.length);
        Assert.assertEquals(EXPECTED_FIELDS.length, fixMessage.numberOfFields());

        for (int indx = 0, len = EXPECTED_FIELDS.length; indx < len; indx++) {
            final String expectedTag = EXPECTED_FIELDS[indx];
            final String expectedValue = EXPECTED_VALUES[indx];
            TagValue<String, String> field = fixMessage.field(indx);
            Assert.assertEquals(expectedTag, field.tag());
            Assert.assertEquals(expectedValue, field.value());
        }
    }

    @Test
    public void tesShouldParseFixRouterMessageWithSpecifiedFieldsOfInterest() throws Exception {
        // setup

        final List<String> fieldsOfInterestArr = Arrays.asList("8", "9", "35", "49");
        final List<String> valueOfInterestArr = Arrays.asList("FIX.4.2", "99", "D", "SenderCompId");

        FixRouterParser parser = new FixRouterParser(new StringReader(replacePipe(FIX_MSG)));
        parser.setFieldsOfInterest(new HashSet<String>(fieldsOfInterestArr));

        // under test

        final FixRouterMessage fixMessage = parser.readFixMessage();

        System.out.println("fixMessage: " + fixMessage);

        Assert.assertNotNull(fixMessage);
        Assert.assertEquals(EXPECTED_FIELDS.length, EXPECTED_VALUES.length);
        Assert.assertEquals(EXPECTED_FIELDS.length, fixMessage.numberOfFields());

        // assert all fields are available
        for (int indx = 0, len = EXPECTED_FIELDS.length; indx < len; indx++) {
            final String expectedTag = EXPECTED_FIELDS[indx];
            final String expectedValue = EXPECTED_VALUES[indx];
            TagValue<String, String> field = fixMessage.field(indx);

            Assert.assertEquals(expectedTag, field.tag());
            Assert.assertEquals(expectedValue, field.value());
        }

        // assert all fields of interest are accessible
        for (int indx = 0; indx < fieldsOfInterestArr.size(); indx++) {
            final String expectedValue = valueOfInterestArr.get(indx);
            final String tag = fieldsOfInterestArr.get(indx);

            Assert.assertEquals(expectedValue, fixMessage.fieldOfInterest(tag).value());
        }
    }

    @Test
    public void testShouldReturnNullWhenReachTheEndOfTheStream() throws Exception {
        FixRouterParser parser = new FixRouterParser(new StringReader(replacePipe(FIX_MSG)));

        // the stream contains only one message
        FixRouterMessage fixMessage = parser.readFixMessage();
        Assert.assertNotNull(fixMessage);

        // this is the end of the stream, so expect a null value
        fixMessage = parser.readFixMessage();
        Assert.assertNull("EOF reached, null is expected", fixMessage);

        // the second attempt to read after EOF, should be null again
        fixMessage = parser.readFixMessage();
        Assert.assertNull("EOF reached, null is expected", fixMessage);
    }

    @Test
    public void testShouldParseMessageWithEmptyTagValue() throws Exception {

        // tag84 (a repeating group instance field) contains an empty value,
        // looks like it is not a valid FIX message according to the spec
        // but we still want to parse it our and save into the db.
        final String SOME_STRANGE_MSG =
                "8=FIX.4.2|9=99|35=N|49=SenderCompId|56=TargetCompId|115=INST|128=BROK|66=ListId_1010|82=1|83=1|431=4|429=2|68=2|73=2|11=1118420243360-25|14=999|39=1|151=0|84=0|6=100.25|11=1118420243360-26|14=1000|39=2|151=0|84=|6=100.25|10=999|";

        FixRouterParser parser = new FixRouterParser(new StringReader(replacePipe(SOME_STRANGE_MSG)));

        // the stream contains only one message
        FixRouterMessage fixMessage = parser.readFixMessage();
        Assert.assertNotNull(fixMessage);

        // if toString or internal fields container is changed this will not be true
        Assert.assertEquals(SOME_STRANGE_MSG, fixMessage.toString());
    }

    @Test
    public void testParseSampleLogFile() throws Exception {
        // nothing special just try to parse out a log file
        InputStream inputStream = new NewLineRemoverInputStream(new FileInputStream(SAMPLE_LOG_FILE));
        FixRouterParser parser = new FixRouterParser(inputStream);

        for (int indx = 0; true; indx++) {
            System.out.println("reading message number: " + (indx + 1));
            FixRouterMessage msg = parser.readFixMessage();
            if (null == msg) {
                break;
            }
        }

        IoUtils.close(inputStream);
    }

    @Test
    public void testShouldParseFixMessageWithMultilineValue() throws Exception {
        final String tag = "91";

        // load the expected multiline value from file
        final String expectedValue = loadTagValueFromFile(new File(FIX_MESSAGE_WITH_MULTILINE_VALUE), tag);
        Assert.assertTrue(expectedValue.startsWith("-----BEGIN PGP MESSAGE-----\n"));
        Assert.assertTrue(expectedValue.endsWith("-----END PGP MESSAGE-----\n"));

        // parse the message

        FileInputStream fis = new FileInputStream(FIX_MESSAGE_WITH_MULTILINE_VALUE);
        FixRouterParser parser = new FixRouterParser(fis);

        FixRouterMessage msg = parser.readFixMessage();
        Assert.assertNotNull(msg);

        IoUtils.close(fis);

        // assert the multiline value

        int numOfMatchingFields = 0;

        for (TagValue<String, String> field : msg) {
            if (tag.equals(field.tag())) {
                Assert.assertEquals(expectedValue, field.value());
                numOfMatchingFields++;
            }
        }

        Assert.assertEquals(1, numOfMatchingFields);
    }

    private static String loadTagValueFromFile(File file, String tag) throws IOException {
        String result = null;

        String fileContent;
        Reader reader = new FileReader(file);

        try {
            fileContent = IoUtils.readAll(reader);
        } finally {
            IoUtils.close(reader);
        }

        if (null == fileContent) {
            throw new IllegalStateException("Cannot read from file: " + file);
        }

        int beginIndex = fileContent.indexOf(tag + "=");
        if (beginIndex <= 0) {
            throw new IllegalStateException("Cannot load tag: " + tag + " from file: " + file);
        }

        beginIndex = beginIndex + tag.length() + 1; // 1 char for '='

        int endIndex = fileContent.indexOf("\u0001", beginIndex);
        if (endIndex <= beginIndex) {
            throw new IllegalStateException("Cannot load tag: " + tag + " from file: " + file);
        }

        result = fileContent.substring(beginIndex, endIndex);

        return result;
    }

    private static String replacePipe(String str) {
        return str.replaceAll("\\|", "\u0001");
    }

    private static class NewLineRemoverInputStream extends InputStream {

        private final InputStream inputSteam;

        private NewLineRemoverInputStream(InputStream inputStream) {
            this.inputSteam = inputStream;
        }

        @Override
        public int read() throws IOException {
            while (true) {
                int result = inputSteam.read();
                if ('\n' != result && '\r' != result) {
                    return result;
                }
            }
        }
    }
}
