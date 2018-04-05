package dk.kb.aim.workflow.steps;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.GoogleRetreiver;
import dk.kb.aim.utils.ImageConverter;
import dk.kb.aim.workflow.steps.ImportToAimStep;
import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;

@SpringBootTest
public class ImportToAimStepTest {
    
    @Test
    public void testRunWhenNothingToRetrieve() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        ImageConverter imageConverter = mock(ImageConverter.class);
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);
        ImportToAimStep step = new ImportToAimStep(retriever, catalogName, imageConverter, googleRetriever);
        
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        when(records.iterator()).thenReturn(new ArrayList<CumulusRecord>().iterator());
        
        when(retriever.getReadyForAIMRecords(eq(catalogName))).thenReturn(records);
        step.runStep();
        
        verify(retriever).getReadyForAIMRecords(eq(catalogName));
        verifyNoMoreInteractions(retriever);
        verify(records).iterator();
        verifyNoMoreInteractions(records);
        
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(googleRetriever);
    }
    
    @Test
    public void testRunStepWithOneSucces() throws IOException {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        ImageConverter imageConverter = mock(ImageConverter.class);
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);
        ImportToAimStep step = new ImportToAimStep(retriever, catalogName, imageConverter, googleRetriever);

        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        String recordName = UUID.randomUUID().toString();
        Integer categoryId = new Random().nextInt();
        String expectedCategory = UUID.randomUUID().toString();
        List<String> categoryPath = Arrays.asList("test", ImportToAimStep.CATEGORY_NAME_AIM, expectedCategory);
        
        when(record.getCategories()).thenReturn(Arrays.asList(categoryId));
        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(recordName);
        when(records.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(retriever.getCategoryPath(eq(catalogName), eq(categoryId))).thenReturn(categoryPath);
        when(retriever.getReadyForAIMRecords(eq(catalogName))).thenReturn(records);

        step.runStep();
        
        verify(record).getCategories();
        verify(record, times(2)).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_AIM_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_AIM_STATUS_IN_PROCESS));
        //verifyNoMoreInteractions(record);
        verify(records).iterator();
        verifyNoMoreInteractions(records);
        verify(retriever).getReadyForAIMRecords(eq(catalogName));
        verify(retriever).getCategoryPath(eq(catalogName), eq(categoryId));
        verifyNoMoreInteractions(retriever);
//        verify(imageConverter).convertTiff(any(File.class));
//        verifyNoMoreInteractions(imageConverter);
//        verify(googleRetriever).createImageAndRetreiveLabels(any(File.class), eq(recordName), eq(expectedCategory));
//        verifyNoMoreInteractions(googleRetriever);
    }
    
    @Test
    public void testGetAimSubCategoryWithCategory() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        ImageConverter imageConverter = mock(ImageConverter.class);
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);
        ImportToAimStep step = new ImportToAimStep(retriever, catalogName, imageConverter, googleRetriever);

        CumulusRecord record = mock(CumulusRecord.class);
        
        Integer categoryId = new Random().nextInt();
        String expectedCategory = UUID.randomUUID().toString();
        List<String> categoryPath = Arrays.asList("test", ImportToAimStep.CATEGORY_NAME_AIM, expectedCategory, "sub-category");
        
        when(record.getCategories()).thenReturn(Arrays.asList(categoryId));
        when(retriever.getCategoryPath(eq(catalogName), eq(categoryId))).thenReturn(categoryPath);
        
        String actualCategory = step.getAimSubCategory(record);
        Assert.assertEquals(expectedCategory, actualCategory);
        
        verify(record).getCategories();
        verifyNoMoreInteractions(record);
        verify(retriever).getCategoryPath(eq(catalogName), eq(categoryId));
        verifyNoMoreInteractions(retriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(googleRetriever);
    }
    
    @Test
    public void testGetAimSubCategoryWithoutCategory() {
        CumulusRetriever retriever = mock(CumulusRetriever.class);
        String catalogName = UUID.randomUUID().toString();
        ImageConverter imageConverter = mock(ImageConverter.class);
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);
        ImportToAimStep step = new ImportToAimStep(retriever, catalogName, imageConverter, googleRetriever);

        CumulusRecord record = mock(CumulusRecord.class);
        
        Integer categoryId = new Random().nextInt();
        List<String> categoryPath = Arrays.asList("test", "NOTAIM", UUID.randomUUID().toString(), "sub-category");
        
        when(record.getCategories()).thenReturn(Arrays.asList(categoryId));
        when(retriever.getCategoryPath(eq(catalogName), eq(categoryId))).thenReturn(categoryPath);
        
        String actualCategory = step.getAimSubCategory(record);
        Assert.assertEquals(ImportToAimStep.CATEGORY_UNKNOWN, actualCategory);
        
        verify(record).getCategories();
        verifyNoMoreInteractions(record);
        verify(retriever).getCategoryPath(eq(catalogName), eq(categoryId));
        verifyNoMoreInteractions(retriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(googleRetriever);
    }
}
