package dk.kb.aim.workflow.steps;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.model.Image;
import dk.kb.aim.model.Word;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.ImageStatus;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.repository.WordStatus;
import dk.kb.cumulus.CumulusRecord;

/**
 * The workflow step for finding the images which not longer has any pending words,
 * and then putting their data back into Cumulus.
 * 
 * @author jolf
 */
public class FindFinishedImagesStep extends WorkflowStep {
    /** The log.*/
    protected static final Logger LOGGER = LoggerFactory.getLogger(FindFinishedImagesStep.class);

    /** The configuration */
    protected final Configuration conf;
    /** The CumulusRetriever for fetching stuff out of Cumulus.*/
    protected final CumulusRetriever retriever;
    /** The name of the catalog.*/
    protected final String catalogName;
    /** The repository for the images.*/
    protected final ImageRepository imageRepo;
    /** The repository for the words.*/
    protected final WordRepository wordRepo;
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param retriever The Cumulus retriever.
     * @param catalogName The catalog.
     * @param imageRepo The repository for the images.
     * @param wordRepo The repository for the words.
     */
    public FindFinishedImagesStep(Configuration conf, CumulusRetriever retriever, String catalogName, 
            ImageRepository imageRepo, WordRepository wordRepo) {
        this.conf = conf;
        this.retriever = retriever;
        this.catalogName = catalogName;
        this.imageRepo = imageRepo;
        this.wordRepo = wordRepo;
    }
    
    @Override
    public void runStep() {
        int numberOfNew = 0;
        int numberOfNewNotFinished = 0;
        int numberOfNewFinished = 0;
        int numberOfUnfinished = 0;
        int numberOfPreviouslyUnfinished = 0;
        int numberOfFailures = 0;
        
        // Go through unfinished first, since new will be set to unfinished if they don't succeed.
        for(Image image : imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)) {
            try {
                numberOfUnfinished++;
                if(isFinished(image)) {
                    numberOfPreviouslyUnfinished++;
                    CumulusRecord record = retriever.findRecord(catalogName, image.getCumulusId());
                    setFinished(record, image);
                }
            } catch(Exception e) {
                LOGGER.warn("Failed to handle image: '" + image.getCumulusId() + "'", e);
                numberOfFailures++;
            }
        }

        // If they are not done, then set them to Unfinished, otherwise set them to done. 
        for(Image image : imageRepo.listImagesWithStatus(ImageStatus.NEW)) {
            try {
                numberOfNew++;
                CumulusRecord record = retriever.findRecord(catalogName, image.getCumulusId());
                if(isFinished(image)) {
                    numberOfNewFinished++;
                    setFinished(record, image);
                } else {
                    numberOfNewNotFinished++;
                    setUnfinished(record, image);
                }
            } catch(Exception e) {
                LOGGER.warn("Failed to handle image: '" + image.getCumulusId() + "'", e);
                numberOfFailures++;
            }
        }

        String results = "";
        if(conf.isTest()) {
            results += "RUNNING IN TEST-MODE!!! ";
        }
        results += numberOfNew + " new images, with " + numberOfNewFinished + " finished and "
                + numberOfNewNotFinished + " not finished; and " + numberOfUnfinished 
                + " previously unfinished images, where " + numberOfPreviouslyUnfinished + " was finished.";
        if(numberOfFailures > 0) {
            results += " Number of failures: " + numberOfFailures;
        }
        setResultOfRun(results);
    }
    
    /**
     * Sets the current image to 'finished', and updates the cumulus record to state 'done' and turn off aim.
     * @param record The Cumulus record to update to 'done' and turn off aim.
     * @param image The image to set to finished.
     */
    protected void setFinished(CumulusRecord record, Image image) {
        LOGGER.info("Setting image '" + image.getCumulusId() + "' to finished!");
        
        List<Word> words = wordRepo.getImageWords(image.getId(), WordStatus.ACCEPTED);
        String keywords;
        if(words.isEmpty()) {
            keywords = "";
            LOGGER.warn("No keywords found for '" + image.getCumulusId() + "'. Setting the field to empty.");
        } else {
            keywords = getKeywords(words);
            LOGGER.info("Found keywords: \n" + keywords);
        }
        
        record.setStringValueInField(CumulusRetriever.FIELD_NAME_KEYWORDS, keywords);
        record.setStringValueInField(CumulusRetriever.FIELD_NAME_COLOR_CODES, image.getColor());
        
        // SET RECORD TO NOT READY FOR AIM!
        if(conf.isTest()) {
            record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_AIM_STATUS, "");
        } else {
            record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_AIM_STATUS, 
                    CumulusRetriever.FIELD_VALUE_AIM_STATUS_DONE);
            record.setBooleanValueInField(CumulusRetriever.FIELD_NAME_READY_FOR_AIM, Boolean.FALSE);            
        }
        image.setStatus(ImageStatus.FINISHED);
        imageRepo.updateImage(image);
    }
    
    /**
     * Retrieves the keywords from the list of words.
     * @param words The list of words.
     * @return The words combined into a single string.
     */
    protected String getKeywords(List<Word> words) {
        StringBuffer res = new StringBuffer();
        for(Word word : words) {
            res.append(word.getTextDa() + "\n");
            res.append("en|" + word.getTextEn() + "\n");
        }
        return res.toString();
    }
    
    /**
     * Sets the current image to 'unfinished', and updates the cumulus record to state 'awaiting'.
     * @param record The Cumulus record to update to 'awaiting'.
     * @param image The image to set to unfinished.
     */
    protected void setUnfinished(CumulusRecord record, Image image) {
        image.setStatus(ImageStatus.UNFINISHED);
        imageRepo.updateImage(image);
        
        record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_AIM_STATUS, 
                CumulusRetriever.FIELD_VALUE_AIM_STATUS_AWATING);
    }
    
    /**
     * Checks whether or not a given image has any pending words.
     * @param image The image.
     * @return Whether or not any words are pending for the image.
     */
    protected boolean isFinished(Image image) {
        List<Word> words = wordRepo.getImageWords(image.getId(), WordStatus.PENDING);
        
        return words.isEmpty();
    }

    @Override
    public String getName() {
        return "Find Finished Images step";
    }
}
