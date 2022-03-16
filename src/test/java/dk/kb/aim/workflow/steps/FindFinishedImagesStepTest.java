package dk.kb.aim.workflow.steps;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.exception.MissingRecordException;
import dk.kb.aim.model.Image;
import dk.kb.aim.model.WordConfidence;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.ImageStatus;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.repository.WordStatus;
import dk.kb.cumulus.CumulusRecord;

@SpringBootTest
public class FindFinishedImagesStepTest {

    @Test
    public void testRunStepUnfinishedFinished() {
        // When only an 'Unfinished' image is found, but it has no more pending words.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        CumulusRecord record = mock(CumulusRecord.class);
        WordConfidence wordConfidence = mock(WordConfidence.class);
        
        String cumulusId = UUID.randomUUID().toString();
        int id = new Random().nextInt();
        String color = "color";
        String ocr = "ocr-" + UUID.randomUUID().toString();
        
        when(image.getId()).thenReturn(id);
        when(image.getCumulusId()).thenReturn(cumulusId);
        when(image.getColor()).thenReturn(color);
        when(image.getIsFront()).thenReturn(true);
        when(image.getOcr()).thenReturn(ocr);

        when(imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)).thenReturn(Arrays.asList(image));
        when(imageRepo.listImagesWithStatus(ImageStatus.NEW)).thenReturn(new ArrayList<Image>());
        when(retriever.findRecord(eq(catalogName), eq(cumulusId))).thenReturn(record);
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.PENDING))).thenReturn(new ArrayList<WordConfidence>());
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.ACCEPTED))).thenReturn(new ArrayList<WordConfidence>());

        step.runStep();
        
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_AIM_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_AIM_STATUS_DONE));
        verify(record).setBooleanValueInField(eq(CumulusRetriever.FIELD_NAME_READY_FOR_AIM), eq(Boolean.FALSE));
        //verify(record).setStringValueInField(eq(CumulusRetriever.FIELD_NAME_KEYWORDS), anyString());
        verify(record).setStringValueInField(eq(CumulusRetriever.FIELD_NAME_COLOR_CODES), eq(color));
        verify(record).setStringValueInField(eq(CumulusRetriever.FIELD_NAME_FORSIDE_TEKST), eq(ocr));
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_HAANDSKRIFT), eq(CumulusRetriever.FIELD_HAANDSKRIFT_VALUE_NO));
        verifyNoMoreInteractions(record);
        
        verify(image, times(5)).getCumulusId();
        verify(image, times(2)).getId();
        verify(image, times(2)).getColor();
        verify(image, times(2)).getOcr();
        verify(image).getIsFront();
        verify(image).setStatus(eq(ImageStatus.FINISHED));
        verify(image).getStatus();
        verifyNoMoreInteractions(image);

        verify(imageRepo).listImagesWithStatus(ImageStatus.UNFINISHED);
        verify(imageRepo).listImagesWithStatus(ImageStatus.NEW);
        verify(imageRepo).updateImage(eq(image));
        verifyNoMoreInteractions(imageRepo);

        verify(conf, times(2)).isTest();
        verifyNoMoreInteractions(conf);

        verify(retriever).findRecord(eq(catalogName), eq(cumulusId));
        verifyNoMoreInteractions(retriever);

        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.PENDING));
        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.ACCEPTED));
        verifyNoMoreInteractions(wordRepo);
        
        verifyZeroInteractions(wordConfidence);
    }
    
    @Test
    public void testRunStepNewFinished() {
        // When only an 'new' image is found, but it has no more pending words.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        CumulusRecord record = mock(CumulusRecord.class);
        WordConfidence wordConfidence = mock(WordConfidence.class);
        
        String cumulusId = UUID.randomUUID().toString();
        int id = new Random().nextInt();
        String color = "color";

        when(image.getId()).thenReturn(id);
        when(image.getCumulusId()).thenReturn(cumulusId);
        when(image.getColor()).thenReturn(color);

        when(imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)).thenReturn(new ArrayList<Image>());
        when(imageRepo.listImagesWithStatus(ImageStatus.NEW)).thenReturn(Arrays.asList(image));
        when(retriever.findRecord(eq(catalogName), eq(cumulusId))).thenReturn(record);
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.PENDING))).thenReturn(new ArrayList<WordConfidence>());
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.ACCEPTED))).thenReturn(new ArrayList<WordConfidence>());

        step.runStep();
        
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_AIM_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_AIM_STATUS_DONE));
        verify(record).setBooleanValueInField(eq(CumulusRetriever.FIELD_NAME_READY_FOR_AIM), eq(Boolean.FALSE));
        verify(record).setStringValueInField(eq(CumulusRetriever.FIELD_NAME_COLOR_CODES), eq(color));
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_HAANDSKRIFT), eq(CumulusRetriever.FIELD_HAANDSKRIFT_VALUE_NO));
        verifyNoMoreInteractions(record);
        
        verify(image, times(5)).getCumulusId();
        verify(image, times(2)).getId();
        verify(image, times(2)).getColor();
        verify(image).getOcr();
        verify(image).setStatus(eq(ImageStatus.FINISHED));
        verify(image).getStatus();
        verifyNoMoreInteractions(image);

        verify(imageRepo).listImagesWithStatus(ImageStatus.UNFINISHED);
        verify(imageRepo).listImagesWithStatus(ImageStatus.NEW);
        verify(imageRepo).updateImage(eq(image));
        verifyNoMoreInteractions(imageRepo);

        verify(conf, times(2)).isTest();
        verifyNoMoreInteractions(conf);

        verify(retriever).findRecord(eq(catalogName), eq(cumulusId));
        verifyNoMoreInteractions(retriever);

        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.PENDING));
        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.ACCEPTED));
        verifyNoMoreInteractions(wordRepo);
        
        verifyZeroInteractions(wordConfidence);
    }
    
    @Test
    public void testRunStepNewUnfinished() {
        // When only an 'new' image is found, but it throws an error.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        CumulusRecord record = mock(CumulusRecord.class);
        WordConfidence wordConfidence = mock(WordConfidence.class);
        
        String cumulusId = UUID.randomUUID().toString();
        int id = new Random().nextInt();
        
        when(image.getId()).thenReturn(id);
        when(image.getCumulusId()).thenReturn(cumulusId);

        when(imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)).thenReturn(new ArrayList<Image>());
        when(imageRepo.listImagesWithStatus(ImageStatus.NEW)).thenReturn(Arrays.asList(image));
        when(retriever.findRecord(eq(catalogName), eq(cumulusId))).thenReturn(record);
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.PENDING))).thenReturn(Arrays.asList(wordConfidence));

        step.runStep();
        
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_AIM_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_AIM_STATUS_AWATING));
        verifyNoMoreInteractions(record);

        verify(image, times(2)).getCumulusId();
        verify(image).getId();
        verify(image).setStatus(eq(ImageStatus.UNFINISHED));
        verify(image).getStatus();
        verifyNoMoreInteractions(image);
        
        verify(imageRepo).listImagesWithStatus(ImageStatus.UNFINISHED);
        verify(imageRepo).listImagesWithStatus(ImageStatus.NEW);
        verify(imageRepo).updateImage(eq(image));
        verifyNoMoreInteractions(imageRepo);

        verify(conf).isTest();
        verifyNoMoreInteractions(conf);

        verify(retriever).findRecord(eq(catalogName), eq(cumulusId));
        verifyNoMoreInteractions(retriever);

        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.PENDING));
        verifyNoMoreInteractions(wordRepo);
        
        verifyZeroInteractions(wordConfidence);
    }
    
    @Test
    public void testRunStepMissingUnfinished() {
        // When only an 'unfinished' image is found, but it throws an error.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        
        String cumulusId = UUID.randomUUID().toString();
        int id = new Random().nextInt();
        
        when(image.getId()).thenReturn(id);
        when(image.getCumulusId()).thenReturn(cumulusId);
        
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.PENDING))).thenReturn(new ArrayList<WordConfidence>());

        when(imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)).thenReturn(Arrays.asList(image));
        when(imageRepo.listImagesWithStatus(ImageStatus.NEW)).thenReturn(new ArrayList<Image>());
        when(retriever.findRecord(eq(catalogName), eq(cumulusId))).thenThrow(new MissingRecordException("TEST", "TEST"));
        
        step.runStep();

        verify(image, times(2)).getCumulusId();
        verify(image).getId();
        verifyNoMoreInteractions(image);
        
        verify(imageRepo).listImagesWithStatus(ImageStatus.UNFINISHED);
        verify(imageRepo).listImagesWithStatus(ImageStatus.NEW);
        verify(imageRepo).removeImage(eq(image));
        verifyNoMoreInteractions(imageRepo);

        verify(conf).isTest();
        verifyNoMoreInteractions(conf);

        verify(retriever).findRecord(eq(catalogName), eq(cumulusId));
        verifyNoMoreInteractions(retriever);

        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.PENDING));
        verifyNoMoreInteractions(wordRepo);
    }
    
    @Test
    public void testRunStepMissingNew() {
        // When only an 'new' image is found, but it throws an error.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        
        String cumulusId = UUID.randomUUID().toString();

        when(image.getCumulusId()).thenReturn(cumulusId);

        when(imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)).thenReturn(new ArrayList<Image>());
        when(imageRepo.listImagesWithStatus(ImageStatus.NEW)).thenReturn(Arrays.asList(image));
        when(retriever.findRecord(eq(catalogName), eq(cumulusId))).thenThrow(new MissingRecordException("TEST", "TEST"));
        
        step.runStep();

        verify(image, times(2)).getCumulusId();
        verifyNoMoreInteractions(image);
        
        verify(imageRepo).listImagesWithStatus(ImageStatus.UNFINISHED);
        verify(imageRepo).listImagesWithStatus(ImageStatus.NEW);
        verify(imageRepo).removeImage(eq(image));
        verifyNoMoreInteractions(imageRepo);

        verify(conf).isTest();
        verifyNoMoreInteractions(conf);

        verify(retriever).findRecord(eq(catalogName), eq(cumulusId));
        verifyNoMoreInteractions(retriever);

        verifyZeroInteractions(wordRepo);
    }
    
    @Test
    public void testRunStepErrorUnfinished() {
        // When only an 'unfinished' image is found, but it throws an error.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        
        String cumulusId = UUID.randomUUID().toString();
        int id = new Random().nextInt();
        
        when(image.getId()).thenReturn(id);
        when(image.getCumulusId()).thenReturn(cumulusId);
        
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.PENDING))).thenReturn(new ArrayList<WordConfidence>());

        when(imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)).thenReturn(Arrays.asList(image));
        when(imageRepo.listImagesWithStatus(ImageStatus.NEW)).thenReturn(new ArrayList<Image>());
        when(retriever.findRecord(eq(catalogName), eq(cumulusId))).thenThrow(new RuntimeException("TEST"));
        
        step.runStep();

        verify(image, times(2)).getCumulusId();
        verify(image).getId();
        verifyNoMoreInteractions(image);
        
        verify(imageRepo).listImagesWithStatus(ImageStatus.UNFINISHED);
        verify(imageRepo).listImagesWithStatus(ImageStatus.NEW);
        verifyNoMoreInteractions(imageRepo);

        verify(conf).isTest();
        verifyNoMoreInteractions(conf);

        verify(retriever).findRecord(eq(catalogName), eq(cumulusId));
        verifyNoMoreInteractions(retriever);

        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.PENDING));
        verifyNoMoreInteractions(wordRepo);
    }
    
    @Test
    public void testRunStepErrorNew() {
        // When only an 'new' image is found, but it throws an error.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        
        String cumulusId = UUID.randomUUID().toString();

        when(image.getCumulusId()).thenReturn(cumulusId);

        when(imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)).thenReturn(new ArrayList<Image>());
        when(imageRepo.listImagesWithStatus(ImageStatus.NEW)).thenReturn(Arrays.asList(image));
        when(retriever.findRecord(eq(catalogName), eq(cumulusId))).thenThrow(new RuntimeException("TEST"));
        
        step.runStep();

        verify(image, times(2)).getCumulusId();
        verifyNoMoreInteractions(image);
        
        verify(imageRepo).listImagesWithStatus(ImageStatus.UNFINISHED);
        verify(imageRepo).listImagesWithStatus(ImageStatus.NEW);
        verifyNoMoreInteractions(imageRepo);

        verify(conf).isTest();
        verifyNoMoreInteractions(conf);

        verify(retriever).findRecord(eq(catalogName), eq(cumulusId));
        verifyNoMoreInteractions(retriever);

        verifyZeroInteractions(wordRepo);
    }
    
    @Test
    public void testSetFinishedFull() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);

        CumulusRecord record = mock(CumulusRecord.class);
        Image image = mock(Image.class);
        WordConfidence word1 = mock(WordConfidence.class);
        
        int id = new Random().nextInt();
        String color = UUID.randomUUID().toString();
        String ocr = UUID.randomUUID().toString();
        
        String word1da = UUID.randomUUID().toString();
        String word1en = FindFinishedImagesStep.LABEL_HANDWRITING;
        
        when(image.getId()).thenReturn(id);
        when(image.getColor()).thenReturn(color);
        when(image.getOcr()).thenReturn(ocr);
        when(image.getIsFront()).thenReturn(false);
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.ACCEPTED))).thenReturn(Arrays.asList(word1));
        when(conf.isTest()).thenReturn(false);
        when(word1.getTextDa()).thenReturn(word1da);
        when(word1.getTextEn()).thenReturn(word1en);
        
        step.setFinished(record, image);
        
        verify(image, times(4)).getCumulusId();
        verify(image).getId();
        verify(image, times(2)).getColor();
        verify(image, times(2)).getOcr();
        verify(image).getIsFront();
        verify(image).setStatus(eq(ImageStatus.FINISHED));
        verify(image).getStatus();
        verifyNoMoreInteractions(image);
        
        verify(record).setStringValueInField(eq(CumulusRetriever.FIELD_NAME_KEYWORDS), anyString());
        verify(record).setStringValueInField(eq(CumulusRetriever.FIELD_NAME_COLOR_CODES), eq(color));
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_AIM_STATUS), eq(CumulusRetriever.FIELD_VALUE_AIM_STATUS_DONE));
        verify(record).setBooleanValueInField(eq(CumulusRetriever.FIELD_NAME_READY_FOR_AIM), eq(Boolean.FALSE));
        verify(record).setStringValueInField(eq(CumulusRetriever.FIELD_NAME_BAGSIDE_TEKST), eq(ocr));
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_HAANDSKRIFT), eq(CumulusRetriever.FIELD_HAANDSKRIFT_VALUE_YES));
        verifyNoMoreInteractions(record);
        
        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.ACCEPTED));
        verifyNoMoreInteractions(wordRepo);
        
        verify(imageRepo).updateImage(eq(image));
        verifyNoMoreInteractions(imageRepo);

        verify(conf).isTest();
        verifyNoMoreInteractions(conf);
        
        verifyZeroInteractions(retriever);
    }
    
    @Test
    public void testSetFinishedNoWordsNoColorNoOcrAndTest() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);

        CumulusRecord record = mock(CumulusRecord.class);
        Image image = mock(Image.class);
        
        int id = new Random().nextInt();
        String color = "";
        String ocr = "";
        
        when(image.getId()).thenReturn(id);
        when(image.getColor()).thenReturn(color);
        when(image.getOcr()).thenReturn(ocr);
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.ACCEPTED))).thenReturn(new ArrayList<WordConfidence>());
        when(conf.isTest()).thenReturn(true);
        
        step.setFinished(record, image);
        
        verify(image, times(4)).getCumulusId();
        verify(image).getId();
        verify(image).getColor();
        verify(image).getOcr();
        verify(image).setStatus(eq(ImageStatus.FINISHED));
        verify(image).getStatus();
        verifyNoMoreInteractions(image);
        
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_AIM_STATUS), eq(FindFinishedImagesStep.EMPTY_STRING));
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_HAANDSKRIFT), eq(CumulusRetriever.FIELD_HAANDSKRIFT_VALUE_NO));
        verifyNoMoreInteractions(record);
        
        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.ACCEPTED));
        verifyNoMoreInteractions(wordRepo);
        
        verify(imageRepo).updateImage(eq(image));
        verifyNoMoreInteractions(imageRepo);

        verify(conf).isTest();
        verifyNoMoreInteractions(conf);
        
        verifyZeroInteractions(retriever);
    }
    
    @Test
    public void testGetKeywords() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        WordConfidence word1 = mock(WordConfidence.class);
        WordConfidence word2 = mock(WordConfidence.class);
        
        String daText1 = UUID.randomUUID().toString(); 
        String daText2 = UUID.randomUUID().toString();
        String enText1 = UUID.randomUUID().toString(); 
        String enText2 = UUID.randomUUID().toString();
        
        when(word1.getTextDa()).thenReturn(daText1);
        when(word1.getTextEn()).thenReturn(enText1);
        when(word2.getTextDa()).thenReturn(daText2);
        when(word2.getTextEn()).thenReturn(enText2);
        
        String s = step.getKeywords(Arrays.asList(word1, word2));
        
        Assert.assertTrue(s.contains(daText1));
        Assert.assertTrue(s.contains(daText2));
        Assert.assertTrue(s.contains(enText1));
        Assert.assertTrue(s.contains(enText2));
        
        verify(word1).getTextDa();
        verify(word1).getTextEn();
        verifyNoMoreInteractions(word1);
        
        verify(word2).getTextDa();
        verify(word2).getTextEn();
        verifyNoMoreInteractions(word2);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(retriever);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
    }
    
    @Test
    public void testSetUnfinished() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        step.setUnfinished(record, image);
        
        verify(record).setStringEnumValueForField(eq(CumulusRetriever.FIELD_NAME_AIM_STATUS), 
                eq(CumulusRetriever.FIELD_VALUE_AIM_STATUS_AWATING));
        verifyNoMoreInteractions(record);
        
        verify(image).setStatus(ImageStatus.UNFINISHED);
        verify(image).getStatus();
        verify(image).getCumulusId();
        verifyNoMoreInteractions(image);
        
        verify(imageRepo).updateImage(eq(image));
        verifyNoMoreInteractions(imageRepo);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(retriever);
        verifyZeroInteractions(wordRepo);
    }
    
    @Test
    public void testIsFinishedTrue() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        int id = new Random().nextInt();
        
        when(image.getId()).thenReturn(id);
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.PENDING))).thenReturn(new ArrayList<WordConfidence>());
        
        Assert.assertTrue(step.isFinished(image));
        
        verify(image).getId();
        verifyNoMoreInteractions(image);
        
        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.PENDING));
        verifyNoMoreInteractions(wordRepo);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(retriever);
        verifyZeroInteractions(imageRepo);
    }
    
    @Test
    public void testIsFinishedFalse() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Image image = mock(Image.class);
        int id = new Random().nextInt();
        
        WordConfidence wordConfidence = mock(WordConfidence.class);
        
        when(image.getId()).thenReturn(id);
        when(wordRepo.getImageWords(eq(id), eq(WordStatus.PENDING))).thenReturn(Arrays.asList(wordConfidence));
        
        Assert.assertFalse(step.isFinished(image));
        
        verify(image).getId();
        verifyNoMoreInteractions(image);
        
        verify(wordRepo).getImageWords(eq(id), eq(WordStatus.PENDING));
        verifyNoMoreInteractions(wordRepo);
        
        verifyZeroInteractions(wordConfidence);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(retriever);
        verifyZeroInteractions(imageRepo);
    }
    
    @Test
    public void testGetName() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever retriever = mock(CumulusRetriever.class); 
        String catalogName = UUID.randomUUID().toString();
        ImageRepository imageRepo = mock(ImageRepository.class);
        WordRepository wordRepo = mock(WordRepository.class);
        FindFinishedImagesStep step = new FindFinishedImagesStep(conf, retriever, catalogName, imageRepo, wordRepo);
        
        Assert.assertEquals(FindFinishedImagesStep.STEP_NAME, step.getName());
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(retriever);
        verifyZeroInteractions(imageRepo);
        verifyNoMoreInteractions(wordRepo);
    }

}
