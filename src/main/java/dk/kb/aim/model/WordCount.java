package dk.kb.aim.model;

import dk.kb.aim.repository.WordStatus;

/**
 * Word with the count of how many images are related to the word..
 */
public class WordCount extends Word {
    /** The number of images related to the word.*/
    protected int count;

    /**
     * Constructor.
     * @param id The ID of the word.
     * @param textEn The english text.
     * @param textDa The danish translated text.
     * @param category The category of the word.
     * @param status The status of the word.
     * @param count The number of images related to the word.
     */
    public WordCount(int id, String textEn, String textDa, String category, WordStatus status, int count) {
        super(id, textEn, textDa, category, status);
        this.count = count;
    }
    
    /**
     * @return The count of images for the word.
     */
    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "WordConfidence{" +
                "id=" + id +
                ", text_en='" + textEn + '\'' +
                ", text_da='" + textDa + '\'' +
                ", status='" + status + '\'' +
                ", count='" + count + '\n' +
                '}';
    }
}
