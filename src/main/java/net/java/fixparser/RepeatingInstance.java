package net.java.fixparser;

import java.util.List;


public class RepeatingInstance extends FieldContainerImpl {

    public RepeatingInstance() {
        super();
    }

    public RepeatingInstance(Field... fields) {
        super(fields);
    }

    public RepeatingInstance(int capacity) {
        super(capacity);
    }

    public RepeatingInstance(List<Field> fields) {
        super(fields);
    }
}
