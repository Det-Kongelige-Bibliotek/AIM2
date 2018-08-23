package dk.kb.aim.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.kb.aim.model.Image;
import dk.kb.aim.model.Word;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;

import java.util.List;

/**
 * Created by dgj on 06-03-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageRepositoryTest  {

    @Autowired
    ImageRepository imageRepository;
    
    @Autowired
    WordRepository wordRepository;

    @Test
    public void testCreateAndRetreiveImage() {
        int id = imageRepository.createImage(new Image(-1,"/tmp/test.jpg","1234","category","red","ocr", ImageStatus.NEW));
        System.out.println("id is "+id);
        Image retreivedImage = imageRepository.getImage(id);
        System.out.println("Retreived image "+retreivedImage);
    }

    @Test
    public void searchForImages() {
        List<Image> result = imageRepository.listAllImages();
        for(Image image : result) {
            System.out.println(image.getId() + ", " + image.getCumulusId() + ", " + image.getCategory() + ", " + image.getPath() + ", " + image.getColor() + ", " + image.getOcr() + ", " + image.getStatus());
        }
        System.out.println(result.size());
        result = imageRepository.listImagesInCategory("category");
        System.out.println(result.size());
        result = imageRepository.listImagesInCategory("test");
        System.out.println(result.size());
        result = imageRepository.listImagesInCategoryWithStatus("category",ImageStatus.NEW);
        System.out.println(result.size());
    }
    
    @Test
    public void testUpdatingImage() throws Exception {
        Image image = new Image(-1, "src/test/resources/image.tif", "image1.tif", "category", "red", "ocr", ImageStatus.NEW);
        int imageId = imageRepository.createImage(image);
        image.setId(imageId);
        
        image.setColor("Invisible");
        image.setOcr("Unrecognizable jibberish");
        
        imageRepository.updateImage(image);
        
        
    }
    
//    @Test
    public void testFindFinishedImages() throws Exception {
        int imageId1 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image1.tif", "category", "red", "ocr", ImageStatus.NEW));
        int imageId2 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image2.tif", "category", "blue", "ocr", ImageStatus.NEW));
        int imageId3 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image3.tif", "category", "green", "ocr", ImageStatus.NEW));
        
        int wordId1 = wordRepository.createWord(new Word("Word", "MS Word", "category", WordStatus.PENDING));
        int wordId2 = wordRepository.createWord(new Word("Adjective", "Tillægsord", "category", WordStatus.REJECTED));
        int wordId3 = wordRepository.createWord(new Word("Gnu", "Gnu", "category", WordStatus.ACCEPTED));
        
        imageRepository.addWordToImage(imageId1, wordId1, 70);
        imageRepository.addWordToImage(imageId2, wordId2, 80);
        imageRepository.addWordToImage(imageId3, wordId3, 90);
        
        imageRepository.listImagesWithStatus(ImageStatus.NEW);
    }
}
