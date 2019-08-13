package dk.kb.aim.model;

import java.util.List;
import java.util.ArrayList;

public class WordsCreationDto {
    private List<WordCount> words = new ArrayList<>();

    /**
     * @return The word.
     */
    public List<WordCount> getWords() {
        return words;
    }

    // default and parameterized constructor
    public void addWord(WordCount word) {
        this.words.add(word);
    }

    // getter and setter
}
