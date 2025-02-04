package dk.kb.aim.repository;

import dk.kb.aim.model.WordCount;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dk.kb.aim.model.Image;
import dk.kb.aim.model.Word;
import dk.kb.aim.model.WordConfidence;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;

import java.util.List;
import java.util.UUID;

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
        String suffix = "-" + UUID.randomUUID().toString();
        String textEn = "Hors" + suffix;
        int id = wordRepository.createWord(new Word(textEn,"hæst","cat1", WordStatus.PENDING));
        Word retrievedWord = wordRepository.getWord(id);
        Assert.assertEquals(textEn,retrievedWord.getTextEn());
        Assert.assertEquals("hæst",retrievedWord.getTextDa());
        Assert.assertEquals("cat1",retrievedWord.getCategory());
        Assert.assertEquals(WordStatus.PENDING,retrievedWord.getStatus());
        retrievedWord.setTextEn("Horse" + suffix);
        retrievedWord.setTextDa("Hest");
        retrievedWord.setStatus(WordStatus.REJECTED);
        retrievedWord.setCategory("cat2");
        wordRepository.updateWord(retrievedWord);
        retrievedWord = wordRepository.getWord(id);
        Assert.assertEquals("Horse" + suffix,retrievedWord.getTextEn());
        Assert.assertEquals("Hest",retrievedWord.getTextDa());
        Assert.assertEquals("cat2",retrievedWord.getCategory());
        Assert.assertEquals(WordStatus.REJECTED,retrievedWord.getStatus());
    }

    @Test
    public void testRelationAndStuff() throws Exception {
        String category = UUID.randomUUID().toString();
        int word1_id = wordRepository.createWord(new Word("Horse","Hest",category,WordStatus.PENDING));
        int word2_id = wordRepository.createWord(new Word("dog like mammal","hund som pattedyr",category,WordStatus.PENDING));
        int img_id = imageRepository.createImage(new Image(-1,"/tmp/test.jpg","1234",category,"red","ocr", ImageStatus.UNFINISHED, true));
        imageRepository.addWordToImage(img_id,word1_id,88);
        imageRepository.addWordToImage(img_id,word2_id,51);

        List<WordConfidence> words = wordRepository.getImageWords(img_id,WordStatus.PENDING);
        Assert.assertEquals(2,words.size());

        Word retreived_word1 = wordRepository.getWord(word1_id);
        Word retreived_word2 = wordRepository.getWord(word2_id);

        retreived_word1.setStatus(WordStatus.ACCEPTED);
        retreived_word2.setStatus(WordStatus.REJECTED);

        wordRepository.updateWord(retreived_word1);
        wordRepository.updateWord(retreived_word2);

        Assert.assertTrue(wordRepository.isAcceptedFor("Horse",category));
        Assert.assertTrue(wordRepository.isRejectedFor("dog like mammal",category));

        Assert.assertFalse(wordRepository.isAcceptedFor("Ship",category));
        Assert.assertFalse(wordRepository.isRejectedFor("Ship",category));
        Assert.assertFalse(wordRepository.isAcceptedFor("Horse","cat2"));
        
        List<WordConfidence> confidences = wordRepository.getImageWords(img_id);
        Assert.assertEquals(2, confidences.size());
        WordConfidence cw = confidences.get(0);
        
        Assert.assertEquals(88, cw.getConfidence());
    }
    
    @Test
    public void testWordCount() throws Exception {
        String category = UUID.randomUUID().toString();
        for(Image image : imageRepository.listAllImages()) {
            imageRepository.removeImage(image);
        }
        int word_id = wordRepository.createWord(new Word("Horse","Hest",category,WordStatus.PENDING));
        int img_id1 = imageRepository.createImage(new Image(-1,"/tmp/test1.jpg","1234",category,"red","ocr", ImageStatus.UNFINISHED, true));
        int img_id2 = imageRepository.createImage(new Image(-1,"/tmp/test2.jpg","5678",category,"burgundy","ocr", ImageStatus.UNFINISHED, true));
        imageRepository.addWordToImage(img_id1,word_id,88);
        imageRepository.addWordToImage(img_id2,word_id,67);

        List<WordCount> wordCounts = wordRepository.getWordCounts(null, "id", true);
        Assert.assertEquals(wordCounts.size(), 1);
        Assert.assertEquals(wordCounts.get(0).getCount(), 2);
    }

    @Test
    public void testCategories() {
        List<String> categories = wordRepository.getCategories();
        for(String cat : categories)
            System.out.println(cat);
    }

    @Test(expected = org.springframework.dao.DuplicateKeyException.class)
    public void testUniqueWordsCreationFailure() {
        // Test that two words with the same english text and same category cannot exist simultaneous.
        String text = UUID.randomUUID().toString();
        String category = UUID.randomUUID().toString();

        wordRepository.createWord(new Word(text, "", category, WordStatus.PENDING));
        wordRepository.createWord(new Word(text, "", category, WordStatus.PENDING));
    }

    @Test
    public void testUniqueWordsUpdate() {
        // Test that a word cannot be update to become the same as another word.
        String category = UUID.randomUUID().toString();

        Word w1 = new Word(UUID.randomUUID().toString(), "", category, WordStatus.PENDING);
        Word w2 = new Word(UUID.randomUUID().toString(), "", category, WordStatus.PENDING);

        int word1 = wordRepository.createWord(w1);
        int word2 = wordRepository.createWord(w2);

        w1.setId(word1);
        w2.setId(word2);

        System.err.println(w1);
        System.err.println(w2);

        w2.setCategory(w1.getCategory());
        w2.setTextEn(w1.getTextEn());
        w2.setStatus(WordStatus.ACCEPTED);

        Assert.assertTrue(wordRepository.hasDuplicateWord(w2));

        wordRepository.updateWord(w2);

        Assert.assertEquals(w2.getId(), w1.getId());
    }
}
