package dk.kb.aim.repository;

/**
 * The states for the DB image.
 * 
 * Created by dgj on 22-03-2018.
 */
public enum ImageStatus {
    /** The state for new images.*/
    NEW("NEW"),
    /** The state for unfinished images.*/
    UNFINISHED("UNFINISHED"),
    /** The state for finished images.*/
    FINISHED("FINISHED");

    /** The name of the status for the image.*/
    protected final String text;

    /**
     * Constructor.
     * @param text The text name of the image status.
     */
    ImageStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
