package dk.kb.cumulus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.kb.cumulus.workflow.AimWorkflow;

/**
 * Created by jolf on 19-03-2018.
 */
@Controller
public class WorkflowController {

    /** The workflow.*/
    protected AimWorkflow workflow;

    @RequestMapping("/workflows")
    public String allWorkflows(Model model) {
        model.addAttribute("workflow", workflow);
        return workflow.getState().toString();
    }
    
    /**
     * Set the AIM workflow.
     * @param workflow The workflow.
     */
    public void setWorkflow(AimWorkflow workflow) {
        this.workflow = workflow;
    }
}
