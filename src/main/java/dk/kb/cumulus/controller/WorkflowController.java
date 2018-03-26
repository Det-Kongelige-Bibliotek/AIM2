package dk.kb.cumulus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.cumulus.workflow.AimWorkflow;

/**
 * Created by jolf on 19-03-2018.
 */
@Controller
public class WorkflowController {

    /** The workflow.*/
    @Autowired
    protected AimWorkflow workflow;

    @RequestMapping("/workflow")
    public String getWorkflow(Model model) {
        model.addAttribute("workflow", workflow);
        
        return "workflow";
    }
    
    @RequestMapping("/workflow/run")
    public RedirectView runWorkflow() {
        workflow.startManually();
        return new RedirectView("/workflow");
    }
}
