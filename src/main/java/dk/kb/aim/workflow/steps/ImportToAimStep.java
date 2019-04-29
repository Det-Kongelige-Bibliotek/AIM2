package dk.kb.aim.workflow.steps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.GoogleRetreiver;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.utils.ImageConverter;
import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;

/**
 * Workflow for importing the Cumulus records, which are ready for AIM.
 */
public class ImportToAimStep extends WorkflowStep {
    /** The log.*/
    protected static final Logger LOGGER = LoggerFactory.getLogger(ImportToAimStep.class);
    
    /** The default value for the */
    protected static final String CATEGORY_UNKNOWN = "UNKNOWN";
    
    /** The name of the root category for AIM.*/
    protected static final String CATEGORY_NAME_AIM = "AIM";
    
    /** The configuration */
    protected final Configuration conf;
    /** The Cumulus retriever.*/
    protected final CumulusRetriever cumulusRetriever;
    /** The name of the catalog, where the Cumulus record should be imported from.*/
    protected final String catalogName;
    /** The image converter.*/
    protected final ImageConverter imageConverter;
    /** The google retreiver.*/
    protected final GoogleRetreiver googleRetriever;
    /** The image repository. */
    protected final ImageRepository imageRepository;
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param cumulusRetriever The Cumulus retriever.
     * @param catalogName The name of the catalog.
     * @param imageConverter The image converter.
     * @param googleRetriever The retriever for the google vision and translation APIs.
     * @param imageRepository The database access for the images.
     */
    public ImportToAimStep(Configuration conf, CumulusRetriever cumulusRetriever, String catalogName, 
            ImageConverter imageConverter, GoogleRetreiver googleRetriever, ImageRepository imageRepository) {
        this.conf = conf;
        this.cumulusRetriever = cumulusRetriever;
        this.catalogName = catalogName;
        this.imageConverter = imageConverter;
        this.googleRetriever = googleRetriever;
        this.imageRepository = imageRepository;
    }
    
    @Override
    public void runStep() {
        int numberOfRecords = 0;
        int numberOfBackPages = 0;
        int numberOfReimported = 0;
        int numberOfSuccess = 0;
        int numberOfFailures = 0;
        
        for(CumulusRecord record : cumulusRetriever.getReadyForAIMRecords(catalogName)) {
            String cumulusId = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
            if(imageRepository.hasImageWithCumulusId(cumulusId)) {
                numberOfReimported++;
                LOGGER.debug("Found record which has already been imported into AIM: '["
                        + record.getClass().getCanonicalName() + " -> " 
                        + record.getFieldValue(Constants.FieldNames.RECORD_NAME) + "]'. Reimporting!");
            }
            
            LOGGER.info("Importing the Cumulus record '[" + record.getClass().getCanonicalName() + " -> " 
                    + record.getFieldValue(Constants.FieldNames.RECORD_NAME) + "]' into AIM.");
            
            numberOfRecords++;
            try {
                // handle the case, when it is a sub-asset, and thus a back-side.
                if(record.isSubAsset()) {
                    setDone(record);
                    numberOfBackPages++;
                    LOGGER.info("Found back-page which will not be imported into AIM: '[" 
                            + record.getClass().getCanonicalName() + " -> " 
                            + record.getFieldValue(Constants.FieldNames.RECORD_NAME) + "]'");
                } else {
                    importRecord(record);
                    numberOfSuccess++;
                    LOGGER.info("Successfully imported the Cumulus record '[" + record.getClass().getCanonicalName() 
                            + " -> " + record.getFieldValue(Constants.FieldNames.RECORD_NAME) + "]' into AIM.");
                }
            } catch (IOException e) {
                LOGGER.warn("An I/O issue occured, when trying to import the image", e);
                numberOfFailures++;
            } catch (Throwable e) {
                System.gc();
                LOGGER.warn("Mayor failure, when trying to import the image.", e);
                numberOfFailures++;
            }
        }
        
        String results = "";
        if(conf.isTest()) {
            results += "RUNNING IN TEST-MODE!!! ";
        }
        results += "Found total: " + numberOfRecords
                + ", number successfully imported: " + numberOfSuccess
                + ", number of backpages: " + numberOfBackPages
                + ", number of reimported: " + numberOfReimported
                + ", number of failures: " + numberOfFailures;
        setResultOfRun(results);
    }
    
    /**
     * Sets a given record to 'DONE'.
     * @param record The record to set to done.
     */
    protected void setDone(CumulusRecord record) {
        if(!conf.isTest()) {
            record.setBooleanValueInField(CumulusRetriever.FIELD_NAME_READY_FOR_AIM, Boolean.FALSE);            
        }
    }
    
    /**
     * Imports a given Cumulus record.
     * @param record The Cumulus record to import.
     */
    protected void importRecord(CumulusRecord record) throws IOException {
        String cumulusId = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
        String category = getAimSubCategory(record);
        File imageFile;
        if(conf.isTest()) {
            imageFile = new File(conf.getTestDir(), cumulusId);
        } else {
            imageFile = record.getFile();
        }

        record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_AIM_STATUS, 
                CumulusRetriever.FIELD_VALUE_AIM_STATUS_IN_PROCESS);

        if(!imageFile.isFile()) {
            throw new IllegalStateException("Cannot find the file '" + imageFile.getAbsolutePath() + "'");
        }
        
        File jpegFile = imageConverter.convertTiff(imageFile);

        googleRetriever.createImageAndRetreiveLabels(jpegFile, cumulusId, category);
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
        LOGGER.warn("No AIM category found. Returning '" + CATEGORY_UNKNOWN + "'");
        return CATEGORY_UNKNOWN;
    }
    
    @Override
    public String getName() {
        return "Import AIM images step";
    }
}
