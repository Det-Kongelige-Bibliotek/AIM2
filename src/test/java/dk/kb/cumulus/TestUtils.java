package dk.kb.cumulus;

import java.io.File;
import java.io.IOException;

import org.junit.Assume;

public class TestUtils {

    public static Configuration getTestConfiguration() {
        Configuration conf = null;
        try {
            File f = new File("aim.yml");
            Assume.assumeTrue("Local configuration file exists", f.isFile());
            conf = new Configuration(f);
        } catch (IOException e) {
            Assume.assumeNoException(e);
        }
        return conf;
    }
}
