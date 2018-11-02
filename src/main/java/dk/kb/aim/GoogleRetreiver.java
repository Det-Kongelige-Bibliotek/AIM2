package dk.kb.aim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.google.protobuf.ByteString;

import dk.kb.aim.model.Image;
import dk.kb.aim.model.Word;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.ImageStatus;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.repository.WordStatus;

/**
 * Created by dgj on 26-03-2018.
 */
@Component
public class GoogleRetreiver {
    /** The logger.*/
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoogleRetreiver.class);
    
    /** The Category for the AIM category.*/
    public static final String AIM_CATEGORY = "AIM";
    
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
    
    /**
     * Creates the entry in the database for the image and retrieve the metadata for the image. 
     * It will extracts the image-file as a Google Vision image, so it is ready for being processed by the 
     * Google Vision online service for retrieving the labels and the dominating color of the image.
     * @param imageFile The image file.
     * @param cumulusId The id of the related Cumulus record.
     * @param category The AIM-sub category of the Cumulus record.
     * @throws IOException If it fails in the connection to the Google Vision service, or it cannot extract the file.
     */
    public void createImageAndRetreiveLabels(File imageFile, String cumulusId, String category) throws IOException {
        com.google.cloud.vision.v1.Image image = readImage(imageFile);
        String color = getDominatingColors(sendRequest(image, Feature.Type.IMAGE_PROPERTIES));
        Image dbImage = new Image(-1,imageFile.getName(),cumulusId,category,color,"",ImageStatus.NEW);
        int image_id = imageRepository.createImage(dbImage);
        dbImage.setId(image_id);
        createImageWordsForLabelAnnotations(dbImage,sendRequest(image, Feature.Type.LABEL_DETECTION));
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
                    String text_en = annotation.getDescription().trim();
                    LOGGER.debug("Handling annotation: " + text_en);
                    Word dbWord = wordRepository.getWordByText(text_en, dbImage.getCategory());
                    if (dbWord == null) {
                        dbWord = wordRepository.getWordByText(text_en, AIM_CATEGORY);
                    }
                    if (dbWord == null) {
                        // The word does not exist in database - create new
                        String text_da = translateText(text_en);
                        dbWord = new Word(text_en, text_da, dbImage.getCategory(), WordStatus.PENDING);

                        int word_id = wordRepository.createWord(dbWord);
                        dbWord.setId(word_id);
                    }
                    imageRepository.addWordToImage(dbImage.getId(),dbWord.getId(),
                            Long.valueOf(Math.round(100.*annotation.getConfidence())).intValue());
                }
            }
        }
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
    protected List<AnnotateImageResponse> sendRequest(com.google.cloud.vision.v1.Image image, Feature.Type type) 
            throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Feature feat = Feature.newBuilder().setType(type).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(image).build();
        requests.add(request);

        BatchAnnotateImagesResponse response = getAnnotationClient().batchAnnotateImages(requests);
        return response.getResponsesList();
    }
    
    /**
     * Reads a file as a Google Vision image object.
     * @param file The file to read.
     * @return The Google Vision image.
     * @throws IOException If it fails to read the image file.
     */
    protected com.google.cloud.vision.v1.Image readImage(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            ByteString imgBytes = ByteString.readFrom(in);
            return com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();
        }
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
