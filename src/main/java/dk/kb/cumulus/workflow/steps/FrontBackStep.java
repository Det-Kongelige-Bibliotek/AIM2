package dk.kb.cumulus.workflow.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusRetriever;

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

    /** The CumulusRetriever for fetching stuff out of Cumulus.*/
    protected final CumulusRetriever retriever;
    /** The name of the catalog.*/
    protected final String catalogName;

    /**
     * Constructor.
     * @param retriever The Cumulus retriever.
     * @param catalogName The catalog.
     */
    public FrontBackStep(CumulusRetriever retriever, String catalogName) {
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
//            setDone(record);
        }
        
        setResultOfRun("Found total: " + total
                + ", number of fronts: " + numberOfFronts
                + ", number of backs: " + numberOfBacks
                + ", number of errors: " + numberOfError);
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
        record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_FRONT_BACK_STATUS, 
                CumulusRetriever.FIELD_VALUE_FRONT_BACK_STATUS_DONE);
        // TODO remove boolean.
    }
    
    /**
     * Retrieves the name of the record for the front-page (which should be set as master record).
     * If filename does not have a front-page (thus the file itself being a front page), then it will return a null.
     * 
     * The name (without the suffix) of front-pages ends with even numbers, and the name 
     * (also without suffix) of the back-pages ends with odd numbers.
     * If there is multiple back-pages, then they will be granted the name of the first back-page, and have added 
     * an underscore and their index.
     * E.g. 'id-123.tiff' will be a front-page, 'id-124.tiff' will be a back-page, and 'id-124_2.tiff' will be a 
     * secondary back-page.
     * 
     * @param filename The name of the file, whose front-page file should be found.
     * @return The name of the front-page, or null if no front-page was found 
     * (e.g. the file might be front-page to other records).
     */
    protected String getFrontPage(String filename) {
        String nameWithoutSuffix = filename.split("\\.")[0];
        String suffix = "";
        if(filename.contains("\\.")) {
            suffix =  filename.split("[\\.]")[1];
        }
        
        // Deal with the multiple backs syntax.
        String prefix;
        if(nameWithoutSuffix.matches(".*[0-9]_[0-9].*")) {
            prefix = nameWithoutSuffix.substring(0, nameWithoutSuffix.lastIndexOf("_"));
        } else {
            prefix = nameWithoutSuffix;
        }
        String lastChar = prefix.substring(prefix.length()-1);

        if(lastChar.matches("[0-9]")) {
            int digit = Integer.parseInt(lastChar);
            if (digit % 2 != 0) {
                return prefix.substring(0,prefix.length() - 1) + (digit - 1) + suffix;
            }
        } else {
            return null;
        }
        return null;
    }
    
    @Override
    public String getName() {
        return "Front/Back step";
    }    
}
