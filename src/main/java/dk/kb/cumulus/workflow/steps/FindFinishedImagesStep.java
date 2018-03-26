package dk.kb.cumulus.workflow.steps;

import dk.kb.cumulus.CumulusRetriever;
import dk.kb.cumulus.workflow.WorkflowStep;

public class FindFinishedImagesStep extends WorkflowStep {


    /** The CumulusRetriever for fetching stuff out of Cumulus.*/
    protected final CumulusRetriever retriever;
    /** The name of the catalog.*/
    protected final String catalogName;

    /**
     * Constructor.
     * @param retriever The Cumulus retriever.
     * @param catalogName The catalog.
     */
    public FindFinishedImagesStep(CumulusRetriever retriever, String catalogName) {
        this.retriever = retriever;
        this.catalogName = catalogName;
    }
    
    @Override
    public void runStep() {
        // ??
    }
}
