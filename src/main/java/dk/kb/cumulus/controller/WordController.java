package dk.kb.cumulus.controller;

import dk.kb.cumulus.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class WordController {

    @Autowired
    private WordRepository wordRepository;

    @RequestMapping("/words")
    public String allWords(Model model) {
        model.addAttribute("words",wordRepository.allWords());
        return "list-words";
    }

    @RequestMapping(value="/words/{category}")
    public String showImage(@PathVariable String category, Model model) {
        model.addAttribute("words",wordRepository.allWordsInCategory(category));
        model.addAttribute("categories",wordRepository.getCategories());
        return "list-words";
    }


}
