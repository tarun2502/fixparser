package net.java.fixrouterparser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.java.util.TagValue;

import org.junit.Assert;
import org.junit.Test;

public class FixRouterMessageTest {
    private static final String TAGS[] =
            new String[] { "8", "9", "35", "34", "49", "56", "52", "70", "71", "626", "857", "73", "11", "38", "37",
                    "54", "124", "32", "17", "31", "29", "32", "17", "31", "29", "10", };

    private static final String VALUES[] =
            new String[] { "FIX.4.4", "60", "J", "1", "CLIENT", "SERVER", "20080204-19:19:11", "1", "0", "2", "1", "1",
                    "20041210-00001", "121", "01", "2", "2", "321", "171", "311", "1", "322", "172", "312", "2", "123" };

    @Test
    public void testRepeatingGroupWithTwoInstances() {

        // GIVEN

        FixRouterMessage msg = createFixRouterMessage(null);

        // ASSERTS

        Assert.assertEquals(TAGS.length, msg.numberOfFields());

        for (int indx = 0, len = TAGS.length; indx < len; indx++) {
            TagValue<String, String> field = msg.field(indx);
            Assert.assertEquals(TAGS[indx], field.tag());
            Assert.assertEquals(VALUES[indx], field.value());
        }
    }

    private FixRouterMessage createFixRouterMessage(Set<String> fieldsOfInterest) {
        Assert.assertEquals(TAGS.length, VALUES.length);

        FixRouterMessage msg = new FixRouterMessage(fieldsOfInterest);

        for (int indx = 0, len = TAGS.length; indx < len; indx++) {
            msg.put(TAGS[indx], VALUES[indx]);
        }
        return msg;
    }

    @Test(expected = NullPointerException.class)
    public void testShouldThrowExceptionForNullTag() {
        FixRouterMessage msg = new FixRouterMessage();
        msg.put("someTag", null);
    }

    @Test(expected = NullPointerException.class)
    public void testShouldThrowExceptionForNullValue() {
        FixRouterMessage msg = new FixRouterMessage();
        msg.put("someTag", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testShouldThrowExceptionWhenFieldsOfInterestNotSpecified() {
        FixRouterMessage msg = createFixRouterMessage(null);
        msg.fieldOfInterest("35");
    }

    @Test
    public void testShouldThrowExceptionWhenGetUnSpecifiedFieldOfInterest() {
        // test setup

        final Set<String> fieldsOfInterest = new HashSet<String>(Arrays.asList("8", "9", "35"));
        final String notConfiguredFieldOfInterest = "70";

        FixRouterMessage msg = createFixRouterMessage(fieldsOfInterest);

        // make sure that message contains notConfiguredFieldOfInterest
        boolean gotIt = false;
        for (TagValue<String, String> field : msg) {
            if (field.tag().equals(notConfiguredFieldOfInterest)) {
                gotIt = true;
                break;
            }
        }
        Assert.assertTrue("Message: " + msg + " does not contain field: " + notConfiguredFieldOfInterest, gotIt);

        // under test

        String errMsg = "";
        try {
            msg.fieldOfInterest(notConfiguredFieldOfInterest);
            Assert.fail("IllegalArgumentException is expected");
        } catch (IllegalArgumentException e) {
            errMsg = e.getMessage().toLowerCase();
        }

        // assert
        Assert.assertTrue(errMsg.contains(notConfiguredFieldOfInterest));
    }

    @Test
    public void testShouldReturnAllConfiguredFieldsOfInterest() {
        // test setup

        final List<String> fieldsOfInterestArr = Arrays.asList( "8", "9", "35", "34");
        final List<String> valueOfInterestArr = Arrays.asList("FIX.4.4", "60", "J", "1");

        FixRouterMessage msg = createFixRouterMessage(new HashSet<String>(fieldsOfInterestArr));

        // assert
        for (int indx = 0; indx < fieldsOfInterestArr.size(); indx++) {
            final String expectedValue = valueOfInterestArr.get(indx);
            final String tag = fieldsOfInterestArr.get(indx);
            
            Assert.assertEquals(expectedValue, msg.fieldOfInterest(tag).value());
        }
    }
}
