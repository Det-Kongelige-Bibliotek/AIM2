package dk.kb.cumulus;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FrontBackWorkflowTest {
    
    @Test
    @Ignore
    public void testStuff() {
        String id = UUID.randomUUID().toString();
        Assert.assertTrue(id, id.matches("[a-zA-Z0-9\\-]*"));
    }

    @Test
    public void testGetParent() {
//        addDescription("Test the getParent method");
        CumulusRetriever retriever = Mockito.mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackWorkflow fbw = new FrontBackWorkflow(retriever, catalogName);

//        addStep("Test with uuid filename with non-digit as last character", "Does not find a parent");
        String f1 = UUID.randomUUID().toString() + "a";
        String p1 = fbw.getFrontPage(f1);
        Assert.assertNull(p1);

//        addStep("Test with an odd digit as last character", "Finds a parent");
        String id2 = UUID.randomUUID().toString();
        String f2 = id2 + "1";
        String p2 = fbw.getFrontPage(f2);
        Assert.assertNotNull(p2);
        Assert.assertEquals(p2, id2 + "0");

//        addStep("Test with an even digit as last character", "No parent");
        String id3 = UUID.randomUUID().toString();
        String f3 = id3 + "0";
        String p3 = fbw.getFrontPage(f3);
        Assert.assertNull(p3);

//        addStep("Test with 'extra' back-pages", "Finds a parent");
        String id4 = UUID.randomUUID().toString();
        String f4 = id4 + "1_123456";
        String p4 = fbw.getFrontPage(f4);
        Assert.assertNotNull(p4);
        Assert.assertEquals(p4, id4 + "0");

//        addStep("Test with 'extra' back-pages, but not on an 'odd' page", "No parent");
        String id5 = UUID.randomUUID().toString();
        String f5 = id5 + "2_123456";
        String p5 = fbw.getFrontPage(f5);
        Assert.assertNull(p5);

    }
}
