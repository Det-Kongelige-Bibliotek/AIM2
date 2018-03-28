package dk.kb.cumulus.workflow.steps;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusRetriever;
import dk.kb.cumulus.workflow.steps.FrontBackStep;

@SpringBootTest
public class FrontBackStepTest {
    
    @Test
    @Ignore
    public void testStuff() {
        String id = UUID.randomUUID().toString();
        Assert.assertTrue(id, id.matches("[a-zA-Z0-9\\-]*"));
    }

    @Test
    public void testGetFrontPage() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(retriever, catalogName);

//        addStep("Test with uuid filename with non-digit as last character", "Does not find a front-page");
        String f1 = UUID.randomUUID().toString() + "a";
        String p1 = fbw.getFrontPage(f1);
        Assert.assertNull(p1);

//        addStep("Test with an odd digit as last character", "Finds a front-page");
        String id2 = UUID.randomUUID().toString();
        String f2 = id2 + "1";
        String p2 = fbw.getFrontPage(f2);
        Assert.assertNotNull(p2);
        Assert.assertEquals(p2, id2 + "0");

//        addStep("Test with an even digit as last character", "No front-page");
        String id3 = UUID.randomUUID().toString();
        String f3 = id3 + "0";
        String p3 = fbw.getFrontPage(f3);
        Assert.assertNull(p3);

//        addStep("Test with 'extra' back-pages", "Finds a front-page");
        String id4 = UUID.randomUUID().toString();
        String f4 = id4 + "1_123456";
        String p4 = fbw.getFrontPage(f4);
        Assert.assertNotNull(p4);
        Assert.assertEquals(p4, id4 + "0");

//        addStep("Test with 'extra' back-pages, but not on an 'odd' page", "No front-page");
        String id5 = UUID.randomUUID().toString();
        String f5 = id5 + "2_123456";
        String p5 = fbw.getFrontPage(f5);
        Assert.assertNull(p5);
    }
    
    @Test
    public void testRunWorkflowWhenFrontPageFound() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(retriever, catalogName);
        
        String id = UUID.randomUUID().toString();
        
        CumulusRecord backRecord = mock(CumulusRecord.class);
        String backRecordName = id + "1";
        CumulusRecord frontRecord = mock(CumulusRecord.class);
        String frontRecordName = id + "0";
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        
        when(retriever.getReadyForFrontBackRecords(eq(catalogName))).thenReturn(records);
        when(retriever.findRecord(eq(catalogName), eq(frontRecordName))).thenReturn(frontRecord);
        when(records.iterator()).thenReturn(Arrays.asList(backRecord).iterator());
        
        when(backRecord.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(backRecordName);
        
        fbw.runStep();
        
        verify(retriever).getReadyForFrontBackRecords(eq(catalogName));
        verify(retriever).findRecord(eq(catalogName), eq(frontRecordName));
        verifyNoMoreInteractions(retriever);
        
        verify(backRecord, times(2)).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(backRecord).addMasterAsset(eq(frontRecord));
        verify(backRecord).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS));
        verifyNoMoreInteractions(backRecord);
        
        verifyZeroInteractions(frontRecord);
        
        verify(records).iterator();
        verifyNoMoreInteractions(records);
    }
    
    @Test
    public void testRunWorkflowWhenNoFrontPageFound() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(retriever, catalogName);
        
        String id = UUID.randomUUID().toString();
        
        CumulusRecord backRecord = mock(CumulusRecord.class);
        String backRecordName = id + "2";
        CumulusRecord frontRecord = mock(CumulusRecord.class);
        String frontRecordName = id + "0";
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        
        when(retriever.getReadyForFrontBackRecords(eq(catalogName))).thenReturn(records);
        when(retriever.findRecord(eq(catalogName), eq(frontRecordName))).thenReturn(frontRecord);
        when(records.iterator()).thenReturn(Arrays.asList(backRecord).iterator());
        
        when(backRecord.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(backRecordName);
        
        fbw.runStep();
        
        verify(retriever).getReadyForFrontBackRecords(eq(catalogName));
        verifyNoMoreInteractions(retriever);
        
        verify(backRecord).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(backRecord).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS));
        verifyNoMoreInteractions(backRecord);
        
        verifyZeroInteractions(frontRecord);
        
        verify(records).iterator();
        verifyNoMoreInteractions(records);
    }
    
    @Test
    public void testRunWorkflowWhenFrontPageRecordCouldNotBeFoundInCumulus() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(retriever, catalogName);
        
        String id = UUID.randomUUID().toString();
        
        CumulusRecord backRecord = mock(CumulusRecord.class);
        String backRecordName = id + "1";
        String frontRecordName = id + "0";
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        
        when(retriever.getReadyForFrontBackRecords(eq(catalogName))).thenReturn(records);
        when(retriever.findRecord(eq(catalogName), eq(frontRecordName))).thenReturn(null);
        when(records.iterator()).thenReturn(Arrays.asList(backRecord).iterator());
        
        when(backRecord.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(backRecordName);
        
        fbw.runStep();
        
        verify(retriever).getReadyForFrontBackRecords(eq(catalogName));
        verify(retriever).findRecord(eq(catalogName), eq(frontRecordName));
        verifyNoMoreInteractions(retriever);
        
        verify(backRecord, times(3)).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(backRecord).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS));
        verifyNoMoreInteractions(backRecord);
        
        verify(records).iterator();
        verifyNoMoreInteractions(records);
    }
    
//    @Test
    public void testRegex() {
        String regex = "([a-zA-Z0-9]{1,}[\\-_])*[a-zA-Z0-9]{1,}[.][a-zA-Z0-9]{1,6}";
        
        Assert.assertFalse("".matches(regex));
        Assert.assertTrue("a.tif".matches(regex));
        Assert.assertTrue("txt_digit_0123.suffix".matches(regex));
        Assert.assertTrue("txt-digit-0123.mp3".matches(regex));
        Assert.assertTrue("txt_digit-0123.suffix".matches(regex));
        Assert.assertFalse("txt_digit-0123.VeryLongSuffix".matches(regex));
        Assert.assertFalse("txt_-0123.suffix".matches(regex));
        Assert.assertFalse("txt_digit-0123.tif1.tif2".matches(regex));
        Assert.assertFalse("ÅØÆ-123_xyz.tif".matches(regex));
        Assert.assertFalse("txt-digit--0123.suffix".matches(regex));
        Assert.assertFalse("txt__digit_0123.suffix".matches(regex));
        Assert.assertFalse("txt_digit-0123!#%&.suffix".matches(regex));
    }
}
