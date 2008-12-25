package net.java.fixparser;

import net.java.util.TagValue;

public interface Field extends TagValue<String, String> {

    boolean isRepeatingGroup();

}