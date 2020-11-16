package dk.kb.aim.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class StringUtilsTest {

    @Test
    public void testInstantiation() {
        Object c = new StringUtils();
        Assert.assertTrue(c instanceof StringUtils);
    }

    @Test
    public void testHasValue() throws IOException {
        Assert.assertTrue(StringUtils.hasValue("value"));
        Assert.assertFalse(StringUtils.hasValue(""));
        Assert.assertFalse(StringUtils.hasValue(null));
    }
}
