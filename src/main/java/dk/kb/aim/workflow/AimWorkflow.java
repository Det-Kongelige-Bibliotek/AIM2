package dk.kb.aim.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.GoogleRetreiver;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.utils.ImageConverter;
import dk.kb.aim.workflow.steps.FindFinishedImagesStep;
import dk.kb.aim.workflow.steps.FrontBackStep;
import dk.kb.aim.workflow.steps.ImportToAimStep;
import dk.kb.aim.workflow.steps.WorkflowStep;

/**
 * Abstract class for workflows.
 * Deals with the generic part of when the workflow should run.
 */
@Component
public class AimWorkflow extends TimerTask {
    /** The log.*/
    protected static final Logger LOGGER = LoggerFactory.getLogger(AimWorkflow.class);

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
    protected CumulusRetriever cumulusRetriever;
    /** The image converter.*/
    @Autowired
    protected ImageConverter imageConverter;
    /** The repository for the images.*/
    @Autowired
    protected ImageRepository imageRepo;
    /** The repository for the words.*/
    @Autowired
    protected WordRepository wordRepo;
    /** The google retriever, both for the vision and the translation APIs.*/
    @Autowired
    protected GoogleRetreiver googleRetriever;

    /** The steps for the workflow.*/
    protected List<WorkflowStep> steps = new ArrayList<WorkflowStep>();;
    
    /**
     * Initialization
     */
    @PostConstruct
    protected void init() {
        steps.add(new FrontBackStep(conf, cumulusRetriever, conf.getCumulusCatalog()));
        steps.add(new ImportToAimStep(conf, cumulusRetriever, conf.getCumulusCatalog(), imageConverter, 
                googleRetriever, imageRepo));
        steps.add(new FindFinishedImagesStep(conf, cumulusRetriever, conf.getCumulusCatalog(), imageRepo, wordRepo));
        
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
        } catch (Throwable e) {
            LOGGER.error("Faild to run all the workflow steps.", e);
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
     * If the interval for the workflow is non-positive, then it is set to infinitely far out in the future.
     */
    protected void readyForNextRun() {
        if(conf.getWorkflowInterval() > 0) {
            nextRun = new Date(System.currentTimeMillis() + conf.getWorkflowInterval());
        } else {
            nextRun = new Date(Long.MAX_VALUE);
        }
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
