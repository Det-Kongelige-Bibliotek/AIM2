package dk.kb.aim.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import dk.kb.aim.Configuration;
import dk.kb.aim.TestUtils;
import dk.kb.aim.utils.ImageConverter;

@SpringBootTest
public class ImageConverterTest {
    
    static Configuration conf;
    
    @BeforeClass
    public static void setup() {
        conf = TestUtils.getTestConfiguration();
    }
    
    @Test
    public void testConvertTiff() throws IOException {
        ImageConverter ic = new ImageConverter();
        ic.conf = conf;
        
        File tiff = new File("src/test/resources/image.tif");
        File jpg = ic.convertTiff(tiff);
        
        Assert.assertTrue(jpg.length() < tiff.length());
        Assert.assertTrue(jpg.length() > 100000L);
    }
    
    @Test
    public void testConvertTiffSeveralTimes() throws IOException {
        ImageConverter ic = new ImageConverter();
        Configuration testConf = mock(Configuration.class);
        ic.conf = testConf;
        
        when(testConf.getJpegFolder()).thenReturn(conf.getJpegFolder());
        when(testConf.getJpegSizeLimit()).thenReturn(100000L);
        
        File tiff = new File("src/test/resources/image.tif");
        File jpg = ic.convertTiff(tiff);
        
        Assert.assertTrue(jpg.length() < tiff.length());
        Assert.assertTrue(jpg.length() < 100000L);
    }
}
