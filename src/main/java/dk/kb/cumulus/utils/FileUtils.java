package dk.kb.cumulus.utils;

import java.io.File;

/**
 * 
 * @author jolf
 *
 */
public class FileUtils {

    /**
     * Retrieves the directory at the given path.
     * If the path already point to an existing directory, then it is just returned.
     * Otherwise the directory will be created.
     * This method will throw an argument exception, if it cannot create the directory.
     * 
     * @param path The path.
     * @return The directory.
     */
    public static File getDirectory(String path) {
        File res = new File(path);
        if(!res.isDirectory()) {
            boolean success = res.mkdirs();
            if(!success) {
                throw new IllegalArgumentException("Could not instantiate directory at '" + path + "'");
            }
        }
        
        return res;
    }
}
