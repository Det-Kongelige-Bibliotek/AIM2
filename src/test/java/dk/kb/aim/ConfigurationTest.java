package dk.kb.aim;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import dk.kb.aim.Configuration;

@SpringBootTest
public class ConfigurationTest {
    
    @Test
    public void testInstantiation() throws Exception {
        File f = new File("src/test/resources/aim.yml");
        Assert.assertTrue(f.isFile());
        
        Configuration conf = new Configuration(f.getAbsolutePath());
        
        Assert.assertNotNull(conf);
    }
}
