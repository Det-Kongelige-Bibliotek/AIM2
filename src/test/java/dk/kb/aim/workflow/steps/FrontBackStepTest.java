package dk.kb.aim.workflow.steps;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;

@SpringBootTest
public class FrontBackStepTest {
    @Test
    public void testGetFrontPage() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        Configuration conf = mock(Configuration.class);
        
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(conf, retriever, catalogName);
        String suffix = "." + UUID.randomUUID().toString();

//        addStep("Test with uuid filename with non-digit as last character", "Does not find a front-page");
        String f1 = UUID.randomUUID().toString() + "a" + suffix;
        String p1 = fbw.getFrontPage(f1);
        Assert.assertNull(p1);

        String id2 = UUID.randomUUID().toString();
        String f2 = id2 + "2" + suffix;
        String p2 = fbw.getFrontPage(f2);
        Assert.assertNotNull(p2);
        Assert.assertEquals(p2, id2 + "1" + suffix);

        String id3 = UUID.randomUUID().toString();
        String f3 = id3 + "3" + suffix;
        String p3 = fbw.getFrontPage(f3);
        Assert.assertNull(p3);

        String id4 = UUID.randomUUID().toString();
        String f4 = id4 + "2_123456" + suffix;
        String p4 = fbw.getFrontPage(f4);
        Assert.assertNotNull(p4);
        Assert.assertEquals(p4, id4 + "1" + suffix);

//        addStep("Test with 'extra' back-pages, but not on an 'odd' page", "No front-page");
        String id5 = UUID.randomUUID().toString();
        String f5 = id5 + "3_123456";
        String p5 = fbw.getFrontPage(f5);
        Assert.assertNull(p5);
        
        String id6 = UUID.randomUUID().toString() + "a";
        String f6 = id6 + "10000000" + suffix;
        String p6 = fbw.getFrontPage(f6);
        Assert.assertNotNull(p6);
        Assert.assertEquals(p6, id6 + "09999999" + suffix);
        
        String id7 = "RU000701.tif";
        String id8 = "RU000702.tif";
        String id9 = "RU000702_1.tif";
        String id10 = "RU000702_2.tif";
        Assert.assertNull(fbw.getFrontPage(id7));
        Assert.assertEquals(id7, fbw.getFrontPage(id8));
        Assert.assertEquals(id7, fbw.getFrontPage(id9));
        Assert.assertEquals(id7, fbw.getFrontPage(id10));
    }
    
    @Test
    public void testGetTrailingDigits() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        Configuration conf = mock(Configuration.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(conf, retriever, catalogName);

        Assert.assertEquals(fbw.getTrailingDigits(""), "");
        Assert.assertEquals(fbw.getTrailingDigits("1234"), "1234");
        Assert.assertEquals(fbw.getTrailingDigits("abc"), "");
        Assert.assertEquals(fbw.getTrailingDigits("abc1234"), "1234");
        Assert.assertEquals(fbw.getTrailingDigits("1234asdf5678"), "5678");
    }
    
    @Test
    public void testRunWorkflowWhenFrontPageFound() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        Configuration conf = mock(Configuration.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(conf, retriever, catalogName);
        
        String id = UUID.randomUUID().toString();
        
        CumulusRecord backRecord = mock(CumulusRecord.class);
        String backRecordName = id + "2";
        CumulusRecord frontRecord = mock(CumulusRecord.class);
        String frontRecordName = id + "1";
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        
        when(retriever.getReadyForFrontBackRecords(eq(catalogName))).thenReturn(records);
        when(retriever.findRecord(eq(catalogName), eq(frontRecordName))).thenReturn(frontRecord);
        when(records.iterator()).thenReturn(Arrays.asList(backRecord).iterator()).thenReturn(Arrays.asList(backRecord).iterator());
        
        when(backRecord.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(backRecordName);

        fbw.runStep();
        
        verify(retriever).getReadyForFrontBackRecords(eq(catalogName));
        verify(retriever).findRecord(eq(catalogName), eq(frontRecordName));
        verifyNoMoreInteractions(retriever);
        
        verify(backRecord, times(2)).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(backRecord).addMasterAsset(eq(frontRecord));
        verify(backRecord).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS));
        verify(backRecord).setStringEnumValueForField(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS, 
                CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_DONE);
        verify(backRecord).setBooleanValueInField(eq(CumulusRetriever.FIELD_NAME_READY_FOR_FRONT_BACK), eq(Boolean.FALSE));
        verifyNoMoreInteractions(backRecord);
        
        verifyZeroInteractions(frontRecord);
        
        verify(records, times(2)).iterator();
        verifyNoMoreInteractions(records);
    }
    
    @Test
    public void testRunWorkflowWhenNoFrontPageFound() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        Configuration conf = mock(Configuration.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(conf, retriever, catalogName);
        
        String id = UUID.randomUUID().toString();
        
        CumulusRecord backRecord = mock(CumulusRecord.class);
        String backRecordName = id + "1";
        CumulusRecord frontRecord = mock(CumulusRecord.class);
        String frontRecordName = id + "0";
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        
        when(retriever.getReadyForFrontBackRecords(eq(catalogName))).thenReturn(records);
        when(retriever.findRecord(eq(catalogName), eq(frontRecordName))).thenReturn(frontRecord);
        when(records.iterator()).thenReturn(Arrays.asList(backRecord).iterator()).thenReturn(Arrays.asList(backRecord).iterator());
        
        when(backRecord.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(backRecordName);

        fbw.runStep();
        
        verify(retriever).getReadyForFrontBackRecords(eq(catalogName));
        verifyNoMoreInteractions(retriever);
        
        verify(backRecord).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(backRecord).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS));
        verify(backRecord).setStringEnumValueForField(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS, 
                CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_DONE);
        verify(backRecord).setBooleanValueInField(eq(CumulusRetriever.FIELD_NAME_READY_FOR_FRONT_BACK), eq(Boolean.FALSE));
        verifyNoMoreInteractions(backRecord);
        
        verifyZeroInteractions(frontRecord);
        
        verify(records, times(2)).iterator();
        verifyNoMoreInteractions(records);
    }
    
    @Test
    public void testRunWorkflowWhenFrontPageRecordCouldNotBeFoundInCumulus() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        Configuration conf = mock(Configuration.class);
        String catalogName = UUID.randomUUID().toString();
        FrontBackStep fbw = new FrontBackStep(conf, retriever, catalogName);
        
        String id = UUID.randomUUID().toString();
        
        CumulusRecord backRecord = mock(CumulusRecord.class);
        String backRecordName = id + "2";
        String frontRecordName = id + "1";
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        
        when(retriever.getReadyForFrontBackRecords(eq(catalogName))).thenReturn(records);
        when(retriever.findRecord(eq(catalogName), eq(frontRecordName))).thenThrow(new IllegalStateException("TESTING"));
        when(records.iterator()).thenReturn(Arrays.asList(backRecord).iterator()).thenReturn(Arrays.asList(backRecord).iterator());
        
        when(backRecord.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(backRecordName);

        fbw.runStep();
        
        verify(retriever).getReadyForFrontBackRecords(eq(catalogName));
        verify(retriever).findRecord(eq(catalogName), eq(frontRecordName));
        verifyNoMoreInteractions(retriever);
        
        verify(backRecord, times(2)).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(backRecord).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS));
        verify(backRecord).setStringEnumValueForField(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS, 
                CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_DONE);
        verify(backRecord).setBooleanValueInField(eq(CumulusRetriever.FIELD_NAME_READY_FOR_FRONT_BACK), eq(Boolean.FALSE));
        verifyNoMoreInteractions(backRecord);
        
        verify(records, times(2)).iterator();
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
