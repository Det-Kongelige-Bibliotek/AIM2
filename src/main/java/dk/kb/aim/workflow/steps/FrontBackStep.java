package dk.kb.aim.workflow.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;

/**
 * The workflow for the FrontBack relations to be generated.
 * If the name of an image ends with and odd digit, then it has a front-page with same name except the last digit, 
 * which will be one less.
 * 
 * E.g. A record named '123.tif' will have the front-page '122.tif'.
 * Whereas the record name '122.tif' will be a front-page itself.
 * 
 * We will then create a Master/Sub asset relation between the given record and the record of the front-page -
 * where front-page will be master.
 */
public class FrontBackStep extends WorkflowStep {
    /** The log.*/
    protected static final Logger log = LoggerFactory.getLogger(FrontBackStep.class);

    /** The configuration */
    protected final Configuration conf;
    /** The CumulusRetriever for fetching stuff out of Cumulus.*/
    protected final CumulusRetriever retriever;
    /** The name of the catalog.*/
    protected final String catalogName;

    /**
     * Constructor.
     * @param conf The configuration.
     * @param retriever The Cumulus retriever.
     * @param catalogName The catalog.
     */
    public FrontBackStep(Configuration conf, CumulusRetriever retriever, String catalogName) {
        this.conf = conf;
        this.retriever = retriever;
        this.catalogName = catalogName;
    }
    
    @Override
    public void runStep() {
        CumulusRecordCollection records = retriever.getReadyForFrontBackRecords(catalogName);

        int numberOfFronts = 0;
        int numberOfBacks = 0;
        int numberOfError = 0;
        int total = 0;
        
        for(CumulusRecord record : records) {
            total++;
            setInProgress(record);
            try {
                String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
                String frontPage = getFrontPage(filename);
                if(frontPage != null) {
                    log.info("The record '[" + record.getClass().getCanonicalName() + " -> " 
                            + record.getFieldValue(Constants.FieldNames.RECORD_NAME) + "]' will have master asset '" 
                            + frontPage + "'");
                    CumulusRecord frontPageRecord = retriever.findRecord(catalogName, frontPage);
                    if(frontPageRecord != null) {
                        record.addMasterAsset(frontPageRecord);
                        numberOfBacks++;
                    } else {
                        numberOfError++;
                        log.warn("The record '[" + record.getClass().getCanonicalName() + " -> " 
                                + record.getFieldValue(Constants.FieldNames.RECORD_NAME) + "]' should have "
                                + "a front page named '" + frontPage + "', but no such record could be found.");
                    }
                } else {
                    numberOfFronts++;
                }
            } catch (Exception e) {
                log.warn("Failed to find front/back.", e);
                numberOfError++;
            }
            setDone(record);
        }
        
        String results = "";
        if(conf.isTest()) {
            results += "RUNNING IN TEST-MODE!!! ";
        }
        results += "Found total: " + total + ", number of fronts: " + numberOfFronts + ", number of backs: " 
                + numberOfBacks + ", number of errors: " + numberOfError;
        
        setResultOfRun(results);
    }
    
    /**
     * Sets the current record to 'IN PROCESS' regarding front/back.
     * @param record The Cumulus record.
     */
    protected void setInProgress(CumulusRecord record) {
        record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS, 
                CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS);
    }
    
    /**
     * Sets the current record to 'DONE' regarding front/back. And also removes the boolean for 'ready for front/back'.
     * @param record The Cumulus record.
     */
    protected void setDone(CumulusRecord record) {
        if(conf.isTest()) {
            record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS, "");
        } else {
            record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS, 
                    CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_DONE);
            record.setBooleanValueInField(CumulusRetriever.FIELD_NAME_READY_FOR_FRONT_BACK, Boolean.FALSE);
        }
    }
    
    /**
     * Retrieves the name of the record for the front-page (which should be set as master record).
     * If filename does not have a front-page (thus the file itself being a front page), then it will return a null.
     * 
     * The name (without the suffix) of front-pages ends with odd numbers, and the name 
     * (also without suffix) of the back-pages ends with even numbers.
     * If there is multiple back-pages, then they will be granted the name of the first back-page, and have added 
     * an underscore and their index.
     * E.g. 'id-124.tiff' will be a front-page, 'id-125.tiff' will be a back-page, and 'id-125_2.tiff' will be a 
     * secondary back-page.
     * 
     * It also handles the border scenario, when the digit-count of the integer is normally would be reduced when 
     * finding the frontpage.
     * E.g. 'id-100.tiff' will have the frontpage 'id-099.tiff' and not 'id-99.tiff'. 
     * 
     * @param filename The name of the file, whose front-page file should be found.
     * @return The name of the front-page, or null if no front-page was found 
     * (e.g. the file might be front-page to other records).
     */
    protected String getFrontPage(String filename) {
        String nameWithoutSuffix = filename.split("\\.")[0];
        String suffix = "";
        if(filename.lastIndexOf(".") > 0) {
            suffix =  filename.substring(filename.lastIndexOf("."));
        }
        
        // Deal with the multiple backs syntax.
        String prefix;
        if(nameWithoutSuffix.matches(".*[0-9]_[0-9].*")) {
            prefix = nameWithoutSuffix.substring(0, nameWithoutSuffix.lastIndexOf("_"));
        } else {
            prefix = nameWithoutSuffix;
        }
        
        String digits = getTrailingDigits(prefix);

        if(digits.matches("[0-9]{1,}")) {
            long digit = Long.parseLong(digits);
            if (digit % 2 == 0) {
                return String.format("%s%0" + digits.length() + "d%s", 
                        prefix.substring(0, prefix.length()-digits.length()), digit-1, suffix);
            }
        } else {
            return null;
        }
        return null;
    }
    
    /**
     * Extracts the trailing digits of a string.
     * E.g. 'id123' will be '123', and '123id456' will be '456'.
     * It will return an empty string, if the argument does not end with any digits.
     * @param s The string.
     * @return The digits in the end of the string.
     */
    protected String getTrailingDigits(String s) {
        String digits = "";
        for(int i = s.length() -1; i >= 0; i--) {
            String digit = s.substring(i, i+1);
            if(digit.matches("[0-9]")) {
                digits = digit + digits;
            } else {
                return digits;
            }
        }
        return digits;
    }
    
    @Override
    public String getName() {
        return "Front/Back step";
    }    
}
