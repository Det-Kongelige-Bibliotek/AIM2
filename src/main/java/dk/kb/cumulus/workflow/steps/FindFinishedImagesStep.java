package dk.kb.cumulus.workflow.steps;

import dk.kb.cumulus.CumulusRetriever;

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
        setResultOfRun("TODO: implement me!!!");
    }

    @Override
    public String getName() {
        return "Find Finished Images step";
    }
}
