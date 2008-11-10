package net.java.fixparser;

import java.util.ArrayList;
import java.util.List;

import net.java.util.TagValue;
import net.java.util.TagValueImpl;

/**
 * Simple FIX Message container. Does not provide access to 
 * fields by tag name. You can access fields only iterating through {@link #fields()}.
 * 
 * @author Leonid Shlyapnikov
 */
public class SimpleFixMessage {
    
    private final ArrayList<TagValue<String, String>> fieldList = new ArrayList<TagValue<String, String>>();

    /**
     * Adds a new field to the message.
     * 
     * @param tag
     * @param value
     */
    public void put(String tag, String value) {
        fieldList.add(new TagValueImpl<String, String>(tag, value));
    }
    
    /**
     * Adds a new fields to the message.
     * 
     * @param tagValue
     */
    public void put(TagValue<String, String> tagValue) {
        if (null == tagValue) {
            throw new NullPointerException("Cannot add a null tagValue to fix message!");
        }
        fieldList.add(tagValue);
    }

    /**
     * Returns list of fields.
     * 
     * @return list of fields, never rerturns <code>null</code>.
     */
    public List<TagValue<String, String>> fields() {
        return fieldList;
    }

    /**
     * Returns pipe-delimited FIX message representation.
     * 
     * @return string.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (TagValue<String, String> tagValue : fieldList) {
            result.append(tagValue);
        }
        
        return result.toString();
    }
}
