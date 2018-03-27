package dk.kb.cumulus.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.cumulus.Configuration;
import dk.kb.cumulus.CumulusRetriever;
import dk.kb.cumulus.repository.ImageRepository;
import dk.kb.cumulus.utils.ImageConverter;
import dk.kb.cumulus.workflow.steps.FindFinishedImagesStep;
import dk.kb.cumulus.workflow.steps.FrontBackStep;
import dk.kb.cumulus.workflow.steps.ImportToAimStep;
import dk.kb.cumulus.workflow.steps.WorkflowStep;

/**
 * Abstract class for workflows.
 * Deals with the generic part of when the workflow should run.
 */
@Component
public class AimWorkflow extends TimerTask {
    /** The log.*/
    protected static final Logger log = LoggerFactory.getLogger(AimWorkflow.class);

    /** The date for the next run of the workflow.*/
    protected Date nextRun;
    /** The current state of the workflow.*/
    protected WorkflowState state = WorkflowState.WAITING;
    /** The status of this workflow.*/
    protected String status = "Has not run yet";
    
    /** The configuration.*/
    @Autowired
    protected Configuration conf;
    /** The Cumulus retriever.*/
    @Autowired
    protected CumulusRetriever retriever;
    /** The image converter.*/
    @Autowired
    protected ImageConverter imageConverter;
    /** The repository for the images.*/
    @Autowired
    protected ImageRepository imageRepo; 

    /** The steps for the workflow.*/
    protected List<WorkflowStep> steps = new ArrayList<WorkflowStep>();;
    
    /**
     * Initialization
     */
    @PostConstruct
    protected void init() {
        steps.add(new FrontBackStep(retriever, conf.getCumulusCatalog()));
        steps.add(new ImportToAimStep(retriever, conf.getCumulusCatalog(), imageConverter, imageRepo));
        steps.add(new FindFinishedImagesStep(retriever, conf.getCumulusCatalog()));
        
        readyForNextRun();
    }
    
    @Override
    public void run() {
        if(state == WorkflowState.WAITING && nextRun.getTime() < System.currentTimeMillis()) {
            try {
                state = WorkflowState.RUNNING;
                runWorkflowSteps();
            } finally {
                readyForNextRun();
            }
        }
    }
    
    /**
     * Start the workflow by setting the nextRun time to 'now'.
     * It will not actually start the workflow immediately, but it will trigger that the timertask is executed 
     * the next time it is executed by the scheduler.
     */
    public void startManually() {
        nextRun = new Date(System.currentTimeMillis());
    }
    
    /**
     * The method for actually running the workflow.
     * Goes through all steps and runs them one after the other.
     */
    protected void runWorkflowSteps() {
        try {
            for(WorkflowStep step : steps) {
                step.run();
            }
        } catch (Exception e) {
            log.error("Faild to run all the workflow steps.", e);
            status = "Failure during last run: " + e.getMessage();
        }
    }
    
    /**
     * @return The current state of the workflow.
     */
    public WorkflowState getState() {
        return state;
    }
    
    /**
     * @return The date for the next time this workflow should be run.
     */
    public Date getNextRunDate() {
        return new Date(nextRun.getTime());
    }
    
    /**
     * @return The steps of the workflow.
     */
    public List<WorkflowStep> getSteps() {
        return new ArrayList<WorkflowStep>(steps);
    }
    
    /**
     * Sets this workflow ready for the next run by setting the date for the next run and the state to 'waiting'.
     * 
     * TODO: this should also handle the situation for negative interval - only run manually??
     */
    protected void readyForNextRun() {
        nextRun = new Date(System.currentTimeMillis() + conf.getWorkflowInterval());
        state = WorkflowState.WAITING;        
    }
    
    /**
     * The enumerator for the workflow state.
     */
    public enum WorkflowState {
        /** The state when the workflow is not running, but waiting for the time to run.*/
        WAITING,
        /** The state when the workflow is running.*/
        RUNNING
    }
}
