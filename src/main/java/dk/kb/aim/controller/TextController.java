package dk.kb.aim.controller;

import dk.kb.aim.model.Word;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.repository.WordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by jolf on 30-07-2019.
 */
@Controller
public class TextController {
    /** The log.*/
    private static final Logger LOGGER = LoggerFactory.getLogger(TextController.class);
    
    /** The default state for the words view.*/
    protected static final String DEFAULT_WORD_STATE = "PENDING";
    
    /** The DB repository for the words.*/
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ImageRepository imageRepository;

    /**
     * The view for the words with a specific status.
     * @param model The request model.
     * @return The name of the jsp page.
     */
    @RequestMapping(value="/text")
    public String statusWords(Model model) {
        model.addAttribute("categories", wordRepository.getCategories());

        return "list-text";
    }
    
    /**
     * The view for the words of a specific category with a specific status.
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
