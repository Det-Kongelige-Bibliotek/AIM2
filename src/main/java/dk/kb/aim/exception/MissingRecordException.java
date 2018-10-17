package dk.kb.aim.exception;

/**
 * Exception for the scenarios, when a record is missing.
 * @author jolf
 */
@SuppressWarnings("serial")
public class MissingRecordException extends RuntimeException {

    /** The name of the record, which is missing.*/
    protected final String recordName;
    
    /**
     * Constructor.
     * @param msg The message for the exception.
     * @param recordName The name of the record.
     */
    public MissingRecordException(String msg, String recordName) {
        super(msg);
        this.recordName = recordName;
    }
    
    /**
     * Constructor.
     * @param msg The message for the exception.
     * @param recordName The name of the record.
     * @param cause The cause of this exception.
     */
    public MissingRecordException(String msg, String recordName, Throwable cause) {
        super(msg, cause);
        this.recordName = recordName;
    }
    
    /**
     * @return The name of the missing record.
     */
    public String getRecordName() {
        return recordName;
    }
}
