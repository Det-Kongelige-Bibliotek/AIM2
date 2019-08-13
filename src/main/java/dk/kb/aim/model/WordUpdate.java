package dk.kb.aim.model;

import dk.kb.aim.repository.WordStatus;

public class WordUpdate {
    /** The ID of the word.*/
    protected int id;
    /** The danish translated text.*/
    protected String textDa;
    /** The status of the word.*/
    protected WordStatus status;

    /**
     * Constructor.
     * @param id The ID of the word.
     * @param textDa The danish translated text.
     * @param status The status of the word.
     */
    public WordUpdate(int id, String textDa, WordStatus status) {
        this.id = id;
        this.textDa = textDa;
        this.status = status;
    }

    public WordUpdate() {
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
                ", text_da='" + textDa + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
