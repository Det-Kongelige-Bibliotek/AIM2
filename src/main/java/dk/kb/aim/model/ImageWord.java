package dk.kb.aim.model;

/**
 * Created by dgj on 06-03-2018.
 */
public class ImageWord {
    /** The ID of the word.*/
    int wordId;
    /** The ID of the image.*/
    int imageId;
    /** The confidence in the association between the word and the image. In percent.*/
    int confidence;
    
    /**
     * @return The ID of the word.
     */
    public int getWordId() {
        return wordId;
    }
    
    /**
     * Set the new ID of the word.
     * @param wordId The new ID of the word.
     */
    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
    
    /**
     * @return The ID of the image.
     */
    public int getImageId() {
        return imageId;
    }
    
    /**
     * Set the new ID of the image.
     * @param imageId The new ID of the image.
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    
    /**
     * @return The confidence in the association between the word and the image. In percent.
     */
    public int getConfidence() {
        return  confidence;
    }
    
    /**
     * Set the new confidence in the association between the word and the image. In percent.
     * @param confidence The new confidence in the association between the word and the image. In percent.
     */
    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
