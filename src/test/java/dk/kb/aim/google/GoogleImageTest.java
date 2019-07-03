package dk.kb.aim.google;

import com.google.cloud.vision.v1.Image;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class GoogleImageTest {
    @Test
    public void testGoogleImage() throws IOException {
        String tiffPath = "src" + File.separator +
                "test" + File.separator +
                "resources" + File.separator +
                "KE051541.tif";
        File tiffFile = new File(tiffPath);

        GoogleImage image = new GoogleImage(tiffFile);
        Assert.assertNotNull(image);
        Assert.assertNotNull(image.image);
        Assert.assertNotNull(image.getImage());
        Assert.assertTrue(image.getImage() instanceof Image);
    }
}
