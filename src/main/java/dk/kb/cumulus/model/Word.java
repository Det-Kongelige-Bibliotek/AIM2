package dk.kb.cumulus.model;

/**
 * Created by dgj on 22-02-2018.
 */
public class Word {
    private int id;
    private String text_en;
    private String text_da;
    private String status;

    public Word(int id, String text_en, String text_da, String status) {
        this.id = id;
        this.text_en = text_en;
        this.text_da = text_da;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText_en() {
        return text_en;
    }

    public void setText_en(String text_en) {
        this.text_en = text_en;
    }

    public String getText_da() {
        return text_da;
    }

    public void setText_da(String text_da) {
        this.text_da = text_da;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", text_en='" + text_en + '\'' +
                ", text_da='" + text_da + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
