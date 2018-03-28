package dk.kb.cumulus.workflow.steps;

import java.util.List;

import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRetriever;
import dk.kb.cumulus.ImageStatus;
import dk.kb.cumulus.WordStatus;
import dk.kb.cumulus.model.Image;
import dk.kb.cumulus.model.Word;
import dk.kb.cumulus.repository.ImageRepository;
import dk.kb.cumulus.repository.WordRepository;

public class FindFinishedImagesStep extends WorkflowStep {
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
     * @param retriever The Cumulus retriever.
     * @param catalogName The catalog.
     * @param imageRepo The repository for the images.
     * @param wordRepo The repository for the words.
     */
    public FindFinishedImagesStep(CumulusRetriever retriever, String catalogName, ImageRepository imageRepo,
            WordRepository wordRepo) {
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
        
        // First go through 'new' 
        // If they are not done, then set them to Unfinished, otherwise set them to done. 
        for(Image image : imageRepo.listImagesWithStatus(ImageStatus.NEW)) {
            numberOfNew++;
            CumulusRecord record = retriever.findRecord(catalogName, image.getCumulus_id());
            if(isFinished(image)) {
                numberOfNewFinished++;
                setFinished(record, image);
            } else {
                numberOfNewNotFinished++;
                setUnfinished(record, image);
            }
        }

        for(Image image : imageRepo.listImagesWithStatus(ImageStatus.UNFINISHED)) {
            numberOfUnfinished++;
            if(isFinished(image)) {
                numberOfPreviouslyUnfinished++;
                CumulusRecord record = retriever.findRecord(catalogName, image.getCumulus_id());
                setFinished(record, image);
            }
        }

        setResultOfRun(numberOfNew + " new images, with " + numberOfNewFinished + " finished and "
                + numberOfNewNotFinished + " not finished; and " + numberOfUnfinished 
                + " previously unfinished images, where " + numberOfPreviouslyUnfinished + " was finished.");
    }
    
    /**
     * Sets the current image to 'finished', and updates the cumulus record to state 'done' and turn off aim.
     * @param record The Cumulus record to update to 'done' and turn off aim.
     * @param image The image to set to finished.
     */
    protected void setFinished(CumulusRecord record, Image image) {
        image.setStatus(ImageStatus.FINISHED);
        imageRepo.updateImage(image);
        
        List<Word> words = wordRepo.getImageWords(image.getId(), WordStatus.ACCEPTED);
        String keywords = getKeywords(words);
        record.setStringValueInField(CumulusRetriever.FIELD_NAME_KEYWORDS, keywords);
        // TODO: Which field for color or OCR?
//        record.setStringValueInField(??, image.getColor());
//        record.setStringValueInField(??, image.getOcr());
        
        record.setStringEnumValueForField(CumulusRetriever.FIELD_NAME_AIM_STATUS, 
                CumulusRetriever.FIELD_VALUE_AIM_STATUS_DONE);
        // TODO: SET RECORD TO NOT READY FOR AIM!
    }
    
    /**
     * Retrieves the keywords from the list of words.
     * TODO: should it be the danish words ?
     * @param words The list of words.
     * @return The words combined into a single string.
     */
    protected String getKeywords(List<Word> words) {
        if(words.isEmpty()) {
            return "";
        }
        String res = words.get(0).getText_da();
        for(int i = 1; i < words.size(); i++) {
            res += ", " + words.get(i).getText_da();
        }
        return res;
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
