package dk.kb.cumulus;

/**
 * Created by dgj on 22-03-2018.
 */
public enum WordStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED");

    private final String text;

    WordStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
