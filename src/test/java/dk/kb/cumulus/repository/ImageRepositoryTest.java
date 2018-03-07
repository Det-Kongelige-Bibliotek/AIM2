package dk.kb.cumulus.repository;

import dk.kb.cumulus.model.Image;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by dgj on 06-03-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageRepositoryTest  {

    @Autowired
    ImageRepository imageRepository;

    @Test
    public void testCreateAndRetreiveImage() {
        int id = imageRepository.createImage(new Image(-1,"/tmp/test.jpg","1234","category","red","ocr","test"));
        System.out.println("id is "+id);
        Image retreivedImage = imageRepository.getImage(id);
        System.out.println("Retreived image "+retreivedImage);
    }

    @Test
    public void searchForImages() {
        List<Image> result = imageRepository.listAllImages();
        System.out.println(result.size());
        result = imageRepository.listImagesInCategory("category");
        System.out.println(result.size());
        result = imageRepository.listImagesInCategory("test");
        System.out.println(result.size());
        result = imageRepository.listImagesInCategoryWithStatus("category","test");
        System.out.println(result.size());
    }
}
