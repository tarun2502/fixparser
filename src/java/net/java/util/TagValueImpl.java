package net.java.util;

/**
 * FIX Field TagValue pair implementation.
 * 
 * @author Leonid Shlyapnikov
 */
public class TagValueImpl<T, V> implements TagValue<T, V> {

    private final T tag;

    private final V value;

    public TagValueImpl(T tag, V value) {
        if (null == tag) {
            throw new NullPointerException("Argument tag is null!");
        }

        if (null == value) {
            throw new NullPointerException("Argument value is null!");
        }

        this.tag = tag;
        this.value = value;
    }

    public T tag() {
        return tag;
    }

    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(tag) + "=" + String.valueOf(value) + "|";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + tag.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        TagValueImpl other = (TagValueImpl) obj;
        if (!tag.equals(other.tag))
            return false;
        if (!value.equals(other.value))
            return false;
        return true;
    }
}
