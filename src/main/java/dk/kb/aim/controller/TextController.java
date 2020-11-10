package dk.kb.aim.controller;

import dk.kb.aim.model.Image;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The controller for the text views
 * Created by jolf on 30-07-2019.
 */
@Controller
public class TextController {
    /** The log.*/
    private static final Logger LOGGER = LoggerFactory.getLogger(TextController.class);

    /** The DB repository for the words.*/
    @Autowired
    private WordRepository wordRepository;
    /** The DB repository for the images.*/
    @Autowired
    private ImageRepository imageRepository;

    /**
     * The default view for the text.
     * @param model The request model.
     * @return The name of the jsp page.
     */
    @RequestMapping(value="/text")
    public String statusWords(Model model) {
        model.addAttribute("categories", wordRepository.getCategories());

        return "list-text";
    }
    
    /**
     * The view for the text of a specific category.
     * @param category The given category for the words to show.
     * @param model The request model.
     * @return The name of the jsp page.
     */
    @RequestMapping(value="/text/{category}")
    public String allWords(@PathVariable String category, Model model) {
        model.addAttribute("categories", wordRepository.getCategories());
        model.addAttribute("currentCategory", category);
        model.addAttribute("images", imageRepository.listImagesInCategory(category));

        return "list-text";
    }

    /**
     * The method for downloading the OCR as a CSV file.
     * @param category The category to extract its OCR text as a CSV file.
     * @return The response containing the metadata file.
     */
    @RequestMapping(value="/text/download")
    public ResponseEntity<Resource> extractAsCsv(@RequestParam(value="category",required=true) String category) {
        try {
            LOGGER.info("Extracting CSV file for OCR text for the category: '" + category + "'.");
            List<Image> images = imageRepository.listImagesInCategory(category);

            StringBuffer content = new StringBuffer();
            for(Image image : images) {
                content.append(image.getId() + ";" + image.getCumulusId() + ";" + image.getIsFront() + ";");
                if(StringUtils.hasValue(image.getOcr())) {
                    content.append(image.getOcr().replaceAll("\n", "#"));
                }
                content.append("\n");
            }

            Resource resource = new ByteArrayResource(content.toString().getBytes(StandardCharsets.UTF_8));

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + category + ".csv\"")
                    .body(resource);
        } catch (Exception e) {
            LOGGER.warn("Failed to retrieve CSV file.", e);
            throw new IllegalStateException("Failed to retrieve CSV file.", e);
        }
    }
}
