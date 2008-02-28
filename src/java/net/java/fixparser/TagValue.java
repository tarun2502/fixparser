package net.java.fixparser;

/**
 * FIX Message Field/TagValue pair.
 * 
 * @author Leonid Shlyapnikov
 */
public interface TagValue<T, V> {

    /**
     * Field tag number.
     * 
     * @return Field tag number.
     */
    T tag();

    /**
     * Field tag value.
     * 
     * @return Field tag value.
     */
    V value();

}
