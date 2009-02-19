package net.java.fixparser;

import java.util.Arrays;
import java.util.List;

public class RepeatingGroup implements Field {

    private final Field noField;

    private final List<RepeatingInstance> repeatingInstances;

    public RepeatingGroup(Field noField, RepeatingInstance... repeatingInstances) {
        this(noField, Arrays.asList(repeatingInstances));
    }

    public RepeatingGroup(Field noField, List<RepeatingInstance> repeatingInstances) {
        if (null == noField) {
            throw new NullPointerException("Argument noField is null!");
        }

        if (null == repeatingInstances) {
            throw new NullPointerException("Argument repeatingInstances is null!");
        }

        this.noField = noField;
        this.repeatingInstances = repeatingInstances;
    }

    public String tag() {
        return noField.tag();
    }

    public String value() {
        return noField.value();
    }

    public Field get(int repeatingInstanceIndex, String tagId) {
        RepeatingInstance repeatingInstance = repeatingInstances.get(repeatingInstanceIndex);
        return repeatingInstance.get(tagId);
    }

    public Field get(int repeatingInstanceIndex, int fieldIndexInRepeatingInstance) {
        RepeatingInstance repeatingInstance = repeatingInstances.get(repeatingInstanceIndex);
        return repeatingInstance.get(fieldIndexInRepeatingInstance);
    }

    public int numberOfRepeatingInstances() {
        return repeatingInstances.size();
    }

    public boolean isRepeatingGroup() {
        return true;
    }

    public RepeatingGroup toRepeatingGroup() {
        return this;
    }
}
