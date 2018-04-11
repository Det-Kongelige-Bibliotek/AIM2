package dk.kb.aim.controller;

import dk.kb.aim.ImageStatus;
import dk.kb.aim.WordStatus;
import dk.kb.aim.repository.WordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.kb.aim.repository.ImageRepository;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class ImageController {

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
        model.addAttribute("images",imageRepository.wordImages(new Integer(wordId),status));
        model.addAttribute("word",wordRepository.getWord(new Integer(wordId)));
        return "list-images";
    }

}
