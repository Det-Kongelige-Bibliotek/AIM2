package dk.kb.aim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.kb.aim.WordStatus;
import dk.kb.aim.model.Word;
import dk.kb.aim.repository.WordRepository;

/**
 * Created by dgj on 22-02-2018.
 */
@Controller
public class WordController {

    @Autowired
    private WordRepository wordRepository;
    
    @RequestMapping(value="/update",params={"id","text_en","text_da","op_category","back_to"})
    public String updateWord( @RequestParam("id")       int id,
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
	Word word       = new Word(id, text_en, text_da, category, status);
        model.addAttribute("words",wordRepository.updateWord(word));
        return "redirect:"+back_to;
    }


    @RequestMapping(value="/words")
    public String allWords(Model model) {
        model.addAttribute("words",wordRepository.allWords());
        model.addAttribute("categories",wordRepository.getCategories());
        return "list-words";
    }

    @RequestMapping(value="/words",params={"status"})
    public String statusWords( @RequestParam("status") WordStatus status, Model model) {
        model.addAttribute("categories",wordRepository.getCategories());
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
        model.addAttribute("categories",wordRepository.getCategories());
        return "list-words";
    }

    @RequestMapping(value="/words/{category}",params={"status"})
    public String allWords(@PathVariable String category,
			   @RequestParam("status") WordStatus status, Model model) {
        model.addAttribute("categories",wordRepository.getCategories());

	if(status.toString().length()>0) {
	    model.addAttribute("words",wordRepository.allWordsInCategoryWithStatus(category,status));
	} else {
	    model.addAttribute("words",wordRepository.allWordsInCategory(category));
	}

        return "list-words";
    }
}
