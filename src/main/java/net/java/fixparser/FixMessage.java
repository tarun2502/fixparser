package net.java.fixparser;

import java.util.List;


public class FixMessage extends FieldContainerImpl {

    public FixMessage() {
        super();
    }

    public FixMessage(Field... fields) {
        super(fields);
    }

    public FixMessage(int capacity) {
        super(capacity);
    }

    public FixMessage(List<Field> fields) {
        super(fields);
    }
}