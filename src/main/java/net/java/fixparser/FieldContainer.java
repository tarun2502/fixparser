package net.java.fixparser;

public interface FieldContainer {

    Field get(String tagId);

    Field put(Field newField);

    Field get(int index);

    // TODO this method needs more unit testing:
    // TODO different scenarios: update a field specifying a new index, update an index specifying a new field, etc all possible choices
    Field set(int index, Field newField);

    Field remove(int index);

    Field remove(String tagId);

    int numberOfFields();

    boolean hasRepeatingGroup();

    RepeatingGroup getRepeatingGroup(String tagId);

    RepeatingGroup getRepeatingGroup(int index);

}