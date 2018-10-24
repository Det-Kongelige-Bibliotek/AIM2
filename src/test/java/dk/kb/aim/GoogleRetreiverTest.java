package dk.kb.aim;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.kb.aim.GoogleRetreiver;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;

import java.io.File;

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
        GoogleRetreiver googleRetreiver = new GoogleRetreiver();
        googleRetreiver.wordRepository = wordRepository;
        googleRetreiver.imageRepository = imageRepository;
        String imgPath = "src" + File.separator +
                         "main" + File.separator +
                         "webapp" + File.separator +
                         "image_store" + File.separator +
                         "hest.jpeg";

//        googleRetreiver.createImageAndRetreiveLabels(new File(imgPath),"test1234","Mammals");
    }

}
