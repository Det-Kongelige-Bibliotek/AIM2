package dk.kb.aim.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {
    /**
     * Retrieves the encoding of a byte array (from a String).
     * @param bArray The byte array to interpret.
     * @return The charset for the byte array.
     */
    public static String getCharEncoding(byte[] bArray){
        InputStream is = new ByteArrayInputStream(bArray);
        InputStreamReader reader = new InputStreamReader(is);
        return reader.getEncoding();
    }
}
