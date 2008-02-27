package net.java.fixparser;

import org.junit.Assert;
import org.junit.Test;

public class SimpleFixMessageTest {

    private static final String TAGS[] =
            new String[] { "8", "9", "35", "34", "49", "56", "52", "70", "71", "626", "857", "73", "11", "38", "37",
                    "54", "124", "32", "17", "31", "29", "32", "17", "31", "29", "10", };

    private static final String VALUES[] =
            new String[] { "FIX.4.4", "60", "J", "1", "CLIENT", "SERVER", "20080204-19:19:11", "1", "0", "2", "1", "1",
                    "20041210-00001", "121", "01", "2", "2", "321", "171", "311", "1", "322", "172", "312", "2", "123" };

    @Test
    public void testRepeatingGroupWithTwoInstances() {

        // GIVEN

        Assert.assertEquals(TAGS.length, VALUES.length);

        SimpleFixMessage msg = new SimpleFixMessage();

        for (int indx = 0, len = TAGS.length; indx < len; indx++) {
            msg.put(TAGS[indx], VALUES[indx]);
        }

        // ASSERTS
        
        Assert.assertEquals(TAGS.length, msg.fields().size());

        for (int indx = 0, len = TAGS.length; indx < len; indx++) {
            TagValue<String, String> field = msg.fields().get(indx);
            Assert.assertEquals(TAGS[indx], field.tag());
            Assert.assertEquals(VALUES[indx], field.value());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testShouldThrowExceptionForNullTag() {
        SimpleFixMessage msg = new SimpleFixMessage();
        msg.put("someTag", null);
    }

    @Test(expected = NullPointerException.class)
    public void testShouldThrowExceptionForNullValue() {
        SimpleFixMessage msg = new SimpleFixMessage();
        msg.put("someTag", null);
    }

}
