package dk.kb.aim.utils;

/**
 * Utility class for methods handling strings.
 */
public class StringUtils {

    /**
     * Whether or not the text has a value.
     * Thus if it is neither null nor empty.
     * @param text The string to validate.
     * @return Whether or not it has a value.
     */
    public static boolean hasValue(String text) {
        return text != null && !text.isEmpty();
    }
}
