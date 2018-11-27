package dk.kb.aim;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;

import dk.kb.aim.GoogleRetreiver;
import dk.kb.aim.model.Image;
import dk.kb.aim.model.Word;
import dk.kb.aim.model.WordConfidence;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by dgj on 26-03-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleRetreiverTest {

    @Autowired
    WordRepository wordRepository;

    @Autowired
    ImageRepository imageRepository;

    @Test
    public void testIt() throws Exception {
//      Requires the environment variable: GOOGLE_APPLICATION_CREDENTIALS
        
        
//        for(Image image : imageRepository.listAllImages()) {
//            imageRepository.removeImage(image);
//        }
//        
//        Assert.assertTrue(imageRepository.listAllImages().isEmpty());
//        
//        GoogleRetreiver googleRetreiver = new GoogleRetreiver();
//        googleRetreiver.wordRepository = wordRepository;
//        googleRetreiver.imageRepository = imageRepository;
//        String imgPath = "src" + File.separator +
//                         "main" + File.separator +
//                         "webapp" + File.separator +
//                         "image_store" + File.separator +
//                         "hest.jpeg";
//
//        googleRetreiver.createImageAndRetreiveLabels(new File(imgPath),"test1234","Mammals");
//        
//        List<Image> images = imageRepository.listAllImages();
//        Assert.assertFalse(images.isEmpty());
//        Assert.assertEquals(images.size(), 1);
//        List<WordConfidence> words = wordRepository.getImageWords(images.get(0).getId());
//        Assert.assertFalse(words.isEmpty());
//        System.out.println("First confidence: " + words.get(0).getConfidence());
//        Assert.assertTrue(words.get(0).getConfidence() > 0);
    }
    
//    @Test
    // DOES NOT WORK, SINCE WE CANNOT MOCK ANNOTATIONS
    public void testCreateImageWordsForLabelAnnotations() throws Exception {
        GoogleRetreiver googleRetreiver = new GoogleRetreiver();
        
        WordRepository wordRepository = Mockito.mock(WordRepository.class);
        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Image dbImage = Mockito.mock(Image.class);
        AnnotateImageResponse response = Mockito.mock(AnnotateImageResponse.class);
        Word dbWord = Mockito.mock(Word.class);
        EntityAnnotation annotation = Mockito.mock(EntityAnnotation.class);
        
        googleRetreiver.wordRepository = wordRepository;
        googleRetreiver.imageRepository = imageRepository;
        
        int imageId = new Random().nextInt();
        int wordId = new Random().nextInt();
        String categoryName = UUID.randomUUID().toString();
        String annotationText = UUID.randomUUID().toString();
        
        float confidence = 0.12345f;
        int expectedConfidence = 12;
        
        Mockito.when(response.getLabelAnnotationsList()).thenReturn(Arrays.asList(annotation));
        Mockito.when(response.hasError()).thenReturn(false);
        
        Mockito.when(dbImage.getId()).thenReturn(imageId);
        Mockito.when(dbImage.getCategory()).thenReturn(categoryName);
        Mockito.when(dbWord.getId()).thenReturn(wordId);
        
        Mockito.when(annotation.getDescription()).thenReturn(annotationText);
        Mockito.when(annotation.getConfidence()).thenReturn(confidence);
        
        Mockito.when(wordRepository.getWordByText(Mockito.eq(annotationText), Mockito.eq(categoryName))).thenReturn(dbWord);
        
        googleRetreiver.createImageWordsForLabelAnnotations(dbImage, Arrays.asList(response));
        
        Mockito.verify(wordRepository).getWordByText(Mockito.eq(annotationText), Mockito.eq(categoryName));
        Mockito.verifyNoMoreInteractions(wordRepository);
        
        Mockito.verify(imageRepository).addWordToImage(Mockito.eq(imageId), Mockito.eq(wordId), Mockito.eq(expectedConfidence));
        Mockito.verifyNoMoreInteractions(imageRepository);
    }
    
}
