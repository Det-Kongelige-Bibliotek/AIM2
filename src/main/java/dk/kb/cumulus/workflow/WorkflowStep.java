package dk.kb.cumulus.workflow;

public abstract class WorkflowStep {

    protected String status;
    
    protected WorkflowStep() {
        this.status = "Not yet run.";
    }
    
    protected void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }

    protected String resultsOfLastRun = "NOT YET RUN";
    
    public String getResultOfLastRun() {
        return resultsOfLastRun;
    }
    
    public void setResultOfRun(String results) {
        resultsOfLastRun = results;
    }

    public abstract String getName();
    
    protected long timeForLastRun = 0L;
    
    public Long getTimeForLastRun() {
        return timeForLastRun;
    }
    
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            setStatus("Running");
            runStep();
            setStatus("Finished");
        } catch (Exception e) {
            setStatus("Failed");
            setResultOfRun("Failure: " + e.getMessage());
        }
        timeForLastRun = System.currentTimeMillis() - startTime;
    }
    
    protected abstract void runStep();
}
