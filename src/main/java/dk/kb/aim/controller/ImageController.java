package dk.kb.aim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.kb.aim.Configuration;
import dk.kb.aim.ImageStatus;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;


/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private WordRepository wordRepository;
    
    @Autowired
    private Configuration conf;

    @RequestMapping("/images")
    public String allImages(Model model) {
        model.addAttribute("images",imageRepository.listAllImages());
        model.addAttribute("image_url", conf.getJpegUrl());
        return "list-images";
    }

    @RequestMapping(value="/images/{imageId}")
    public String showImage(@PathVariable String imageId, Model model) {
        model.addAttribute("image_details",imageRepository.getImage(new Integer(imageId).intValue()));
        model.addAttribute("image_words",wordRepository.getImageWords(new Integer(imageId)));
        model.addAttribute("image_url", conf.getJpegUrl());
        return "show-image";
    }

    @RequestMapping(value="/word_images/{wordId}",params={"status"})
    public String wordImages(@PathVariable String wordId,
                             @RequestParam("status") ImageStatus status, Model model) {
        model.addAttribute("images",imageRepository.wordImages(new Integer(wordId),status));
        model.addAttribute("word",wordRepository.getWord(new Integer(wordId)));
        model.addAttribute("image_url", conf.getJpegUrl());
        return "list-images";
    }

}
