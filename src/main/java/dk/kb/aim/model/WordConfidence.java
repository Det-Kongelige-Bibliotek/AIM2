package dk.kb.aim.model;

import dk.kb.aim.repository.WordStatus;

/**
 * Word with the confidence for the word.
 * This is basically a class for wrapping the word together with the confidence that the given word
 * is a label for a specific image.
 */
public class WordConfidence extends Word {
    /** The confidence, that the word matches the image.*/
    protected int confidence;
    
    /**
     * Constructor.
     * @param id The ID of the word.
     * @param textEn The english text.
     * @param textDa The danish translated text.
     * @param category The category of the word.
     * @param status The status of the word.
     * @param confidence The confidence that the word matches the image.
     */
    public WordConfidence(int id, String textEn, String textDa, String category, WordStatus status, int confidence) {
        super(id, textEn, textDa, category, status);
        this.confidence = confidence;
    }
    
    /**
     * @return The confidence.
     */
    public int getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "WordConfidence{" +
                "id=" + id +
                ", text_en='" + textEn + '\'' +
                ", text_da='" + textDa + '\'' +
                ", status='" + status + '\'' +
                ", confidence='" + confidence + '\n' +
                '}';
    }
}
