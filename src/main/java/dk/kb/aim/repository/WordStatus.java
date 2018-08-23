package dk.kb.aim.repository;

/**
 * The states for the DB words.
 * 
 * Created by dgj on 22-03-2018.
 */
public enum WordStatus {
    /** The state when a word is pending, thus neither accepted nor rejected.*/
    PENDING("PENDING"),
    /** The state when a word is accepted.*/
    ACCEPTED("ACCEPTED"),
    /** The state when a word is rejected.*/
    REJECTED("REJECTED");
    
    /** The state name.*/
    protected final String text;
    
    /** 
     * Constructor.
     * @param text The name of the state.
     */
    WordStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
