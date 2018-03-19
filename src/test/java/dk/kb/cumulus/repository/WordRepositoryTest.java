package dk.kb.cumulus.repository;

import dk.kb.cumulus.model.Word;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by dgj on 08-03-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WordRepositoryTest {

    @Autowired
    WordRepository wordRepository;

    @Test
    public void createAndRetreiveWord() {
        int id = wordRepository.createWord(new Word("Hors","hæst","cat1","status1"));
        Word retrievedWord = wordRepository.getWord(id);
        Assert.assertEquals("Hors",retrievedWord.getText_en());
        Assert.assertEquals("hæst",retrievedWord.getText_da());
        Assert.assertEquals("cat1",retrievedWord.getCategory());
        Assert.assertEquals("status1",retrievedWord.getStatus());
        retrievedWord.setText_en("Horse");
        retrievedWord.setText_da("Hest");
        retrievedWord.setStatus("status2");
        retrievedWord.setCategory("cat2");
        wordRepository.updateWord(retrievedWord);
        retrievedWord = wordRepository.getWord(id);
        Assert.assertEquals("Horse",retrievedWord.getText_en());
        Assert.assertEquals("Hest",retrievedWord.getText_da());
        Assert.assertEquals("cat2",retrievedWord.getCategory());
        Assert.assertEquals("status2",retrievedWord.getStatus());

    }


}
