package dk.kb.cumulus;

/**
 * Created by dgj on 22-03-2018.
 */
public enum ImageStatus {
    NEW("NEW"),
    UNFINISHED("UNFINISHED"),
    FINISHED("FINISHED");

    private final String text;

    ImageStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
