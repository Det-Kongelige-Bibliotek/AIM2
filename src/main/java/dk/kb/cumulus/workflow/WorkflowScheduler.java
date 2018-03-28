package dk.kb.cumulus.workflow;

import java.util.Timer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The workflow scheduler for scheduling the workflows.
 * 
 * Basically the timer checks whether to run any of workflows once every second.
 * It is the workflows themselves, who checks their conditions and performs their tasks if the conditions are met.
 */
@Service
public class WorkflowScheduler {
    /** The timer should run as a daemon.*/
    protected final static Boolean isDaemon = true;
    
    /** The interval for the timer, so it .*/
    protected final static long timerInterval = 1000L;
    
    /** The workflows running in this scheduler.*/
    @Autowired
    AimWorkflow workflow;
    
    /** The timer for running the TimerTasks.*/
    Timer timer;
    
    /**
     * Constructor.
     * Instantiates the timer as a daemon.
     */
    public WorkflowScheduler() {
        this.timer = new Timer(isDaemon);
    }
    
    /**
     * Adds a workflow to the scheduler and schedule it.
     * @param workflow The workflow
     */
    @PostConstruct
    public void scheduleWorkflow() {
        timer.scheduleAtFixedRate(workflow, timerInterval, timerInterval);
    }
}
