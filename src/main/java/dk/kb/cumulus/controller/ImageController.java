package dk.kb.cumulus.controller;

import dk.kb.cumulus.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @RequestMapping("/images")
    public String allImages(Model model) {
        model.addAttribute("images",imageRepository.listAllImages());
        return "list-images";
    }

}
