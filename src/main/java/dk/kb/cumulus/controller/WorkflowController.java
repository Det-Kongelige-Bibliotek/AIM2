package dk.kb.cumulus.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.kb.cumulus.workflow.Workflow;
import dk.kb.cumulus.workflow.WorkflowScheduler;

/**
 * Created by jolf on 19-03-2018.
 */
@Controller
public class WorkflowController {

    /** The workflow scheduler.*/
    protected WorkflowScheduler scheduler;

    @RequestMapping("/workflows")
    public List<Workflow> allWorkflows(Model model) {
        if(scheduler != null) {
            return scheduler.getWorkflows();            
        }
        return new ArrayList<Workflow>();
    }
    
    /**
     * Set the scheduler for the workflows.
     * @param scheduler The scheduler.
     */
    public void setScheduler(WorkflowScheduler scheduler) {
        this.scheduler = scheduler;
    }
}
