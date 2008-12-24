package net.java.fixparser;

import net.java.util.TagValue;
import net.java.util.TagValueImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompositeTagValueTest {

    private CompositeTagValue message;

    @Before
    public void before() {
        message = new CompositeTagValue();
    }

    @Test
    public void testPutAndGet() {
        // GIVEN
        final TagValue<String, String> expectedField = createField("11", "Value");

        // WHEN
        Assert.assertEquals(0, message.size());
        message.put(expectedField);

        // THEN
        Assert.assertEquals(1, message.size());
        Assert.assertEquals(expectedField, message.get(expectedField.tag()));
        Assert.assertEquals(expectedField, message.get(0));
    }

    @Test
    public void testPutUpdate() {
        // GIVEN
        final String tag = "11";
        final String value = "Value";
        final TagValue<String, String> expectedField = createField(tag, value);
        message.put(expectedField);
        Assert.assertEquals(1, message.size());

        // WHEN
        message.put(createField(tag, value));

        // THEN
        Assert.assertEquals(1, message.size());
        Assert.assertEquals(tag, message.get(0).tag());
        Assert.assertEquals(value, message.get(0).value());
    }
    
    @Test
    public void testPutPut() {
        // GIVEN
        
        final String tag = "11";
        final String value = "Value11";
        
        message.put(createField("10", "value10"));
        message.put(createField(tag, value));
        message.put(createField("12", "value12"));
        
        Assert.assertEquals(3, message.size());

        // WHEN
        final TagValue<String, String> oldField = message.put(createField(tag, "_NEW_" + value));

        // THEN
        Assert.assertEquals(3, message.size());
        Assert.assertEquals(tag, message.get(1).tag());
        Assert.assertEquals("_NEW_" + value, message.get(1).value());
        Assert.assertEquals(tag, oldField.tag());
        Assert.assertEquals(value, oldField.value());
    }

    @Test
    public void testShouldUpdateExistingFieldByIndex() {
        // GIVEN
        final String tag = "11";
        final String value = "Value11";
        message.put(createField("10", "value10"));
        message.put(createField(tag, value));
        message.put(createField("12", "value12"));
        Assert.assertEquals(3, message.size());

        // WHEN
        message.set(1, createField(tag, "_NEW_" + value));

        // THEN
        Assert.assertEquals(3, message.size());
        Assert.assertEquals(tag, message.get(1).tag());
        Assert.assertEquals("_NEW_" + value, message.get(1).value());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldBlowBecauseUpdatingWithWrongIndex() {
        // GIVEN
        final String tag = "11";
        final String value = "Value11";
        message.put(createField("10", "value10"));
        message.put(createField(tag, value));
        message.put(createField("12", "value12"));
        Assert.assertEquals(3, message.size());

        // WHEN
        message.set(0, createField(tag, value)); // MUST BE 1
        
        // THEN
        Assert.fail("IllegalArgumentException is expected");
    }

    @Test
    public void testPutPutAndGet() {
        // GIVEN
        
        final TagValue<String, String> initialField = createField("11", "Value");
        final TagValue<String, String> expectedField = createField("11", "NewTestValue");
        
        message.put(initialField);
        Assert.assertEquals(1, message.size());

        // WHEN
        message.put(expectedField);

        // THEN
        Assert.assertEquals(1, message.size());
        Assert.assertEquals(expectedField, message.get(expectedField.tag()));
        Assert.assertEquals(expectedField, message.get(0));
    }

    @Test
    public void testPutThenSet() {
        // GIVEN
        
        final TagValue<String, String> initialField = createField("11", "TestValue");
        final TagValue<String, String> expectedField = createField("11", "NewTestValue");
        
        message.put(initialField);
        Assert.assertEquals(1, message.size());

        // WHEN
        Assert.assertEquals(1, message.size());
        message.set(0, expectedField);

        // THEN
        Assert.assertEquals(1, message.size());
        Assert.assertEquals(expectedField, message.get(expectedField.tag()));
        Assert.assertEquals(expectedField, message.get(0));
    }

    @Test
    public void testRemoveByTag() {
        // GIVEN
        final TagValue<String, String> toBeRemovedField = createField("11", "TestValue");
        message.put(toBeRemovedField);
        Assert.assertEquals(1, message.size());

        // WHEN
        final TagValue<String, String> removedField = message.remove(toBeRemovedField.tag());

        // THEN
        Assert.assertEquals(0, message.size());
        Assert.assertEquals(toBeRemovedField, removedField);        
    }

    @Test
    public void testRemoveByTag2() {
        // GIVEN
        
        final TagValue<String, String> expectedField0 = createField("11", "Value11");
        final TagValue<String, String> toBeRemoved = createField("12", "Value12");
        final TagValue<String, String> expectedField1 = createField("13", "Value13");

        message.put(expectedField0);
        message.put(toBeRemoved);
        message.put(expectedField1);
        
        Assert.assertEquals(3, message.size());

        // WHEN
        final TagValue<String, String> removedField  = message.remove(toBeRemoved.tag());

        // THEN
        Assert.assertEquals(2, message.size());
        Assert.assertEquals(expectedField0, message.get(0));
        Assert.assertEquals(expectedField1, message.get(1));
        Assert.assertEquals(toBeRemoved, removedField);        
    }
    
    @Test
    public void testRemoveByTag3() {
        // GIVEN
        final TagValue<String, String> field = createField("11", "TestValue");
        message.put(field);
        Assert.assertEquals(1, message.size());

        // WHEN
        final TagValue<String, String> mustBeNull = message.remove("666");

        // THEN
        Assert.assertEquals(1, message.size());
        Assert.assertEquals(field, message.get(0));
        Assert.assertNull(mustBeNull);
    }

    @Test
    public void testRemoveByIndex() {
        // GIVEN
        final TagValue<String, String> toBeRemoved = createField("11", "TestValue");
        message.put(toBeRemoved);
        Assert.assertEquals(1, message.size());

        // WHEN
        final TagValue<String, String> removedField = message.remove(0);

        // THEN
        Assert.assertEquals(0, message.size());
        Assert.assertEquals(toBeRemoved, removedField);
    }

    @Test
    public void testRemoveByIndex2() {
        // GIVEN
        
        final TagValue<String, String> expectedField0 = createField("11", "Value11");
        final TagValue<String, String> toBeRemoved = createField("12", "Value12");
        final TagValue<String, String> expectedField1 = createField("13", "Value13");

        message.put(expectedField0);
        message.put(toBeRemoved);
        message.put(expectedField1);
        
        Assert.assertEquals(3, message.size());        

        // WHEN
        final TagValue<String, String> removedField = message.remove(1);

        // THEN
        Assert.assertEquals(2, message.size());
        Assert.assertEquals(expectedField0, message.get(0));
        Assert.assertEquals(expectedField1, message.get(1));
        Assert.assertEquals(toBeRemoved, removedField);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveByIndex3() {
        // GIVEN
        final TagValue<String, String> field = createField("11", "TestValue");
        message.put(field);
        Assert.assertEquals(1, message.size());

        // WHEN
        message.remove(1); // MUST BLOW

        // THEN
        Assert.fail("Expected IndexOutOfBoundsException");
    }

    private static TagValueImpl<String, String> createField(String tag, String value) {
        return new TagValueImpl<String, String>(tag, value);
    }

}
