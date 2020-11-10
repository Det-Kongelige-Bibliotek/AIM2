package dk.kb.aim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.aim.workflow.AimWorkflow;

/**
 * The controller for the Workflow view
 * Created by jolf on 19-03-2018.
 */
@Controller
public class WorkflowController {

    /** The workflow.*/
    @Autowired
    protected AimWorkflow workflow;
    
    /**
     * The view for the workflow.
     * @param model The model.
     * @return The name of the jsp page.
     */
    @RequestMapping("/workflow")
    public String getWorkflow(Model model) {
        model.addAttribute("workflow", workflow);
        
        return "workflow";
    }
    
    /**
     * Mapping for starting the workflow.
     * @return Redirects to the workflow page again, when the workflow has been started.
     */
    @RequestMapping("/workflow/run")
    public RedirectView runWorkflow() {
        workflow.startManually();
        
        try {
            synchronized(this) {
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("../workflow",true);
    }
}
