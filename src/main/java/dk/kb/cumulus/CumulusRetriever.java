package dk.kb.cumulus;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.canto.cumulus.CategoryItem;
import com.canto.cumulus.constants.CombineMode;
import com.canto.cumulus.constants.FindFlag;

import dk.kb.cumulus.utils.StringUtils;

/**
 * Class for accessing Cumulus and retrieving the CumulusRecords.
 */
public class CumulusRetriever {
    // TODO: MAKE SURE THAT THE FIELD ACTUALLY HAVE THESE NAMES!!!!
    /** The Cumulus field name for AIM.*/
    public static final String FIELD_NAME_READY_FOR_AIM = "Klar til AIM";
    /** The value regarding 'Ready for AIM' for the Cumulus field 'AIM'. ̈́*/
    public static final String FIELD_VALUE_AIM_READY_TRUE = "True";
    /** The value for the 'Ready for AIM' field, when AIM is finished. ̈́*/
    public static final String FIELD_VALUE_AIM_READY_FALSE = "False";

    /** The Cumulus field name for the AIM status.*/
    public static final String FIELD_NAME_AIM_STATUS = "AIM status";
    /** The AIM status field value for 'in process'.*/
    public static final String FIELD_VALUE_AIM_STATUS_IN_PROCESS = "I proces";
    /** The AIM status field value for 'awaiting approval'.*/
    public static final String FIELD_VALUE_AIM_STATUS_AWATING = "Afventer godkendelse";
    /** The AIM status field value for 'done'.*/
    public static final String FIELD_VALUE_AIM_STATUS_DONE = "Afsluttet";
    
    /** The Cumulus field name for Forside/Bagside*/
    public static final String FIELD_NAME_READY_FOR_FRONT_BACK = "Klar til for- og bagside";
    /** The value regarding ready for Forside/Bagside workflow.*/
    public static final String FIELD_VALUE_FRONT_BACK_READY = "True";

    /** The Cumulus server.*/
    protected final CumulusServer server;
    
    /**
     * Constructor.
     * @param server The CumulusServer.
     */
    public CumulusRetriever(CumulusServer server) {
        this.server = server;
    }
    
    /**
     * Retrieves all the CumulusRecords which are ready for AIM for the given catalog.
     * @param catalogName The name of the catalog.
     * @return The collection of CumulusRecords which are ready for AIM from the given catalog.
     */
    public CumulusRecordCollection getReadyForAIMRecords(String catalogName) {
        String queryString = String.format(
                StringUtils.replaceSpacesToTabs("%s is %s\nand %s is %s"),
                FIELD_NAME_READY_FOR_AIM,
                FIELD_VALUE_AIM_READY_TRUE,
                Constants.FieldNames.CATALOG_NAME,
                catalogName);
        EnumSet<FindFlag> findFlags = EnumSet.of(
                FindFlag.FIND_MISSING_FIELDS_ARE_ERROR, 
                FindFlag.FIND_MISSING_STRING_LIST_VALUES_ARE_ERROR);    

        CumulusQuery query = new CumulusQuery(queryString, findFlags, CombineMode.FIND_NEW);
        return server.getItems(catalogName, query);
    }

    /**
     * Retrieves all the CumulusRecords which are ready for the Front/Back workflow for the given catalog.
     * @param catalogName The name of the catalog.
     * @return The collection of CumulusRecords which are ready for AIM from the given catalog.
     */
    public CumulusRecordCollection getReadyForFrontBackRecords(String catalogName) {
        String queryString = String.format(
                StringUtils.replaceSpacesToTabs("%s is %s\nand %s is %s"),
                FIELD_NAME_READY_FOR_FRONT_BACK,
                FIELD_VALUE_FRONT_BACK_READY,
                Constants.FieldNames.CATALOG_NAME,
                catalogName);
        EnumSet<FindFlag> findFlags = EnumSet.of(
                FindFlag.FIND_MISSING_FIELDS_ARE_ERROR,
                FindFlag.FIND_MISSING_STRING_LIST_VALUES_ARE_ERROR);

        CumulusQuery query = new CumulusQuery(queryString, findFlags, CombineMode.FIND_NEW);
        return server.getItems(catalogName, query);
    }

    /**
     * Finds the CumulusRecord with the given name in the given catalog.
     * @param catalogName The name of the catalog.
     * @param filename The name of the file.
     * @return The record with the given name from the given catalog.
     */
    public CumulusRecord findRecord(String catalogName, String filename) {
        return server.findCumulusRecordByName(catalogName, filename);
    }
    
    /**
     * Retrieves the path for a category.
     * 
     * @param catalogName The name of the catalog of the category.
     * @param categoryId The ID (integer) of the category.
     * @return The path
     */
    public List<String> getCategoryPath(String catalogName, int categoryId) {
        return getCategoryPath(server.getCategory(catalogName, categoryId));
    }
    
    /**
     * Recursively goes through the category and its parent categories to establish the path.
     * Will be presented in the format: [root-category, sub-category, sub-sub-category, ...].
     * @param category The category to have its path retrieved.
     * @return The list of categories in the path (in descending order).
     */
    protected List<String> getCategoryPath(CategoryItem category) {
        List<String> res;
        CategoryItem parent = category.getParentCategoryItem();
        if(parent != null) {
            res = getCategoryPath(parent);
        } else {
            res = new ArrayList<>();
        }
        res.add(category.getDisplayString());
        return res;
    }
} 
