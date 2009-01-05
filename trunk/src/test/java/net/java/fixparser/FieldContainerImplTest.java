package net.java.fixparser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FieldContainerImplTest {
    
    private FieldContainer message;
    
    @Before
    public void before() {
        message = new FieldContainerImpl();
    }

    @Test
    public void testPutAndGet() {
        // GIVEN
        final Field expectedField = createField("11", "Value");

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
    public void testPutUpdate() {
        // GIVEN
        final String tag = "11";
        final String value = "Value";
        final Field expectedField = createField(tag, value);
        message.put(expectedField);
        assertEquals(1, message.numberOfFields());

        // WHEN
        message.put(createField(tag, value));

        // THEN
        assertEquals(1, message.numberOfFields());
        assertEquals(tag, message.get(0).tag());
        assertEquals(value, message.get(0).value());
    }
    
    @Test
    public void testPutPut() {
        // GIVEN
        
        final String tag = "11";
        final String value = "Value11";
        
        message.put(createField("10", "value10"));
        message.put(createField(tag, value));
        message.put(createField("12", "value12"));
        
        assertEquals(3, message.numberOfFields());

        // WHEN
        final Field oldField = message.put(createField(tag, "_NEW_" + value));

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
        message.put(createField("10", "value10"));
        message.put(createField(tag, value));
        message.put(createField("12", "value12"));
        assertEquals(3, message.numberOfFields());

        // WHEN
        message.set(1, createField(tag, "_NEW_" + value));

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
        message.put(createField("10", "value10"));
        message.put(createField(tag, value));
        message.put(createField("12", "value12"));
        assertEquals(3, message.numberOfFields());

        // WHEN
        message.set(0, createField(tag, value)); // MUST BE 1
        
        // THEN
        Assert.fail("IllegalArgumentException is expected");
    }

    @Test
    public void testPutPutAndGet() {
        // GIVEN
        
        final Field initialField = createField("11", "Value");
        final Field expectedField = createField("11", "NewTestValue");
        
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
        
        final Field initialField = createField("11", "TestValue");
        final Field expectedField = createField("11", "NewTestValue");
        
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
        final Field toBeRemovedField = createField("11", "TestValue");
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
        
        final Field expectedField0 = createField("11", "Value11");
        final Field toBeRemoved = createField("12", "Value12");
        final Field expectedField1 = createField("13", "Value13");

        message.put(expectedField0);
        message.put(toBeRemoved);
        message.put(expectedField1);
        
        assertEquals(3, message.numberOfFields());

        // WHEN
        final Field removedField  = message.remove(toBeRemoved.tag());

        // THEN
        assertEquals(2, message.numberOfFields());
        assertEquals(expectedField0, message.get(0));
        assertEquals(expectedField1, message.get(1));
        assertEquals(toBeRemoved, removedField);        
    }
    
    @Test
    public void testRemoveByTag3() {
        // GIVEN
        final Field field = createField("11", "TestValue");
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
        final Field toBeRemoved = createField("11", "TestValue");
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
        
        final Field expectedField0 = createField("11", "Value11");
        final Field toBeRemoved = createField("12", "Value12");
        final Field expectedField1 = createField("13", "Value13");

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
        final Field field = createField("11", "TestValue");
        message.put(field);
        assertEquals(1, message.numberOfFields());

        // WHEN
        message.remove(1); // MUST BLOW

        // THEN
        Assert.fail("Expected IndexOutOfBoundsException");
    }

    private static Field createField(String tag, String value) {
        return new FieldImpl(tag, value);
    }

}
