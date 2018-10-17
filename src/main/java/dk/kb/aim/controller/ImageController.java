package dk.kb.aim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.kb.aim.Configuration;
import dk.kb.aim.model.Image;
import dk.kb.aim.model.WordConfidence;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.ImageStatus;
import dk.kb.aim.repository.WordRepository;


/**
 * The controller for the image views and the image_word views.
 * 
 * Created by dgj on 22-02-2018.
 */
@Controller
public class ImageController {
    /** The log.*/
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);
    
    /** The DB repository for the images.*/
    @Autowired
    private ImageRepository imageRepository;
    
    /** The DB repository for the words.*/
    @Autowired
    private WordRepository wordRepository;
    
    /** The configuration.*/
    @Autowired
    private Configuration conf;
    
    /**
     * The view for all the images.
     * @param model The model.
     * @return The name of the jsp page.
     */
    @RequestMapping("/images")
    public String allImages(Model model) {
        model.addAttribute("images", imageRepository.listAllImages());
        model.addAttribute("image_url", conf.getJpegUrl());
        return "list-images";
    }
    
    /**
     * The view for a specific image.
     * @param imageId The ID of the image.
     * @param model The model.
     * @return name of the jsp page.
     */
    @RequestMapping(value="/images/{imageId}")
    public String showImage(@PathVariable String imageId, Model model) {
        model.addAttribute("image_details", imageRepository.getImage(Integer.parseInt(imageId)));
        model.addAttribute("image_words", wordRepository.getImageWords(Integer.parseInt(imageId)));
        model.addAttribute("image_url", conf.getJpegUrl());
        return "show-image";
    }
    
    /**
     * The view for the images of a specific word.
     * @param wordId The id of the word to show.
     * @param limit The number of images to show.
     * @param status The state for the images to show.
     * @param model The model.
     * @return name of the jsp page.
     */
    @RequestMapping(value="/word_images/{wordId}")
    public String wordImages(@PathVariable String wordId,
                             @RequestParam(value="limit", required=false, defaultValue="10") int limit,
                             @RequestParam(value="status", required = false) ImageStatus status, Model model) {
        List<Image> images = imageRepository.wordImages(Integer.parseInt(wordId), status, limit);

        /* fetch the words for each image
           This is not the most efficient way of doing it, but it will have to do for now
         */
        Map<Integer, List<WordConfidence>> imageWords = new HashMap<Integer, List<WordConfidence>>();
        for (Image img : images) {
            imageWords.put(img.getId(),wordRepository.getImageWords(img.getId()));
        }
        LOGGER.info("image word size " + imageWords.size() + " " + imageWords.get(1));
        model.addAttribute("images", images);
        model.addAttribute("word", wordRepository.getWord(Integer.parseInt(wordId)));
        model.addAttribute("image_url", conf.getJpegUrl());
        model.addAttribute("image_words", imageWords);
        return "list-images";
    }
}
