package dk.kb.cumulus;

import java.util.TimerTask;

public class FrontBackWorkflow extends TimerTask {

    protected final CumulusRetriever retriever;
    protected final String catalogName;

    public FrontBackWorkflow(CumulusRetriever retriever, String catalogName) {
        this.retriever = retriever;
        this.catalogName = catalogName;
    }

    @Override
    public void run() {
        CumulusRecordCollection records = retriever.getReadyForFrontBackRecords(catalogName);

        for(CumulusRecord record : records) {
            String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
            String parent = getParent(filename);
            if(parent != null) {
                //TODO set 'parent' as master.
            }
        }
        // TODO stuff!!!
    }

    protected String getParent(String filename) {
        String nameWithoutSuffix = filename.split("\\.")[0];
        String suffix = "";
        if(filename.contains("\\.")) {
            suffix =  filename.split("[\\.]")[1];
        }

        String prefix;
        if(nameWithoutSuffix.matches("[0-9]_[0-9]")) {
            prefix = nameWithoutSuffix.substring(0, nameWithoutSuffix.lastIndexOf("_")-1);
        } else {
            prefix = nameWithoutSuffix;
        }
        String lastChar = prefix.substring(prefix.length()-1);

        if(lastChar.matches("[0-9]")) {
            int digit = Integer.parseInt(lastChar);
            if (digit % 2 == 1) {
                return prefix.substring(0,nameWithoutSuffix.length() - 1) + (digit - 1) + suffix;
            }
        } else {
            // ???
        }
        return null;
    }
}
