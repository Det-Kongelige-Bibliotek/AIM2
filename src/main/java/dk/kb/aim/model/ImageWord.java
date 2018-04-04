package dk.kb.aim.model;

/**
 * Created by dgj on 06-03-2018.
 */
public class ImageWord {
    int word_id;
    int image_id;
    int confidence;

    public int getWord_id() {
        return word_id;
    }

    public void setWord_id(int word_id) {
        this.word_id = word_id;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getConfidence() {
        return  confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
