package dk.kb.cumulus;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConfigurationTest {
    
    @Test
    public void testInstantiation() throws Exception {
        File f = new File("src/test/resources/aim.yml");
        Assert.assertTrue(f.isFile());
        
        Configuration conf = new Configuration(f);
    }
}
