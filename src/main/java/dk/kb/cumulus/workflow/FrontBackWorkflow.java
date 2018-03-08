package dk.kb.cumulus.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusRetriever;

/**
 * The workflow for the FrontBack relations to be generated.
 */
public class FrontBackWorkflow extends Workflow {
    /** The log.*/
    Logger log = LoggerFactory.getLogger(FrontBackWorkflow.class);

    /** The CumulusRetriever for fetching stuff out of Cumulus.*/
    protected final CumulusRetriever retriever;
    /** The name of the catalog.*/
    protected final String catalogName;

    /**
     * Constructor.
     * @param retriever The Cumulus retriever.
     * @param catalogName The catalog.
     * @param interval The interval for workflow.
     */
    public FrontBackWorkflow(CumulusRetriever retriever, String catalogName, long interval) {
        super(interval);
        this.retriever = retriever;
        this.catalogName = catalogName;
    }
    
    @Override
    public void runWorkflow() {
        CumulusRecordCollection records = retriever.getReadyForFrontBackRecords(catalogName);

        for(CumulusRecord record : records) {
            String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
            String parent = getFrontPage(filename);
            if(parent != null) {
                log.info("The record '" + record + "' will have master asset '" + parent + "'");
                CumulusRecord parentRecord = retriever.findRecord(catalogName, parent);
                if(parentRecord != null) {
                    record.addMasterAsset(parentRecord);
                } else {
                    log.warn("The record '" + record + "' should have a parent named '" + parent 
                            + "', but no such record could be found.");
                }
            }
        }
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
     * @param filename The name of the file, whose parent file should be found.
     * @return The name of the parent, or null if no parent was found (e.g. the file might be parent to other records)
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
            if (digit % 2 == 1) {
                return prefix.substring(0,prefix.length() - 1) + (digit - 1) + suffix;
            }
        } else {
            log.warn("Cannot find parent, when file '" + filename + "' does not have the required format: "
                    + "[a-zA-Z0-9\\-]*[0-9].`suffix` or [a-zA-Z0-9]*[0-9]_[0-9]*.`suffix`");
        }
        return null;
    }
}
