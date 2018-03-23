package dk.kb.cumulus.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Configuration;
import dk.kb.cumulus.CumulusRetriever;
import dk.kb.cumulus.workflow.steps.FindFinishedImagesStep;
import dk.kb.cumulus.workflow.steps.FrontBackStep;
import dk.kb.cumulus.workflow.steps.ImportToAimStep;

/**
 * Abstract class for workflows.
 * Deals with the generic part of when the workflow should run.
 */
public class AimWorkflow extends TimerTask {
    /** The log.*/
    protected static Logger log = LoggerFactory.getLogger(AimWorkflow.class);

    /** The date for the next run of the workflow.*/
    protected Date nextRun;
    /** The current state of the workflow.*/
    protected WorkflowState state = WorkflowState.WAITING;
    /** The status of this workflow.*/
    protected String status = "Has not run yet";
    
    /** The configuration.*/
    protected final Configuration conf;
    /** The Cumulus retriever.*/
    protected final CumulusRetriever retriever;
    /** The steps for the workflow.*/
    protected final List<WorkflowStep> steps;
    
    /**
     * Constructor.
     * @param interval The interval for the workflow.
     */
    public AimWorkflow(Configuration conf, CumulusRetriever retriever) {
        this.conf = conf;
        this.retriever = retriever;
        this.steps = new ArrayList<WorkflowStep>();
        
        steps.add(new FrontBackStep(retriever, conf.getCumulusCatalog()));
        steps.add(new ImportToAimStep(retriever, conf.getCumulusCatalog()));
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
                step.runStep();
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
        return nextRun;
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
