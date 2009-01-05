package net.java.fixparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class FieldContainerImpl implements FieldContainer {

    /**
     * Contains all fields including repeating groups.
     */
    private List<Field> fieldList;

    /**
     * Contains mappings field.tag() to index in the fieldList for all fields including repeating groups.
     */
    private Map<String, Integer> fieldIndexMap;

    /**
     * Contains mappings repeatingGroup.tag() to index in the fieldList for all repeating groups.
     */
    private Map<String, Integer> repeatingGroupIndexMap;

    public FieldContainerImpl() {
        this.fieldList = new ArrayList<Field>();
        this.fieldIndexMap = new HashMap<String, Integer>();
        this.repeatingGroupIndexMap = new HashMap<String, Integer>(2);
    }

    public FieldContainerImpl(int capacity) {
        this.fieldList = new ArrayList<Field>(capacity);
        this.fieldIndexMap = new HashMap<String, Integer>(capacity);
        this.repeatingGroupIndexMap = new HashMap<String, Integer>(2);
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#get(java.lang.String)
     */
    public Field get(String tagId) {
        Integer index = fieldIndexMap.get(tagId);
        if (null == index) {
            return null;
        }

        return fieldList.get(index.intValue());
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#put(net.java.fixparser.Field)
     */
    public Field put(Field newField) {
        Integer index = fieldIndexMap.get(newField.tag());

        Field result = null;

        if (index != null) {
            // update existing field
            result = set(index, newField);
        } else {
            // add new field
            addNew(newField);
        }

        assert fieldIndexMap.size() == fieldList.size();

        return result;
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#get(int)
     */
    public Field get(int index) {
        return fieldList.get(index);
    }

    // TODO this method needs more unit testing:
    // TODO different scenarios: update a field specifying a new index, update an index specifying a new field, etc all possible choices
    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#set(int, net.java.fixparser.Field)
     */
    public Field set(int index, Field newField) {

        final Integer newIndex = new Integer(index);

        // 1. set field in fieldIndexMap
        final Integer existingIndex = fieldIndexMap.put(newField.tag(), newIndex);
        if (null != existingIndex && existingIndex.intValue() != newIndex.intValue()) {
            // rollback the change
            fieldIndexMap.put(newField.tag(), existingIndex);
            throw new IllegalArgumentException("Cannot set a field. Field: " + newField.tag()
                    + " already set on the container with existingIndex: " + existingIndex
                    + ", you tried to set it with newIndex: " + newIndex);
        }

        // 2. if repeating group then put the index into repeatingGroupIndexMap
        if (newField.isRepeatingGroup()) {
            repeatingGroupIndexMap.put(newField.tag(), newIndex);
        }

        // 3. set in fieldList
        final Field removedField = fieldList.set(newIndex.intValue(), newField);
        
        // 4. if removedField.tag is not newField.tag, remove it from repeatingGroupIndexMap
        if (removedField.isRepeatingGroup() && !removedField.tag().equals(newField.tag())) {
            repeatingGroupIndexMap.remove(removedField.tag());
        }

        assert fieldIndexMap.size() == fieldList.size();

        return removedField;
    }

    private void addNew(Field newField) {
        final int newFieldIndex = fieldList.size();

        // 1. add to fieldIndexMap
        {
            Integer oldIndx = fieldIndexMap.put(newField.tag(), newFieldIndex); // put new index
            assert null == oldIndx;
        }

        // 2. if it's repeating group, add it to repeatingGroupIndexMap
        if (newField.isRepeatingGroup()) {
            Integer oldRepeatingGroupIndx = repeatingGroupIndexMap.put(newField.tag(), newFieldIndex);
            assert null == oldRepeatingGroupIndx;
        }

        // 3. add to fieldList 
        fieldList.add(newField);

        assert fieldIndexMap.size() == fieldList.size();
    }
    
    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#remove(int)
     */
    public Field remove(int index) {
        // 1. remove from fieldList
        final Field removedField = fieldList.remove(index);
        if (null == removedField) {
            return null;
        }
        
        // 2. remove from fieldIndexMap
        fieldIndexMap.remove(removedField.tag());
        
        //3. if it's repeating group, remove it from repeatingGroupIndexMap
        if (removedField.isRepeatingGroup()) {
            Integer removedRepeatingGroupIndx = repeatingGroupIndexMap.remove(removedField.tag());
            assert null == removedRepeatingGroupIndx;
        }
        
        assert fieldIndexMap.size() == fieldList.size();
        return removedField;
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#remove(java.lang.String)
     */
    public Field remove(String tagId) {
        // 1. remove from fieldIndexMap
        Integer removedFieldIndex = fieldIndexMap.remove(tagId);
        if (null == removedFieldIndex) {
            return null;
        }
        
        // 2. remove from fieldList
        Field removedField = fieldList.remove(removedFieldIndex.intValue());
        
        // 3. if it's repeating group, remove it from repeatingGroupIndexMap
        if (removedField.isRepeatingGroup()) {
            Integer removedRepeatingGroupIndx = repeatingGroupIndexMap.remove(removedField.tag());
            assert null == removedRepeatingGroupIndx;
        }
        
        assert fieldIndexMap.size() == fieldList.size();
        return removedField;
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#numberOfFields()
     */
    public int numberOfFields() {
        assert fieldIndexMap.size() == fieldList.size();
        return fieldList.size();
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#hasRepeatingGroup()
     */
    public boolean hasRepeatingGroup() {
        return repeatingGroupIndexMap.size() > 0;
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#getRepeatingGroup(java.lang.String)
     */
    public RepeatingGroup getRepeatingGroup(String tagId) {
        return (RepeatingGroup) get(tagId);
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.FieldContainer#getRepeatingGroup(int)
     */
    public RepeatingGroup getRepeatingGroup(int index) {
        return (RepeatingGroup) get(index);
    }

    /**
     * For unit testing only.
     * 
     * @return
     */
    int fieldListSize() {
        return fieldList.size();
    }

    /**
     * For unit testing only.
     * 
     * @return
     */
    int fieldIndexMapSize() {
        return fieldIndexMap.size();
    }
    
    /**
     * For unit testing only.
     * 
     * @return
     */
    int repeatingGroupIndexMapSize() {
        return repeatingGroupIndexMap.size();
    }
}
