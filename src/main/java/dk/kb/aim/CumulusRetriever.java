package dk.kb.aim;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.canto.cumulus.CategoryItem;
import com.canto.cumulus.constants.CombineMode;
import com.canto.cumulus.constants.FindFlag;

import dk.kb.aim.exception.MissingRecordException;
import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.cumulus.utils.StringUtils;

/**
 * Class for accessing Cumulus and retrieving the CumulusRecords.
 */
@Component
public class CumulusRetriever {
    /** The log.*/
    protected static final Logger LOGGER = LoggerFactory.getLogger(CumulusRetriever.class);

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
    /** The value regarding ready for Forside/Bagside workflow.*/
    public static final String FIELD_VALUE_FRONT_BACK_DONE = "False";
    
    /** The Cumulus field name for Forside/Bagside status.*/
    public static final String FIELD_NAME_FRONT_BACK_STATUS = "For- og bagside status";
    /** The Front/Back state for 'in process'*/
    public static final String FIELD_VALUE_FRONT_BACK_STATUS_IN_PROCESS = "I proces";
    /** The Front/Back state for 'done'.*/
    public static final String FIELD_VALUE_FRONT_BACK_STATUS_DONE = "Afsluttet";
    
    /** The search string for a field with no value.*/
    public static final String FIELD_HAS_NO_VALUE = "has no value";
    
    /** The Cumulus field name for Keywords.*/
    public static final String FIELD_NAME_KEYWORDS = "Keywords";
    /** The Cumulus field name for Color Codes.*/
    public static final String FIELD_NAME_COLOR_CODES = "Color Codes";
    
    
    /** The Cumulus server.*/
    protected CumulusServer server;
    
    /** The configuration. Auto-wired.*/
    @Autowired
    protected Configuration conf;

    /**
     * Initializes this component.
     */
    @PostConstruct
    protected void initialize() {
        setCumulusServer(new CumulusServer(conf.getCumulusConf()));
    }
    
    /**
     * Close the Cumulus client.
     */
    @PreDestroy
    protected void tearDown() {
        try {
            server.close();
        } catch (Exception e) {
            LOGGER.error("Issue while closing the Cumulus client.", e);
        }
    }
    
    /**
     * Sets the server. 
     * Made as separate function to make testing possible.
     * @param server The Cumulus server.
     */
    protected void setCumulusServer(CumulusServer server) {
        this.server = server;        
    }
    
    /**
     * Retrieves all the CumulusRecords which are ready for AIM for the given catalog.
     * @param catalogName The name of the catalog.
     * @return The collection of CumulusRecords which are ready for AIM from the given catalog.
     */
    public CumulusRecordCollection getReadyForAIMRecords(String catalogName) {
        String queryString = String.format(
                StringUtils.replaceSpacesToTabs("%s is %s\nand %s is %s\nand %s is %s\nor %s %s"),
                FIELD_NAME_READY_FOR_AIM,
                FIELD_VALUE_AIM_READY_TRUE,
                Constants.FieldNames.CATALOG_NAME,
                catalogName,
                FIELD_NAME_AIM_STATUS,
                FIELD_VALUE_AIM_STATUS_IN_PROCESS,
                FIELD_NAME_AIM_STATUS,
                FIELD_HAS_NO_VALUE
                );
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
        CumulusRecord record = server.findCumulusRecordByName(catalogName, filename);
        if(record == null) {
            throw new MissingRecordException("Cannot find the file '" + filename + "' from the catalog '"
                    + catalogName + "'", filename);
        }
        return record;
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
