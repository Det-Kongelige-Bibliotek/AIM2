package dk.kb.aim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import dk.kb.aim.repository.WordRepository;

/**
 * Created by dgj on 26-03-2018.
 */
@Component
public class GoogleRetreiver {

    public static final String AIM_category = "AIM";
    private static final Logger logger = LoggerFactory.getLogger(GoogleRetreiver.class);

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private WordRepository wordRepository;

    public GoogleRetreiver(ImageRepository imageRepository, WordRepository wordRepository) {
        this.imageRepository = imageRepository;
        this.wordRepository = wordRepository;
    }


    public void createImageAndRetreiveLabels(File imageFile, String cumulusId, String category) throws IOException {
        com.google.cloud.vision.v1.Image image = readImage(imageFile);
        String color = getDominatingColors(sendRequest(image, Feature.Type.IMAGE_PROPERTIES));
        Image dbImage = new Image(-1,imageFile.getName(),cumulusId,category,color,"",ImageStatus.NEW);
        int image_id = imageRepository.createImage(dbImage);
        dbImage.setId(image_id);
        retreiveAndCreateImageWords(dbImage,sendRequest(image, Feature.Type.LABEL_DETECTION));
    }

    private void retreiveAndCreateImageWords(Image dbImage, List<AnnotateImageResponse> responses) throws IOException {
        logger.debug("Received " + responses.size() + " image annotations.");
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                logger.error("Error: %s\n", res.getError().getMessage());
            } else {
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    String text_en = annotation.getDescription().trim();
                    logger.debug("Handling annotation: " + text_en);
                    Word dbWord = wordRepository.getWordByText(text_en,dbImage.getCategory());
		    if (dbWord == null) {
			dbWord = wordRepository.getWordByText(text_en,AIM_category);
		    }
                    if (dbWord == null) {
                        // The word does not exist in database - create new
                        String text_da = translateText(text_en); //TODO: translate text
                        dbWord = new dk.kb.aim.model.Word(text_en,text_da,dbImage.getCategory(),WordStatus.PENDING);

                        int word_id = wordRepository.createWord(dbWord);
                        dbWord.setId(word_id);
                    }
                    imageRepository.addWordToImage(dbImage.getId(),dbWord.getId(),Math.round(100*annotation.getConfidence()));
                }
            }
        }
    }

    private String getDominatingColors(List<AnnotateImageResponse> responses) {
        String result = "";
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                logger.error("Error: %s\n", res.getError().getMessage());
            } else {
                DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
                for (ColorInfo color : colors.getColorsList()) {
                    float fraction = color.getPixelFraction();
                    if(fraction > 0.01f) {
                        result += Float.valueOf(fraction*100.0f).intValue() + "% "
                                + String.format("%02x%02x%02x",
                                        Float.valueOf(color.getColor().getRed()).intValue(),
                                        Float.valueOf(color.getColor().getGreen()).intValue(),
                                        Float.valueOf(color.getColor().getBlue()).intValue())
                                + "\n";
                    }
                }
            }
        }
        
        logger.debug("Found the colors: \n" + result);
        return result;
    }

    private List<AnnotateImageResponse> sendRequest(com.google.cloud.vision.v1.Image image, Feature.Type type) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Feature feat = Feature.newBuilder().setType(type).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(image).build();
        requests.add(request);

        ImageAnnotatorClient client = ImageAnnotatorClient.create();
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        return response.getResponsesList();
    }

    private com.google.cloud.vision.v1.Image readImage(File file) throws IOException {
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(file));
        return com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();
    }
    
    private String translateText(String text_en) throws IOException {
        Translate translate = TranslateOptions.newBuilder().build().getService();
        Translate.TranslateOption srcLang = Translate.TranslateOption.sourceLanguage("en");
        Translate.TranslateOption tgtLang = Translate.TranslateOption.targetLanguage("da");
        Translation translation = translate.translate(text_en,srcLang,tgtLang);
        return translation.getTranslatedText();
    }
}
