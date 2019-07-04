package dk.kb.aim.repository;

import org.junit.Assert;
import org.junit.Before;
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
import java.util.UUID;

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
    
    @Before
    public void setup() {
        for(Image image : imageRepository.listAllImages()) {
            imageRepository.removeImage(image);
        }
    }

    @Test
    public void testCreateAndRetreiveImage() {
        Image image = new Image(-1,"/tmp/test.jpg","1234","category","red","ocr", ImageStatus.NEW, true);
        int id = imageRepository.createImage(image);
        Image retrievedImage = imageRepository.getImage(id);

        Assert.assertEquals(image.getCategory(), retrievedImage.getCategory());
        Assert.assertEquals(image.getColor(), retrievedImage.getColor());
        Assert.assertEquals(image.getCumulusId(), retrievedImage.getCumulusId());
        Assert.assertEquals(image.getOcr(), retrievedImage.getOcr());
        Assert.assertEquals(image.getPath(), retrievedImage.getPath());
        Assert.assertEquals(image.getStatus(), retrievedImage.getStatus());
        Assert.assertEquals(image.getIsFront(), retrievedImage.getIsFront());
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
        Image image = new Image(-1, "src/test/resources/image.tif", "image1.tif", "category", "red", "ocr", ImageStatus.NEW, true);
        int imageId = imageRepository.createImage(image);
        image.setId(imageId);
        
        image.setColor("Invisible");
        image.setOcr("Unrecognizable jibberish");
        
        imageRepository.updateImage(image);
    }
    
    @Test
    public void testFindFinishedImages() throws Exception {
        String catagory = UUID.randomUUID().toString();
        int imageId1 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image1.tif", catagory, "red", "ocr", ImageStatus.NEW, true));
        int imageId2 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image2.tif", catagory, "blue", "ocr", ImageStatus.NEW, true));
        int imageId3 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image3.tif", catagory, "green", "ocr", ImageStatus.NEW, true));
        
        int wordId1 = wordRepository.createWord(new Word("Word", "MS Word", catagory, WordStatus.PENDING));
        int wordId2 = wordRepository.createWord(new Word("Adjective", "Tillægsord", catagory, WordStatus.REJECTED));
        int wordId3 = wordRepository.createWord(new Word("Gnu", "Gnu", catagory, WordStatus.ACCEPTED));
        
        imageRepository.addWordToImage(imageId1, wordId1, 70);
        imageRepository.addWordToImage(imageId2, wordId2, 80);
        imageRepository.addWordToImage(imageId3, wordId3, 90);
        
//        Assert.assertEquals(3, imageRepository.listAllImages().size());
//        Assert.assertEquals(3, imageRepository.listImagesWithStatus(ImageStatus.NEW).size());
//        Assert.assertEquals(0, imageRepository.listImagesWithStatus(ImageStatus.FINISHED).size());
        Assert.assertEquals(3, imageRepository.listImagesInCategory(catagory).size());
        Assert.assertEquals(0, imageRepository.listImagesInCategory("NOT THE CATEGORY").size());
        Assert.assertEquals(3, imageRepository.listImagesInCategoryWithStatus(catagory, ImageStatus.NEW).size());
        Assert.assertEquals(0, imageRepository.listImagesInCategoryWithStatus(catagory, ImageStatus.UNFINISHED).size());
        Assert.assertEquals(0, imageRepository.listImagesInCategoryWithStatus("NOT THE CATEGORY", ImageStatus.UNFINISHED).size());
    }
    
    @Test
    public void testRemovingImages() throws Exception {
        int imageId1 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image1.tif", "category", "red", "ocr", ImageStatus.NEW, true));
        
        int wordId1 = wordRepository.createWord(new Word("Word", "MS Word", "category", WordStatus.PENDING));
        
        imageRepository.addWordToImage(imageId1, wordId1, 70);

        Image image1 = imageRepository.getImage(imageId1);
        
        Assert.assertEquals(1, wordRepository.getImageWords(image1.getId()).size());
        Assert.assertEquals(1, imageRepository.wordImages(wordId1, ImageStatus.NEW).size());
        
        imageRepository.removeImage(image1);
        
        Assert.assertNull(imageRepository.getImage(imageId1));
        Assert.assertEquals(0, imageRepository.wordImages(wordId1, ImageStatus.NEW).size());
        Assert.assertEquals(0, imageRepository.wordImages(wordId1, ImageStatus.FINISHED).size());
        Assert.assertEquals(0, imageRepository.wordImages(wordId1, ImageStatus.UNFINISHED).size());
        
    }
    
    @Test
    public void testListImages() {
        String catagory = UUID.randomUUID().toString();
        int imageId1 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image1.tif", catagory, "red", "ocr", ImageStatus.NEW, true));
        int imageId2 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image2.tif", catagory, "blue", "ocr", ImageStatus.NEW, true));
        int imageId3 = imageRepository.createImage(new Image(-1, "src/test/resources/image.tif", "image3.tif", catagory, "green", "ocr", ImageStatus.NEW, true));

        int allImages = imageRepository.listAllImages().size();
        Assert.assertEquals(3, allImages);
        List<Image> images;
        images = imageRepository.listImages(1, 0);
        Assert.assertEquals(images.size(), 1);
        Assert.assertEquals(images.get(0).getId(), imageId3);
        
        images = imageRepository.listImages(1, 1);
        Assert.assertEquals(images.size(), 1);
        Assert.assertEquals(images.get(0).getId(), imageId2);

        images = imageRepository.listImages(1, 2);
        Assert.assertEquals(images.size(), 1);
        Assert.assertEquals(images.get(0).getId(), imageId1);

        images = imageRepository.listImages(2, 1);
        Assert.assertEquals(images.size(), 2);
        Assert.assertEquals(images.get(0).getId(), imageId2);
        Assert.assertEquals(images.get(1).getId(), imageId1);

//        Assert.assertEquals(imageRepository.listImages(1, 2).size(), 1);
//        Assert.assertEquals(imageRepository.listImages(1, 2).size(), 1);
        
    }
}
