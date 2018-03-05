package dk.kb.cumulus;

import java.io.File;
import java.util.List;
import java.util.TimerTask;

/**
 * Workflow for importing the Cumulus records, which are ready for AIM.
 * 
 */
public class ImportWorkflow extends TimerTask {
    /** The name of the */
    protected static final String CATEGORY_NAME_AIM = "AIM";
    
    /** The Cumulus retriever.*/
    protected final CumulusRetriever cumulusRetriever;
    /** The name of the catalog, where the Cumulus record should be imported from.*/
    protected final String catalogName;
    
    /**
     * Constructor.
     * @param cumulusRetriever The Cumulus retriever.
     * @param catalogName The name of the catalog.
     */
    public ImportWorkflow(CumulusRetriever cumulusRetriever, String catalogName) {
        this.cumulusRetriever = cumulusRetriever;
        this.catalogName = catalogName;
    }
    
    @Override
    public void run() {
        for(CumulusRecord record : cumulusRetriever.getReadyForAIMRecords(catalogName)) {
            importRecord(record);
        }
    }
    
    /**
     * Imports a given Cumulus record.
     * @param record The Cumulus record to import.
     */
    protected void importRecord(CumulusRecord record) {
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
            if(aimPath > 0) {
                return path.get(aimPath + 1);
            }
        }
        // TODO what should we do, if we cannot find the category?
        return "UNKNOWN";
    }
}
