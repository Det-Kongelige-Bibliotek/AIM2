package dk.kb.aim.model;

import dk.kb.aim.repository.WordStatus;

/**
 * Created by dgj on 22-02-2018.
 */
public class Word {
    /** The ID of the word.*/
    private int id;
    /** The english text.*/
    private String textEn;
    /** The danish translated text.*/
    private String textDa;
    /** The category of the word.*/
    private String category;
    /** The status of the word.*/
    private WordStatus status;
    
    /**
     * Constructor.
     * @param id The ID of the word.
     * @param textEn The english text.
     * @param textDa The danish translated text.
     * @param category The category of the word.
     * @param status The status of the word.
     */
    public Word(int id, String textEn, String textDa, String category, WordStatus status) {
        this.id = id;
        this.textEn = textEn;
        this.textDa = textDa;
        this.category = category;
        this.status = status;
    }
    
    /**
     * Constructor, before the word has been given an ID.
     * @param textEn The english text.
     * @param textDa The danish translated text.
     * @param category The category of the word.
     * @param status The status of the word.
     */
    public Word(String textEn, String textDa, String category, WordStatus status) {
        this.id = -1;
        this.textEn = textEn;
        this.textDa = textDa;
        this.category = category;
        this.status = status;
    }
    
    /**
     * @return The ID of the word.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set a new ID of the word.
     * @param id The new ID of the word.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return The english text.
     */
    public String getTextEn() {
        return textEn;
    }
    
    /**
     * Set a new english text.
     * @param textEn The new english text.
     */
    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }
    
    /**
     * @return The danish translated text.
     */
    public String getTextDa() {
        return textDa;
    }
    
    /**
     * Set a new danish translated text.
     * @param textDa The new danish translated text.
     */
    public void setTextDa(String textDa) {
        this.textDa = textDa;
    }
    
    /**
     * @return The category of the word.
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Set a new category of the word.
     * @param category The new category of the word.
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * @return The status of the word.
     */
    public WordStatus getStatus() {
        return status;
    }
    
    /**
     * Set the new status of the word.
     * @param status The new status of the word.
     */
    public void setStatus(WordStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", text_en='" + textEn + '\'' +
                ", text_da='" + textDa + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
