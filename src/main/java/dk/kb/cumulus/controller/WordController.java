package dk.kb.cumulus.controller;

import dk.kb.cumulus.repository.WordRepository;
import dk.kb.cumulus.WordStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class WordController {

    @Autowired
    private WordRepository wordRepository;

    @RequestMapping(value="/words")
    public String allWords(Model model) {
        model.addAttribute("words",wordRepository.allWords());
        return "list-words";
    }

    @RequestMapping(value="/words",params={"status"})
	public String statusWords( @RequestParam("status") WordStatus status, Model model) {
	if(status.toString().length()>0) {
	    model.addAttribute("words",wordRepository.allWordsWithStatus(status));
	} else {
	    model.addAttribute("words",wordRepository.allWords());
	}

        return "list-words";
    }

    @RequestMapping(value="/words/{category}")
    public String categoryWords(@PathVariable String category, Model model) {
        model.addAttribute("words",wordRepository.allWordsInCategory(category));
        return "list-words";
	}

    @RequestMapping(value="/words/{category}",params={"status"})
    public String allWords(@PathVariable String category,
			   @RequestParam("status") WordStatus status, Model model) {
	if(status.toString().length()>0) {
	    model.addAttribute("words",wordRepository.allWordsInCategoryWithStatus(category,status));
	} else {
	    model.addAttribute("words",wordRepository.allWordsInCategory(category));
	}
        return "list-words";
    }


}
