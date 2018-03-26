package dk.kb.cumulus.repository;

import dk.kb.cumulus.ImageStatus;
import dk.kb.cumulus.WordStatus;
import dk.kb.cumulus.model.Image;
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

    @Autowired
    ImageRepository imageRepository;

    @Test
    public void createAndRetreiveWord() {
        int id = wordRepository.createWord(new Word("Hors","hæst","cat1", WordStatus.PENDING));
        Word retrievedWord = wordRepository.getWord(id);
        Assert.assertEquals("Hors",retrievedWord.getText_en());
        Assert.assertEquals("hæst",retrievedWord.getText_da());
        Assert.assertEquals("cat1",retrievedWord.getCategory());
        Assert.assertEquals(WordStatus.PENDING,retrievedWord.getStatus());
        retrievedWord.setText_en("Horse");
        retrievedWord.setText_da("Hest");
        retrievedWord.setStatus(WordStatus.REJECTED);
        retrievedWord.setCategory("cat2");
        wordRepository.updateWord(retrievedWord);
        retrievedWord = wordRepository.getWord(id);
        Assert.assertEquals("Horse",retrievedWord.getText_en());
        Assert.assertEquals("Hest",retrievedWord.getText_da());
        Assert.assertEquals("cat2",retrievedWord.getCategory());
        Assert.assertEquals(WordStatus.REJECTED,retrievedWord.getStatus());
    }

//    @Test
    public void testRelationAndStuff() throws Exception{
        int word1_id = wordRepository.createWord(new Word("Horse","Hest","cat3",WordStatus.PENDING));
        int word2_id = wordRepository.createWord(new Word("dog like mammal","hund som pattedyr","cat3",WordStatus.PENDING));
        int img_id = imageRepository.createImage(new Image(-1,"/tmp/test.jpg","1234","cat3","red","ocr", ImageStatus.UNFINISHED));
        imageRepository.addWordToImage(img_id,word1_id,88);
        imageRepository.addWordToImage(img_id,word2_id,51);

        List<Word> words = wordRepository.getImageWords(img_id,WordStatus.PENDING);
        Assert.assertEquals(2,words.size());

        Word retreived_word1 = wordRepository.getWord(word1_id);
        Word retreived_word2 = wordRepository.getWord(word2_id);

        retreived_word1.setStatus(WordStatus.ACCEPTED);
        retreived_word2.setStatus(WordStatus.REJECTED);

        wordRepository.updateWord(retreived_word1);
        wordRepository.updateWord(retreived_word2);

        Assert.assertTrue(wordRepository.isAcceptedFor("Horse","cat3"));
        Assert.assertTrue(wordRepository.isRejectedFor("dog like mammal","cat3"));

        Assert.assertFalse(wordRepository.isAcceptedFor("Ship","cat3"));
        Assert.assertFalse(wordRepository.isRejectedFor("Ship","cat3"));
        Assert.assertFalse(wordRepository.isAcceptedFor("Horse","cat2"));
        Assert.assertFalse(wordRepository.isRejectedFor("Horse","cat2"));
    }
}
