package dk.kb.cumulus.workflow;

import java.util.Date;
import java.util.TimerTask;

/**
 * Abstract class for workflows.
 * Deals with the generic part of when the workflow should run.
 */
public abstract class Workflow extends TimerTask {

    /** The interval for running the workflow - in millis.*/
    protected long interval;
    /** The date for the next run of the workflow.*/
    protected Date nextRun;
    /** The current state of the workflow.*/
    protected WorkflowState state = WorkflowState.WAITING;
    
    /**
     * Constructor.
     * @param interval The interval for the workflow.
     */
    public Workflow(long interval) {
        this.interval = interval;
        readyForNextRun();
    }
    
    @Override
    public void run() {
        if(state == WorkflowState.WAITING && nextRun.getTime() < System.currentTimeMillis()) {
            try {
                state = WorkflowState.RUNNING;
                runWorkflow();
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
     * This must implement the 
     */
    protected abstract void runWorkflow();
    
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
     * Sets this workflow ready for the next run by setting the date for the next run and the state to 'waiting'.
     * 
     * TODO: this should also handle the situation for negative interval - only run manually??
     */
    protected void readyForNextRun() {
        nextRun = new Date(System.currentTimeMillis() + interval);
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
