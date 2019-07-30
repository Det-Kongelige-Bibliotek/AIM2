package dk.kb.aim.controller;

import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jolf on 30-07-2019.
 */
@Controller
public class TextController {
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
        model.addAttribute("images", imageRepository.listImagesInCategory(category));

        return "list-text";
    }
}
