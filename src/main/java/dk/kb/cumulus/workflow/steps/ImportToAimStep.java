package dk.kb.cumulus.workflow.steps;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRetriever;
import dk.kb.cumulus.ImageStatus;
import dk.kb.cumulus.model.Image;
import dk.kb.cumulus.repository.ImageRepository;
import dk.kb.cumulus.workflow.WorkflowStep;

/**
 * Workflow for importing the Cumulus records, which are ready for AIM.
 * 
 */
public class ImportToAimStep extends WorkflowStep {
    /** The log.*/
    protected static Logger log = LoggerFactory.getLogger(ImportToAimStep.class);
    
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
     */
    public ImportToAimStep(CumulusRetriever cumulusRetriever, String catalogName) {
        this.cumulusRetriever = cumulusRetriever;
        this.catalogName = catalogName;
    }
    
    @Override
    public void runStep() {
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
        
        String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
        String category = getAimSubCategory(record);
        File imageFile = record.getFile();

        record.setStringValueInField(CumulusRetriever.FIELD_NAME_AIM_STATUS, 
                CumulusRetriever.FIELD_VALUE_AIM_STATUS_IN_PROCESS);
        
        File jpegFile = createJpegFile(imageFile);
        Image image = new Image(0, jpegFile.getAbsolutePath(), filename, category, null, null, ImageStatus.NEW);
        
        ImageRepository repo = null;
        repo.createImage(image);
        
        runVision(jpegFile);
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
    
    protected File createJpegFile(File imageFile) {
        // TODO: Use method for creating the JPEG.
        return null;
    }
    
    protected void runVision(File jpegFile) {
        // TODO: implement the call for the VISION api.
        return;
    }
    
    @Override
    public String getName() {
        return "Import AIM images step";
    }
}
