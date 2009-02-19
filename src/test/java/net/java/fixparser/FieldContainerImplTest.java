package net.java.fixparser;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static java.util.Arrays.asList;

// TODO get rid of FixMessageTest
public class FieldContainerImplTest {

    private FieldContainer message;

    @Before
    public void before() {
        message = new FieldContainerImpl();
    }

    @Test
    public void testPutAndGet() {
        // GIVEN
        final Field expectedField = field("11", "Value");

        // WHEN
        assertEquals(0, message.numberOfFields());
        assertFalse(message.hasRepeatingGroup());
        message.put(expectedField);

        // THEN
        assertEquals(1, message.numberOfFields());
        assertFalse(message.hasRepeatingGroup());
        assertEquals(expectedField, message.get(expectedField.tag()));
        assertEquals(expectedField, message.get(0));
    }

    @Test
    public void testPutAndGetForRepeatingGroup() {
        // GIVEN
        final RepeatingGroup expectedField =
                new RepeatingGroup(field("NoField", "NoValue"), new RepeatingInstance(field("ri01", "ri01V"), field(
                        "ri02", "ri02V"), field("ri03", "ri03V")));

        // WHEN
        assertEquals(0, message.numberOfFields());
        assertFalse(message.hasRepeatingGroup());
        message.put(expectedField);

        // THEN
        assertEquals(1, message.numberOfFields());
        assertTrue(message.hasRepeatingGroup());
        assertEquals(expectedField, message.get(expectedField.tag()));
        assertEquals(expectedField, message.get(0));
    }

    @Test
    public void testPutUpdate() {
        // GIVEN
        final String tag = "11";
        final String value = "Value";
        final Field expectedField = field(tag, value);
        message.put(expectedField);
        assertEquals(1, message.numberOfFields());

        // WHEN
        message.put(field(tag, value));

        // THEN
        assertEquals(1, message.numberOfFields());
        assertEquals(tag, message.get(0).tag());
        assertEquals(value, message.get(0).value());
    }

    @Test
    public void testUpdateKeepsPosition() {
        // GIVEN
        message.put(field("zzz", "zzz-value"));
        message.put(field("xxx", "xxx-value"));
        message.put(field("ggg", "ggg-value"));
        message.put(field("aaa", "aaa-value"));
        message.put(field("bbb", "bbb-value"));
        assertEquals(5, message.numberOfFields());

        // WHEN
        Field removed2 = message.put(field("ggg", "ggg-value-updated"));

        // THEN
        assertEquals("ggg", removed2.tag());
        assertEquals("ggg-value", removed2.value());
        int indx = 0;
        for (String expectedTag: asList("zzz", "xxx", "ggg", "aaa", "bbb")) {
            String expectedValue = expectedTag + "-value";
            if ("ggg".equals(expectedTag)) {
                expectedValue += "-updated";
            }
            assertEquals(expectedTag, message.get(indx).tag());
            assertEquals(expectedValue, message.get(indx).value());
            indx++;
        }
    }

    @Test
    public void testPutPut() {
        // GIVEN

        final String tag = "11";
        final String value = "Value11";

        message.put(field("10", "value10"));
        message.put(field(tag, value));
        message.put(field("12", "value12"));

        assertEquals(3, message.numberOfFields());

        // WHEN
        final Field oldField = message.put(field(tag, "_NEW_" + value));

        // THEN
        assertEquals(3, message.numberOfFields());
        assertEquals(tag, message.get(1).tag());
        assertEquals("_NEW_" + value, message.get(1).value());
        assertEquals(tag, oldField.tag());
        assertEquals(value, oldField.value());
    }

    @Test
    public void testShouldUpdateExistingFieldByIndex() {
        // GIVEN
        final String tag = "11";
        final String value = "Value11";
        message.put(field("10", "value10"));
        message.put(field(tag, value));
        message.put(field("12", "value12"));
        assertEquals(3, message.numberOfFields());

        // WHEN
        message.set(1, field(tag, "_NEW_" + value));

        // THEN
        assertEquals(3, message.numberOfFields());
        assertEquals(tag, message.get(1).tag());
        assertEquals("_NEW_" + value, message.get(1).value());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldBlowBecauseUpdatingWithWrongIndex() {
        // GIVEN
        final String tag = "11";
        final String value = "Value11";
        message.put(field("10", "value10"));
        message.put(field(tag, value));
        message.put(field("12", "value12"));
        assertEquals(3, message.numberOfFields());

        // WHEN
        message.set(0, field(tag, value)); // MUST BE 1

        // THEN
        fail("IllegalArgumentException is expected");
    }

    @Test
    public void testShouldSetNewFieldAtExistingIndex() {
        // GIVEN
        final String tag = "11";
        final String value = "Value11";
        message.put(field("10", "value10"));
        message.put(field(tag, value));
        message.put(field("12", "value12"));
        assertEquals(3, message.numberOfFields());

        // WHEN
        final Field removedField = message.set(1, field(tag + "-NEW", value + "-NEW"));

        // THEN
        assertEquals(tag, removedField.tag());
        assertEquals(value, removedField.value());
        assertEquals(tag + "-NEW", message.get(1).tag());
        assertEquals(value + "-NEW", message.get(1).value());
    }

    @Test
    public void testPutPutAndGet() {
        // GIVEN

        final Field initialField = field("11", "Value");
        final Field expectedField = field("11", "NewTestValue");

        message.put(initialField);
        assertEquals(1, message.numberOfFields());

        // WHEN
        message.put(expectedField);

        // THEN
        assertEquals(1, message.numberOfFields());
        assertEquals(expectedField, message.get(expectedField.tag()));
        assertEquals(expectedField, message.get(0));
    }

    @Test
    public void testPutThenSet() {
        // GIVEN

        final Field initialField = field("11", "TestValue");
        final Field expectedField = field("11", "NewTestValue");

        message.put(initialField);
        assertEquals(1, message.numberOfFields());

        // WHEN
        assertEquals(1, message.numberOfFields());
        message.set(0, expectedField);

        // THEN
        assertEquals(1, message.numberOfFields());
        assertEquals(expectedField, message.get(expectedField.tag()));
        assertEquals(expectedField, message.get(0));
    }

    @Test
    public void testRemoveByTag() {
        // GIVEN
        final Field toBeRemovedField = field("11", "TestValue");
        message.put(toBeRemovedField);
        assertEquals(1, message.numberOfFields());

        // WHEN
        final Field removedField = message.remove(toBeRemovedField.tag());

        // THEN
        assertEquals(0, message.numberOfFields());
        assertEquals(toBeRemovedField, removedField);
    }

    @Test
    public void testRemoveByTag2() {
        // GIVEN

        final Field expectedField0 = field("11", "Value11");
        final Field toBeRemoved = field("12", "Value12");
        final Field expectedField1 = field("13", "Value13");

        message.put(expectedField0);
        message.put(toBeRemoved);
        message.put(expectedField1);

        assertEquals(3, message.numberOfFields());

        // WHEN
        final Field removedField = message.remove(toBeRemoved.tag());

        // THEN
        assertEquals(2, message.numberOfFields());
        assertEquals(expectedField0, message.get(0));
        assertEquals(expectedField1, message.get(1));
        assertEquals(toBeRemoved, removedField);
    }

    @Test
    public void testRemoveByTag3() {
        // GIVEN
        final Field field = field("11", "TestValue");
        message.put(field);
        assertEquals(1, message.numberOfFields());

        // WHEN
        final Field mustBeNull = message.remove("666");

        // THEN
        assertEquals(1, message.numberOfFields());
        assertEquals(field, message.get(0));
        Assert.assertNull(mustBeNull);
    }

    @Test
    public void testRemoveByIndex() {
        // GIVEN
        final Field toBeRemoved = field("11", "TestValue");
        message.put(toBeRemoved);
        assertEquals(1, message.numberOfFields());

        // WHEN
        final Field removedField = message.remove(0);

        // THEN
        assertEquals(0, message.numberOfFields());
        assertEquals(toBeRemoved, removedField);
    }

    @Test
    public void testRemoveByIndex2() {
        // GIVEN

        final Field expectedField0 = field("11", "Value11");
        final Field toBeRemoved = field("12", "Value12");
        final Field expectedField1 = field("13", "Value13");

        message.put(expectedField0);
        message.put(toBeRemoved);
        message.put(expectedField1);

        assertEquals(3, message.numberOfFields());

        // WHEN
        final Field removedField = message.remove(1);

        // THEN
        assertEquals(2, message.numberOfFields());
        assertEquals(expectedField0, message.get(0));
        assertEquals(expectedField1, message.get(1));
        assertEquals(toBeRemoved, removedField);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveByIndex3() {
        // GIVEN
        final Field field = field("11", "TestValue");
        message.put(field);
        assertEquals(1, message.numberOfFields());

        // WHEN
        message.remove(1); // MUST BLOW

        // THEN
        Assert.fail("Expected IndexOutOfBoundsException");
    }

    private static Field field(String tag, String value) {
        return new FieldImpl(tag, value);
    }
}
