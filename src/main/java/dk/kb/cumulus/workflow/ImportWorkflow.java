package dk.kb.cumulus.workflow;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRetriever;

/**
 * Workflow for importing the Cumulus records, which are ready for AIM.
 * 
 */
public class ImportWorkflow extends Workflow {
    /** The log.*/
    Logger log = LoggerFactory.getLogger(ImportWorkflow.class);
    
    /** The default value for the */
    protected static final String CATEGORY_UNKNOWN = "UNKNOWN";
    
    /** The name of the root category for AIM.*/
    protected static final String CATEGORY_NAME_AIM = "AIM";
    
    /** The Cumulus retriever.*/
    protected final CumulusRetriever cumulusRetriever;
    /** The name of the catalog, where the Cumulus record should be imported from.*/
    protected final String catalogName;
    
    /**
     * Constructor.
     * @param cumulusRetriever The Cumulus retriever.
     * @param catalogName The name of the catalog.
     * @param interval The interval for workflow.
     */
    public ImportWorkflow(CumulusRetriever cumulusRetriever, String catalogName, long interval) {
        super(interval);
        this.cumulusRetriever = cumulusRetriever;
        this.catalogName = catalogName;
    }
    
    @Override
    public void runWorkflow() {
        for(CumulusRecord record : cumulusRetriever.getReadyForAIMRecords(catalogName)) {
            importRecord(record);
        }
    }
    
    /**
     * Imports a given Cumulus record.
     * @param record The Cumulus record to import.
     */
    protected void importRecord(CumulusRecord record) {
        log.info("Importing the Cumulus record '" + record + "' into AIM.");
        
        // TODO set to 'processing' for the aim field.
        String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
        String category = getAimSubCategory(record);
        File imageFile = record.getFile();
        // TODO make actual import!!!
    }
    
    /**
     * Retrieves the AIM subcategory for the given Cumulus record.
     * @param record The record.
     * @return The subcategory for AIM. Or 'UNKNOWN' if no AIM category.
     */
    protected String getAimSubCategory(CumulusRecord record) {
        for(int i : record.getCategories()) {
            List<String> path = cumulusRetriever.getCategoryPath(catalogName, i);
            int aimPath = path.indexOf(CATEGORY_NAME_AIM);
            if(aimPath > 0 && aimPath < path.size()) {
                return path.get(aimPath + 1);
            }
        }
        // TODO what should we do, if we cannot find the category?
        log.warn("No AIM category found. Returning 'UNKNOWN'");
        return CATEGORY_UNKNOWN;
    }
}
