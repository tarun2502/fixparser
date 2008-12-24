package net.java.fixparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.util.TagValue;

public class CompositeTagValue {

    private List<TagValue<String, String>> tagList;

    private Map<String, Integer> tagIndexMap;

    public CompositeTagValue() {
        this.tagList = new ArrayList<TagValue<String, String>>();
        this.tagIndexMap = new HashMap<String, Integer>();
    }

    public CompositeTagValue(int capacity) {
        this.tagList = new ArrayList<TagValue<String, String>>(capacity);
        this.tagIndexMap = new HashMap<String, Integer>(capacity);
    }

    public TagValue<String, String> get(String tagId) {
        Integer index = tagIndexMap.get(tagId);
        if (null == index) {
            return null;
        }

        return tagList.get(index.intValue());
    }

    public TagValue<String, String> put(TagValue<String, String> newField) {
        Integer index = tagIndexMap.get(newField.tag());

        TagValue<String, String> result = null;

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

    public TagValue<String, String> get(int index) {
        return tagList.get(index);
    }

    public TagValue<String, String> set(int index, TagValue<String, String> newField) {
        Integer existingIndex = tagIndexMap.get(newField.tag());

        if (null != existingIndex && existingIndex.intValue() != index) {
            throw new IllegalArgumentException("Cannot set a field. Field: " + newField.tag()
                    + " already set on the container with existingIndex: " + existingIndex
                    + ", you tried to set it with index: " + index);
        }

        TagValue<String, String> result = tagList.set(index, newField);
        assert tagIndexMap.size() == tagList.size();

        return result;
    }

    private void addNew(TagValue<String, String> newField) {
        Integer oldIndx = tagIndexMap.put(newField.tag(), tagList.size()); // put new index
        assert null == oldIndx;
        
        //        if (null != oldIndx) {
        //            // rollback the change
        //            tagIndexMap.put(newField.tag(), oldIndx);
        //            // throw
        //            throw new IllegalArgumentException("Cannot add a field. Field: " + newField.tag()
        //                    + " already added to the container");
        //        }

        tagList.add(newField);

        assert tagIndexMap.size() == tagList.size();
    }

    public TagValue<String, String> remove(int index) {
        TagValue<String, String> result = tagList.remove(index);
        if (null == result) {
            return null;
        }
        tagIndexMap.remove(result.tag());
        assert tagIndexMap.size() == tagList.size();
        return result;
    }

    public TagValue<String, String> remove(String tagId) {
        Integer index = tagIndexMap.remove(tagId);
        if (null == index) {
            return null;
        }
        TagValue<String, String> result = tagList.remove(index.intValue());
        assert tagIndexMap.size() == tagList.size();
        return result;
    }

    public int size() {
        assert tagIndexMap.size() == tagList.size();
        return tagList.size();
    }
}
