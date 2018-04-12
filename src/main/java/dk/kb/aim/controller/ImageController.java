package dk.kb.aim.controller;

import dk.kb.aim.ImageStatus;
import dk.kb.aim.WordStatus;
import dk.kb.aim.model.Image;
import dk.kb.aim.model.Word;
import dk.kb.aim.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.kb.aim.repository.ImageRepository;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);


    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private WordRepository wordRepository;

    @RequestMapping("/images")
    public String allImages(Model model) {
        model.addAttribute("images",imageRepository.listAllImages());
        return "list-images";
    }

    @RequestMapping(value="/images/{imageId}")
    public String showImage(@PathVariable String imageId, Model model) {
        model.addAttribute("image_details",imageRepository.getImage(new Integer(imageId).intValue()));
        model.addAttribute("image_words",wordRepository.getImageWords(new Integer(imageId)));
        return "show-image";
    }

    @RequestMapping(value="/word_images/{wordId}")
    public String wordImages(@PathVariable String wordId,
                             @RequestParam(value="status", required = false) ImageStatus status, Model model) {
        List<Image> images = imageRepository.wordImages(new Integer(wordId),status);

        /* fetch the words for each image
           This is not the most efficient way of doing it, but it will have to do for now
         */
        Map<Integer,List<Word>> image_words = new HashMap<Integer,List<Word>>();
        for (Image img : images) {
            image_words.put(img.getId(),wordRepository.getImageWords(img.getId()));
        }
        logger.info("image word size "+image_words.size()+" "+image_words.get(1));
        model.addAttribute("images",images);
        model.addAttribute("word",wordRepository.getWord(new Integer(wordId)));
        model.addAttribute("image_words",image_words);
        return "list-images";
    }

}
