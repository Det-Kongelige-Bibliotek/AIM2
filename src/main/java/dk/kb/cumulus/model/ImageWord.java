package dk.kb.cumulus.model;

/**
 * Created by dgj on 06-03-2018.
 */
public class ImageWord {
    int word_id;
    int image_id;
    int percent;

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

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
