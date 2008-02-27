package net.java.fixparser;

import java.util.ArrayList;
import java.util.List;

public class SimpleFixMessage {
    
    private final ArrayList<TagValue<String, String>> fieldList = new ArrayList<TagValue<String, String>>();

    public void put(String tag, String value) {
        fieldList.add(new TagValueImpl<String, String>(tag, value));
    }
    
    public void put(TagValue<String, String> tagValue) {
        if (null == tagValue) {
            throw new NullPointerException("Cannot add a null tagValue to fix message!");
        }
        fieldList.add(tagValue);
    }

    public List<TagValue<String, String>> fields() {
        return fieldList;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (TagValue<String, String> tagValue : fieldList) {
            result.append(tagValue);
        }
        
        return result.toString();
    }
}
