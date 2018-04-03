package dk.kb.aim.workflow.steps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.GoogleRetreiver;
import dk.kb.aim.utils.ImageConverter;
import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;

/**
 * Workflow for importing the Cumulus records, which are ready for AIM.
 * 
 */
public class ImportToAimStep extends WorkflowStep {
    /** The log.*/
    protected static final Logger log = LoggerFactory.getLogger(ImportToAimStep.class);
    
    /** The default value for the */
    protected static final String CATEGORY_UNKNOWN = "UNKNOWN";
    
    /** The name of the root category for AIM.*/
    protected static final String CATEGORY_NAME_AIM = "AIM";
    
    /** The Cumulus retriever.*/
    protected final CumulusRetriever cumulusRetriever;
    /** The name of the catalog, where the Cumulus record should be imported from.*/
    protected final String catalogName;
    /** The image converter.*/
    protected final ImageConverter imageConverter;
    /** The google retreiver.*/
    protected final GoogleRetreiver googleRetriever;
    
    /**
     * Constructor.
     * @param cumulusRetriever The Cumulus retriever.
     * @param catalogName The name of the catalog.
     * @param imageConverter The image converter.
     * @param googleRetriever The retriever for the google vision and translation APIs.
     */
    public ImportToAimStep(CumulusRetriever cumulusRetriever, String catalogName, ImageConverter imageConverter, 
            GoogleRetreiver googleRetriever) {
        this.cumulusRetriever = cumulusRetriever;
        this.catalogName = catalogName;
        this.imageConverter = imageConverter;
        this.googleRetriever = googleRetriever;
    }
    
    @Override
    public void runStep() {
        int numberOfRecords = 0;
        int numberOfSuccess = 0;
        int numberOfFailures = 0;
        
        for(CumulusRecord record : cumulusRetriever.getReadyForAIMRecords(catalogName)) {
            numberOfRecords++;
            try {
                importRecord(record);
                numberOfSuccess++;
            } catch (Exception e) {
                log.warn("Failure to import image.", e);
                numberOfFailures++;
            }
        }
        
        setResultOfRun("NOT FINISHED IMPLEMENTATION!!! Found total: " + numberOfRecords
                + ", number successfully imported: " + numberOfSuccess
                + ", number of failures: " + numberOfFailures);
    }
    
    /**
     * Imports a given Cumulus record.
     * @param record The Cumulus record to import.
     */
    protected void importRecord(CumulusRecord record) throws IOException {
        log.info("Importing the Cumulus record '[" + record.getClass().getCanonicalName() + " -> " 
                + record.getFieldValue(Constants.FieldNames.RECORD_NAME) + "]' into AIM.");
        
        String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
        String category = getAimSubCategory(record);
        // TODO: use the right image instead of this test one.
//        File imageFile = record.getFile();
        File imageFile = new File("src/test/resources/samename.tif");

        record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_AIM_STATUS, 
                CumulusRetriever.FIELD_VALUE_AIM_STATUS_IN_PROCESS);

        File jpegFile = imageConverter.convertTiff(imageFile);
        
        googleRetriever.createImageAndRetreiveLabels(jpegFile, filename, category);
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
    
    @Override
    public String getName() {
        return "Import AIM images step";
    }
}
