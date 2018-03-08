package dk.kb.cumulus.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * The workflow scheduler for scheduling the workflows.
 * 
 * Basically the timer checks whether to run any of workflows once every second.
 * It is the workflows themselves, who checks their conditions and performs their tasks if the conditions are met.
 */
public class WorkflowScheduler {
    /** The timer should run as a daemon.*/
    protected final static Boolean isDaemon = true;
    
    /** The interval for the timer, so it .*/
    protected final static long timerInterval = 1000L;
    
    /** The workflows running in this scheduler.*/
    List<Workflow> workflows;
    
    /** The timer for running the TimerTasks.*/
    Timer timer;
    
    /**
     * Constructor.
     * Instantiates the timer as a daemon.
     */
    public WorkflowScheduler() {
        this.timer = new Timer(isDaemon);
        this.workflows = new ArrayList<Workflow>();
    }
    
    /**
     * Adds a workflow to the scheduler and schedule it.
     * @param workflow The workflow
     */
    public void scheduleWorkflow(Workflow workflow) {
        workflows.add(workflow);
        timer.scheduleAtFixedRate(workflow, timerInterval, timerInterval);
    }
    
    /**
     * @return All the current workflows.
     */
    public List<Workflow> getWorkflows() {
        return new ArrayList<Workflow>(workflows);
    }
}
