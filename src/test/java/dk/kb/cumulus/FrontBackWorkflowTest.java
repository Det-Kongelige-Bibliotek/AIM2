package dk.kb.cumulus;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class FrontBackWorkflowTest extends ExtendedTestCase {

    @Test
    public void testGetParent() {
        addDescription("Test the getParent method");
        CumulusRetriever retriever = Mockito.mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackWorkflow fbw = new FrontBackWorkflow(retriever, catalogName);

        addStep("Test with uuid filename with non-digit as last character", "Does not find a parent");
        String f1 = UUID.randomUUID().toString() + "a";
        String p1 = fbw.getParent(f1);
        Assert.assertNull(p1);

        addStep("Test with an odd digit as last character", "Finds a parent");
        String id2 = UUID.randomUUID().toString();
        String f2 = id2 + "1";
        String p2 = fbw.getParent(f2);
        Assert.assertNotNull(p2);
        Assert.assertEquals(p2, id2 + "0");

        addStep("Test with an even digit as last character", "No parent");
        String id3 = UUID.randomUUID().toString();
        String f3 = id3 + "0";
        String p3 = fbw.getParent(f3);
        Assert.assertNull(p3);

//        addStep("Test with 'extra' back-pages", "Finds a parent");
//        String id4 = UUID.randomUUID().toString();
//        String f4 = id4 + "1_123456";
//        String p4 = fbw.getParent(f4);
//        Assert.assertNotNull(p4);
//        Assert.assertEquals(p4, id4 + "0");

    }
}
