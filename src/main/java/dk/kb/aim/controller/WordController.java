package dk.kb.aim.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.kb.aim.model.Word;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.repository.WordStatus;

/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class WordController {
    /** The log.*/
    private static final Logger LOGGER = LoggerFactory.getLogger(WordController.class);

    /** The default state for the words view.*/
    protected static final String DEFAULT_WORD_STATE = "PENDING";

    /** The DB repository for the words.*/
    @Autowired
    private WordRepository wordRepository;

    /**
     * Method for updating a given word, regarding both the state and the danish text translation.
     * @param id The ID of the word.
     * @param text_en The english text.
     * @param text_da The danish translation.
     * @param op_category The category.
     * @param back_to Where the request comes from.
     * @param model The request model.
     * @return Redirects back.
     */
    @RequestMapping(value="/words/update",params={"id", "text_en", "text_da", "op_category", "back_to"})
    public String updateWord(@RequestParam("id") int id,
            @RequestParam("text_en")  String text_en,
            @RequestParam("text_da")  String text_da,
            @RequestParam("op_category") String op_category,
            @RequestParam("back_to")  String back_to,
            Model model) {
        String[] parts  = op_category.split(":");
        WordStatus status;
        if(parts[0].equals("PENDING")) {
            status = WordStatus.PENDING;
        } else if (parts[0].equals("ACCEPTED")) {
            status = WordStatus.ACCEPTED;
        } else {
            status = WordStatus.REJECTED;
        }
        String category = parts[1];
        Word word       = new Word(id, text_en.toLowerCase(), text_da.toLowerCase(), category, status);
        model.addAttribute("words",wordRepository.updateWord(word));
        LOGGER.info("Updating word: " + word);
        return "redirect:" + back_to;
    }

    /**
     * The view for the words with a specific status.
     * @param status The status for the words to show.
     * @param model The request model.
     * @return The name of the jsp page.
     */
    @RequestMapping(value="/words")
    public String statusWords( @RequestParam(value="status", defaultValue=DEFAULT_WORD_STATE) WordStatus status,
            Model model) {
        model.addAttribute("controllerStatus", status);
        model.addAttribute("categories", wordRepository.getCategories());
        model.addAttribute("currentCategory", wordRepository.getCategories().get(0));

        if(status.toString().isEmpty()) {
            model.addAttribute("words", wordRepository.allWordCounts());
        } else {
            model.addAttribute("words", wordRepository.allWordCountsWithStatus(status));
        }

        return "list-words";
    }

    /**
     * The view for the words of a specific category with a specific status.
     * @param category The given category for the words to show.
     * @param status The given status for the words to show.
     * @param model The request model.
     * @return The name of the jsp page.
     */
    @RequestMapping(value="/words/{category}")
    public String allWords(@PathVariable String category,
            @RequestParam(value="status", defaultValue="PENDING") WordStatus status, Model model) {
        model.addAttribute("controllerStatus", status);
        model.addAttribute("categories", wordRepository.getCategories());
        model.addAttribute("currentCategory", category);

        if(status.toString().isEmpty()) {
            model.addAttribute("words", wordRepository.allWordCountsInCategory(category));
        } else {
            model.addAttribute("words", wordRepository.allWordCountsInCategoryWithStatus(category, status));
        }

        return "list-words";
    }
}
