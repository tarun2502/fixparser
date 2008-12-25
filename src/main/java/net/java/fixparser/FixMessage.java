package net.java.fixparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixMessage {

    private List<Field> tagList;

    private Map<String, Integer> tagIndexMap;

    public FixMessage() {
        this.tagList = new ArrayList<Field>();
        this.tagIndexMap = new HashMap<String, Integer>();
    }

    public FixMessage(int capacity) {
        this.tagList = new ArrayList<Field>(capacity);
        this.tagIndexMap = new HashMap<String, Integer>(capacity);
    }

    public Field get(String tagId) {
        Integer index = tagIndexMap.get(tagId);
        if (null == index) {
            return null;
        }

        return tagList.get(index.intValue());
    }

    public Field put(Field newField) {
        Integer index = tagIndexMap.get(newField.tag());

        Field result = null;

        if (index != null) {
            // update existing field
            result = set(index, newField);
        } else {
            // add new field
            addNew(newField);
        }

        assert tagIndexMap.size() == tagList.size();

        return result;
    }

    public Field get(int index) {
        return tagList.get(index);
    }

    public Field set(int index, Field newField) {
        Integer existingIndex = tagIndexMap.get(newField.tag());

        if (null != existingIndex && existingIndex.intValue() != index) {
            throw new IllegalArgumentException("Cannot set a field. Field: " + newField.tag()
                    + " already set on the container with existingIndex: " + existingIndex
                    + ", you tried to set it with index: " + index);
        }

        Field result = tagList.set(index, newField);
        assert tagIndexMap.size() == tagList.size();

        return result;
    }

    private void addNew(Field newField) {
        Integer oldIndx = tagIndexMap.put(newField.tag(), tagList.size()); // put new index
        assert null == oldIndx;
        tagList.add(newField);
        assert tagIndexMap.size() == tagList.size();
    }

    public Field remove(int index) {
        Field result = tagList.remove(index);
        if (null == result) {
            return null;
        }
        tagIndexMap.remove(result.tag());
        assert tagIndexMap.size() == tagList.size();
        return result;
    }

    public Field remove(String tagId) {
        Integer index = tagIndexMap.remove(tagId);
        if (null == index) {
            return null;
        }
        Field result = tagList.remove(index.intValue());
        assert tagIndexMap.size() == tagList.size();
        return result;
    }

    public int size() {
        assert tagIndexMap.size() == tagList.size();
        return tagList.size();
    }
    
    public boolean hasRepeatingGroup() {
        return false; //XXX
    }
    
    public RepeatingGroup getRepeatingGroup(String tagId) {
       return (RepeatingGroup) get(tagId);
    }
    
    public RepeatingGroup getRepeatingGroup(int index) {
        return (RepeatingGroup) get(index);
    }    
}
