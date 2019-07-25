package dk.kb.aim.utils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;

public class FileUtilsTest {

    static File tempDir;

    @BeforeClass
    public static void setup() {
        tempDir = new File("tempDir");
        if(!tempDir.isDirectory()) {
            Assert.assertTrue(tempDir.mkdirs());
        }
    }

    @AfterClass
    public static void tearDown() {
        if(tempDir.listFiles() != null) {
            for (File f : tempDir.listFiles()) {
                Assert.assertTrue(f.delete());
            }
        }
        Assert.assertTrue(tempDir.delete());
    }

    @Test
    public void testInstantiation() {
        Object c = new FileUtils();
        Assert.assertTrue(c instanceof FileUtils);
    }

    @Test
    public void testGetDirectory() {
        String dirPath = new File(tempDir, UUID.randomUUID().toString()).getAbsolutePath();
        Assert.assertFalse(new File(dirPath).isDirectory());
        File dir = FileUtils.getDirectory(dirPath);
        Assert.assertTrue(dir.isDirectory());
        Assert.assertTrue(new File(dirPath).isDirectory());
    }

    @Test
    public void testGetDirectoryAlreadyExists() {
        Assert.assertTrue(tempDir.isDirectory());
        File dir = FileUtils.getDirectory(tempDir.getAbsolutePath());
        Assert.assertTrue(dir.isDirectory());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDirectoryFailure() throws IOException {
        File f = new File(tempDir, UUID.randomUUID().toString());
        try (OutputStream out = new FileOutputStream(f)) {
            out.write(UUID.randomUUID().toString().getBytes());
        }

        FileUtils.getDirectory(f.getAbsolutePath());
    }
}
