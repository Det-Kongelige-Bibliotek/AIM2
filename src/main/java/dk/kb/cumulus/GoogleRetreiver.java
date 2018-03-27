package dk.kb.cumulus;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import dk.kb.cumulus.repository.ImageRepository;
import dk.kb.cumulus.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dgj on 26-03-2018.
 */
@Component
public class GoogleRetreiver {

    private Logger logger = LoggerFactory.getLogger(GoogleRetreiver.class);

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private WordRepository wordRepository;

    public GoogleRetreiver(ImageRepository imageRepository, WordRepository wordRepository) {
        this.imageRepository = imageRepository;
        this.wordRepository = wordRepository;
    }


    public void createImageAndRetreiveLabels(File imageFile, String cumulusId, String category) {
        try {
            Image image = readImage(imageFile);
            String color = getDominatingColors(sendRequest(image, Feature.Type.IMAGE_PROPERTIES));
            dk.kb.cumulus.model.Image dbImage = new dk.kb.cumulus.model.Image(-1,imageFile.getAbsolutePath(),cumulusId,category,color,"",ImageStatus.NEW);
            int image_id = imageRepository.createImage(dbImage);
            dbImage.setId(image_id);
            retreiveAndCreateImageWords(dbImage,sendRequest(image, Feature.Type.LABEL_DETECTION));
        } catch (Exception e) {
            logger.error("error creating image ",e);
        }
    }

    private void retreiveAndCreateImageWords(dk.kb.cumulus.model.Image dbImage, List<AnnotateImageResponse> responses) throws Exception {
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                logger.error("Error: %s\n", res.getError().getMessage());
            } else {
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    String text_en = annotation.getDescription().trim();
                    dk.kb.cumulus.model.Word dbWord = wordRepository.getWordByText(text_en,dbImage.getCategory());
                    if (dbWord == null) {
                        // The word does not exist in database - create new
                        String text_da = ""; //TODO: translate text
                        dbWord = new dk.kb.cumulus.model.Word(text_en,text_da,dbImage.getCategory(),WordStatus.PENDING);
                        int word_id = wordRepository.createWord(dbWord);
                        dbWord.setId(word_id);
                    }
                    imageRepository.addWordToImage(dbImage.getId(),dbWord.getId(),Math.round(100*annotation.getConfidence()));
                }
            }
        }
    }

    private String getDominatingColors(List<AnnotateImageResponse> responses) {
        String result = null;
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                logger.error("Error: %s\n", res.getError().getMessage());
            } else {
                DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
                for (ColorInfo color : colors.getColorsList()) {
                    result += String.format("fraction: %f, r: %f, g: %f, b: %f;",
                            color.getPixelFraction(),
                            color.getColor().getRed(),
                            color.getColor().getGreen(),
                            color.getColor().getBlue());
                }
            }
        }

        return result;
    }

    private List<AnnotateImageResponse> sendRequest(Image image, Feature.Type type) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Feature feat = Feature.newBuilder().setType(type).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(image).build();
        requests.add(request);

        ImageAnnotatorClient client = ImageAnnotatorClient.create();
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        return response.getResponsesList();
    }

    private Image readImage(File file) throws IOException {
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(file));
        return Image.newBuilder().setContent(imgBytes).build();
    }


}
