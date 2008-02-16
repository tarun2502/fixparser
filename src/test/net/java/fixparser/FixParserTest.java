package net.java.fixparser;

import java.io.FileInputStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class FixParserTest {

    private static final String FIX_MSG =
            "8=FIX.4.2|9=99|35=D|49=SenderCompId|56=TargetCompId|115=BROK|128=INST|11=ClOrdId1_1010|15=USD|21=3|38=999|40=1|54=1|55=IBM|60=20050611-00:43:34|10=999|";

    private static final String EXPECTED_FIELDS[] =
            new String[] { "8", "9", "35", "49", "56", "115", "128", "11", "15", "21", "38", "40", "54", "55", "60",
                    "10" };

    private static final String EXPECTED_VALUES[] =
            new String[] { "FIX.4.2", "99", "D", "SenderCompId", "TargetCompId", "BROK", "INST", "ClOrdId1_1010",
                    "USD", "3", "999", "1", "1", "IBM", "20050611-00:43:34", "999" };

    private static final String SAMPLE_LOG_FILE = "data/sample_fix_messages_bin.txt";

    @Test
    public void tesShouldParseSimpleFixMessage() throws Exception {
        SimpleFixParser parser = new SimpleFixParser(new StringReader(FIX_MSG));
        SimpleFixMessage fixMessage = parser.readFixMessage();

        System.out.println("fixMessage: " + fixMessage);

        Assert.assertNotNull(fixMessage);
        Assert.assertEquals(EXPECTED_FIELDS.length, EXPECTED_VALUES.length);
        Assert.assertEquals(EXPECTED_FIELDS.length, fixMessage.fields().size());

        for (int indx = 0, len = EXPECTED_FIELDS.length; indx < len; indx++) {
            final String expectedTag = EXPECTED_FIELDS[indx];
            final String expectedValue = EXPECTED_VALUES[indx];
            TagValue<String, String> field = fixMessage.fields().get(indx);
            Assert.assertEquals(expectedTag, field.tag());
            Assert.assertEquals(expectedValue, field.value());
        }
    }

    @Test
    public void testShouldReturnNullWhenReachTheEndOfTheStream() throws Exception {
        SimpleFixParser parser = new SimpleFixParser(new StringReader(FIX_MSG));

        // the stream contains only one message
        SimpleFixMessage fixMessage = parser.readFixMessage();
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
       
        SimpleFixParser parser = new SimpleFixParser(new StringReader(SOME_STRANGE_MSG));

        // the stream contains only one message
        SimpleFixMessage fixMessage = parser.readFixMessage();
        Assert.assertNotNull(fixMessage);
        
        // if toString or internal fields container is changed this will not be true
        Assert.assertEquals(SOME_STRANGE_MSG, fixMessage.toString());
    }
    
    @Test
    public void testParseSampleLogFile() throws Exception {
        // nothing special just try to parse out a log file
        FileInputStream fis = new FileInputStream(SAMPLE_LOG_FILE);
        SimpleFixParser parser = new SimpleFixParser(fis);

        for (int indx = 0; true; indx++) {
            System.out.println("reading message number: " + (indx + 1));
            SimpleFixMessage msg = parser.readFixMessage();
            if (null == msg) {
                break;
            }
        }
    }
}
