package dk.kb.aim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Setting the default start path for the view.
 */
@Controller
public class IndexController {

    /**
     * Index controller, for redirecting towards the words site.
     * @return The redirect toward the words site.
     */
    @RequestMapping("/")
    public RedirectView getIndex() {
        return new RedirectView("words",true);
    }
}
