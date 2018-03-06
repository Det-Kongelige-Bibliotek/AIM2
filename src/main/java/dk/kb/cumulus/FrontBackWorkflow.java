package dk.kb.cumulus;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontBackWorkflow extends TimerTask {
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
     */
    public FrontBackWorkflow(CumulusRetriever retriever, String catalogName) {
        this.retriever = retriever;
        this.catalogName = catalogName;
    }
    
    @Override
    public void run() {
        CumulusRecordCollection records = retriever.getReadyForFrontBackRecords(catalogName);

        for(CumulusRecord record : records) {
            String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
            String parent = getParent(filename);
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
     * Retrieves the name of the parent 
     * 
     * 
     * @param filename The name of the file, whose parent file should be found.
     * @return The name of the parent, or null if no parent was found (e.g. the file might be parent to other records)
     */
    protected String getParent(String filename) {
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
