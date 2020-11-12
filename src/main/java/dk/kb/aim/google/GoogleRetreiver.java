package dk.kb.aim.google;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ColorInfo;
import com.google.cloud.vision.v1.DominantColorsAnnotation;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageContext;
import dk.kb.aim.Configuration;
import dk.kb.aim.Constants;
import dk.kb.aim.model.Image;
import dk.kb.aim.model.Word;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.repository.WordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Google Retriever, which encapsulates both the image annotation client and the text translation client.
 *
 * Created by dgj on 26-03-2018.
 */
@Component
public class GoogleRetreiver {
    /** The logger.*/
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoogleRetreiver.class);

    /** The client for the google image annotation service.*/
    protected ImageAnnotatorClient annotationClient;
    /** The client for the google translation service.*/
    protected Translate translate = TranslateOptions.newBuilder().build().getService();

    /** The repository for the db image table.*/
    @Autowired
    protected ImageRepository imageRepository;
    /** The repository for the db word table.*/
    @Autowired
    protected WordRepository wordRepository;

    /** The configuration.*/
    @Autowired
    protected Configuration conf;


    /**
     * Retrieves the color of the image from GoogleVision, and adds it to the database image.
     * @param dbImage The database image.
     * @param googleImage The Google image.
     * @throws IOException If it fails to retrieve the color data from Google Vision.
     */
    public void retrieveColor(Image dbImage, GoogleImage googleImage) throws IOException {
        String color = getDominatingColors(sendRequest(googleImage, Feature.Type.IMAGE_PROPERTIES));
        dbImage.setColor(color);
        imageRepository.updateImage(dbImage);
    }

    /**
     * Retrieves the labels, create the words and link them to the image.
     * @param dbImage The database image.
     * @param googleImage The Google image.
     * @throws IOException If it fails to retrieve the labels from Google Vision.
     */
    public void retrieveLabels(Image dbImage, GoogleImage googleImage) throws IOException {
        List<AnnotateImageResponse> responses = sendRequest(googleImage, Feature.Type.LABEL_DETECTION,
                null, conf.getMaxResultsForLabels());
        createImageWordsForLabelAnnotations(dbImage, responses);
    }

    /**
     * Retrieves the OCR text for the image.
     * Adds the parameter to the image annotation request to ensure that it primarily looks for words of
     * the language defined in LANGUAGE_HINT.
     * @param dbImage The database image.
     * @param googleImage The Google image.
     * @throws IOException If it fails to retrieve the OCR text from Google Vision.
     */
    public void retrieveText(Image dbImage, GoogleImage googleImage) throws IOException {
        ImageContext imageContext = ImageContext.newBuilder().addLanguageHints(conf.getLanguageHint()).build();
        String ocrText = getOcrText(sendRequest(googleImage, Feature.Type.TEXT_DETECTION, imageContext, null));
        dbImage.setOcr(ocrText);
        imageRepository.updateImage(dbImage);
    }

    /**
     * Creates the ImageWords for the annotations for the given image.
     * @param dbImage The DB image entry.
     * @param responses The annotations with the labels for the images.
     * @throws IOException If the translation of the label fails.
     */
    protected void createImageWordsForLabelAnnotations(Image dbImage, List<AnnotateImageResponse> responses) 
            throws IOException {
        LOGGER.debug("Received " + responses.size() + " image annotations.");
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                LOGGER.error("Error: %s\n", res.getError().getMessage());
            } else {
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    handleWordAnnotation(dbImage, annotation);
                }
            }
        }
    }

    /**
     * Handle a single word annotation.
     * @param dbImage The DB image where the annotated words comes from.
     * @param annotation The annotation to handle.
     * @throws IOException If it fails to translate.
     */
    protected synchronized void handleWordAnnotation(Image dbImage, EntityAnnotation annotation) throws IOException {
        String textEn = annotation.getDescription().trim().toLowerCase();
        int confidence = Math.round(100.0f*annotation.getScore());
        if(confidence < conf.getConfidenceLimit()) {
            LOGGER.debug("Ignoring the label '" + textEn + "', since confidence '" + confidence + "' < '"
                    + conf.getConfidenceLimit() + "' (limit)");
            return;
        }

        LOGGER.debug("Handling annotation: " + textEn);
        Word dbWord = wordRepository.getWordByText(textEn, dbImage.getCategory());
        if (dbWord == null) {
            dbWord = wordRepository.getWordByText(textEn, Constants.AIM_CATEGORY);
        }
        if (dbWord == null) {
            // The word does not exist in database - create new
            String textDa = translateText(textEn).toLowerCase();
            dbWord = new Word(textEn, textDa, dbImage.getCategory(), WordStatus.PENDING);

            int word_id = wordRepository.createWord(dbWord);
            dbWord.setId(word_id);
        }
        imageRepository.addWordToImage(dbImage.getId(),dbWord.getId(), confidence);
    }

    /**
     * Handles the Google Vision responses for the OCR text detection.
     * @param responses The annotated image response from Google Vision.
     * @return The OCR text from the responses.
     */
    protected String getOcrText(List<AnnotateImageResponse> responses) {
        StringBuffer result = new StringBuffer();
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                LOGGER.error("Error: %s\n", res.getError().getMessage());
            } else {
                result.append(res.getFullTextAnnotation().getText());
                result.append("\n");
            }
        }
        return result.toString();
    }
    
    /**
     * Retrieves the dominating colors of the image and turn it into the String value for the field
     * for the related Cumulus record.
     * @param responses The annotations for the image regarding the dominant color properties. 
     * @return The dominating color property summary.
     */
    protected String getDominatingColors(List<AnnotateImageResponse> responses) {
        StringBuffer result = new StringBuffer();
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                LOGGER.error("Error: %s\n", res.getError().getMessage());
            } else {
                DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
                for (ColorInfo color : colors.getColorsList()) {
                    float fraction = color.getPixelFraction();
                    if(fraction > 0.01f) {
                        result.append(Float.valueOf(fraction*100.0f).intValue() + "% "
                                + String.format("%02x%02x%02x",
                                        Float.valueOf(color.getColor().getRed()).intValue(),
                                        Float.valueOf(color.getColor().getGreen()).intValue(),
                                        Float.valueOf(color.getColor().getBlue()).intValue())
                                + "\n");
                    }
                }
            }
        }

        LOGGER.debug("Found the colors: \n" + result);
        return result.toString();
    }
    
    /**
     * Send the given request for the Google Vision service for the given image.
     * @param image The image to have annotated according to the given feature type.
     * @param type The type of feature to have Google Vision annotated.
     * @return The list of annotation responses for the image regarding to the given type.
     * @throws IOException If it fails in the communication with the Google Vision service. 
     */
    protected List<AnnotateImageResponse> sendRequest(GoogleImage image, Feature.Type type)
            throws IOException {
        return sendRequest(image, type, null, null);
    }

    /**
     * Send the given request for the Google Vision service for the given image.
     * @param image The image to have annotated according to the given feature type.
     * @param type The type of feature to have Google Vision annotated.
     * @param context The imageContext for the feature to have Google Vision annotated.
     * @param maxResults The maximum number of results (optional, set to null to ignore).
     * @return The list of annotation responses for the image regarding to the given type.
     * @throws IOException If it fails in the communication with the Google Vision service. 
     */
    protected List<AnnotateImageResponse> sendRequest(GoogleImage image, Feature.Type type, ImageContext context,
                                                      Integer maxResults) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Feature.Builder fBuilder = Feature.newBuilder().setType(type);
        if(maxResults != null) {
            fBuilder = fBuilder.setMaxResults(maxResults.intValue());
        }
        AnnotateImageRequest.Builder builder = AnnotateImageRequest.newBuilder()
                .addFeatures(fBuilder.build())
                .setImage(image.getImage());
        if(context != null) {
            builder = builder.setImageContext(context);
        }
        AnnotateImageRequest request = builder.build();
        requests.add(request);

        BatchAnnotateImagesResponse response = getAnnotationClient().batchAnnotateImages(requests);
        return response.getResponsesList();
    }

    /**
     * Translate the english text into danish text.
     * @param textEn The english text.
     * @return The danish translation.
     * @throws IOException If it fails to connect to the Google Translation service.
     */
    protected String translateText(String textEn) throws IOException {
        Translate.TranslateOption srcLang = Translate.TranslateOption.sourceLanguage("en");
        Translate.TranslateOption tgtLang = Translate.TranslateOption.targetLanguage("da");
        Translation translation = translate.translate(textEn, srcLang, tgtLang);
        return translation.getTranslatedText();
    }
    
    /**
     * Retrieves the Google Vision annotation client, or starts a new one if the current one is closed.
     * @return The Google Vision annotation client.
     * @throws IOException If it fails to instantiate the client.
     */
    protected ImageAnnotatorClient getAnnotationClient() throws IOException {
        if(annotationClient == null || annotationClient.isShutdown() || annotationClient.isTerminated()) {
            annotationClient = ImageAnnotatorClient.create();
        }
        return annotationClient;
    }
}
