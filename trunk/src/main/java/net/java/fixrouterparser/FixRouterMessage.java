package net.java.fixrouterparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.java.util.TagValue;
import net.java.util.TagValueImpl;

/**
 * @author Leonid Shlyapnikov
 */
public class FixRouterMessage implements Iterable<TagValue<String, String>> {

    private final ArrayList<TagValue<String, String>> fieldList = new ArrayList<TagValue<String, String>>();

    private final Set<String> fieldsOfInterest;

    private final Map<String, Integer> fieldsOfInterestMap;

    public FixRouterMessage() {
        this(null);
    }

    public FixRouterMessage(Set<String> fieldsOfInterest) {
        if (null != fieldsOfInterest && fieldsOfInterest.size() > 0) {
            this.fieldsOfInterest = Collections.unmodifiableSet(new HashSet<String>(fieldsOfInterest));
            this.fieldsOfInterestMap = new HashMap<String, Integer>(fieldsOfInterest.size());
        } else {
            this.fieldsOfInterest = null;
            this.fieldsOfInterestMap = null;
        }
    }

    /**
     * Adds a new field to the message.
     * 
     * @param tag
     * @param value
     */
    public void put(String tag, String value) {
        this.put(new TagValueImpl<String, String>(tag, value));
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

        if (null != fieldsOfInterest && fieldsOfInterest.contains(tagValue.tag())) {
            fieldsOfInterestMap.put(tagValue.tag(), fieldList.size() - 1);
        }
    }
    
    public TagValue<String, String> field(int indx) {
        return fieldList.get(indx);
    }

    public Iterator<TagValue<String, String>> iterator() {
        return Collections.unmodifiableList(fieldList).iterator();
    }

    public int numberOfFields() {
        return fieldList.size();
    }

    public TagValue<String, String> fieldOfInterest(String tag) {
        if (null == fieldsOfInterest) {
            throw new IllegalStateException("Fields of interest are not specified for this message");
        }

        if (!fieldsOfInterest.contains(tag)) {
            throw new IllegalArgumentException("Field: " + tag + " is not specified as a field of interest");
        }

        Integer fieldIndex = fieldsOfInterestMap.get(tag);
        if (null == fieldIndex) {
            return null;
        } else {
            return fieldList.get(fieldIndex.intValue());
        }
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
