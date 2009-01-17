package net.java.fixparser;

import net.java.util.TagValue;
import net.java.util.TagValueImpl;
import net.jcip.annotations.Immutable;

@Immutable
public class FieldImpl implements Field {

    private final TagValue<String, String> tagValue;

    public FieldImpl(String tag, String value) {
        this.tagValue = new TagValueImpl<String, String>(tag, value);
    }

    public FieldImpl(TagValue<String, String> tagValue) {
        if (null == tagValue) {
            throw new NullPointerException("Argument tagValue is null!");
        }
        this.tagValue = tagValue;
    }

    /* (non-Javadoc)
     * @see net.java.fixparser.Field#isRepeatingGroup()
     */
    public boolean isRepeatingGroup() {
        return false;
    }
    
    public RepeatingGroup toRepeatingGroup() {
        throw new UnsupportedOperationException("Cannot cast Field to RepeatingGroup");
    }
    
    public String tag() {
        return tagValue.tag();
    }

    public String value() {
        return tagValue.value();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tagValue == null) ? 0 : tagValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FieldImpl other = (FieldImpl) obj;
        if (!tagValue.equals(other.tagValue))
            return false;

        return true;
    }
    
    public String toString() {
        return tagValue.toString();
    }

}
