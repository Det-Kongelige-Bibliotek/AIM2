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
    
    public abstract void runStep();
}
