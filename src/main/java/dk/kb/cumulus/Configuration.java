package dk.kb.cumulus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import dk.kb.cumulus.config.CumulusConfiguration;
import dk.kb.cumulus.utils.ArgumentCheck;
import dk.kb.cumulus.utils.FileUtils;


/**
 * The configuration for the AIM.
 * @author jolf
 * 
 * The configuration file must be a YAML in the following format:
 * AIM:
 *   cumulus:
 *     server: $ URL for the Cumulus server
 *     username: $ Cumulus user name
 *     password: $ Cumulus user password
 *     catalog: $ Cumulus Catalog
 *   vision_credentials: $ the credentials for using the Google vision API
 *   workflow_interval: $ interval for how often to run the workflows
 *   jpeg_folder: $ The folder where the jpeg compressed are placed
 */
public class Configuration {
    
    /** Cumulus node-element.*/
    protected static final String CONF_CUMULUS = "cumulus";
    /** The cumulus server url leaf-element.*/
    protected static final String CONF_CUMULUS_SERVER = "server";
    /** The cumulus server username leaf-element.*/
    protected static final String CONF_CUMULUS_USERNAME = "username";
    /** The cumulus server password leaf-element.*/
    protected static final String CONF_CUMULUS_PASSWORD = "password";
    /** The cumulus catalogs array leaf-element.*/
    protected static final String CONF_CUMULUS_CATALOG = "catalog";

    /** The interval for how often the workflow should run (time in millis).*/
    protected static final String CONF_WORKFLOW_INTERVAL = "workflow_interval";
    /** The path to the folder, where the JPEGs are kept.*/
    protected static final String CONF_JPEG_FOLDER = "jpeg_folder";
    
    /** Whether Cumulus should have write access. */
    protected static final boolean CUMULUS_WRITE_ACCESS = true;

    /** The configuration for Cumulus.*/
    protected final CumulusConfiguration cumulusConf;
    /** The interval for running the workflow.*/
    protected final Long workflowInterval;
    /** The folder for containing the images.*/
    protected final File jpegFolder;

    /** 
     * Constructor.
     * @param f The file with the YAML configuration structure (as described in class header).
     * @throws IOException If it cannot load the configuration from the YAML file.
     */
    public Configuration(File f) throws IOException {
        try (InputStream in = new FileInputStream(f)) {
            Object o = new Yaml().load(in);
            if(!(o instanceof LinkedHashMap)) {
                throw new IllegalArgumentException("The file '" + f + "' does not contain a valid AIM configuration.");
            }
            LinkedHashMap<String, Object> rootMap = (LinkedHashMap<String, Object>) o;
            ArgumentCheck.checkTrue(rootMap.containsKey("aim"), 
                    "Configuration must contain the '" + CONF_WORKFLOW_INTERVAL + "' element.");
            
            LinkedHashMap<String, Object> confMap = (LinkedHashMap<String, Object>) rootMap.get("aim");
            
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_WORKFLOW_INTERVAL), 
                    "Configuration must contain the '" + CONF_WORKFLOW_INTERVAL + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_JPEG_FOLDER), 
                    "Configuration must contain the '" + CONF_JPEG_FOLDER + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_CUMULUS), 
                    "Configuration must contain the '" + CONF_CUMULUS + "' element.");
            
            this.workflowInterval = Long.valueOf((Integer) confMap.get(CONF_WORKFLOW_INTERVAL));
            this.jpegFolder = FileUtils.getDirectory((String) confMap.get(CONF_JPEG_FOLDER));
            this.cumulusConf = loadCumulusConfiguration((Map<String, Object>) confMap.get(CONF_CUMULUS));
        }
    }
    
    /**
     * Method for extracting the Cumulus configuration from the YAML map.
     * @param map The map with the Cumulus configuration elements.
     * @return The Cumulus configuration.
     */
    protected CumulusConfiguration loadCumulusConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_SERVER), 
                "Missing Cumulus element '" + CONF_CUMULUS_SERVER + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_USERNAME), 
                "Missing Cumulus element '" + CONF_CUMULUS_USERNAME + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_PASSWORD), 
                "Missing Cumulus element '" + CONF_CUMULUS_PASSWORD + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_CATALOG), 
                "Missing Cumulus element '" + CONF_CUMULUS_CATALOG + "'");
        
        List<String> catalogs = Arrays.asList((String) map.get(CONF_CUMULUS_CATALOG));
        
        return new CumulusConfiguration(CUMULUS_WRITE_ACCESS, (String) map.get(CONF_CUMULUS_SERVER), 
                (String) map.get(CONF_CUMULUS_USERNAME), (String) map.get(CONF_CUMULUS_PASSWORD), catalogs);
    }
    
    /** @return The configuration for Cumulus.*/
    public CumulusConfiguration getCumulusConf() {
        return cumulusConf;
    }
    
    /** @return The interval for running the workflow.*/
    public Long getWorkflowInterval() {
        return workflowInterval;
    }
    
    /** @return The folder for containing the images.*/
    public File getJpegFolder() {
        return jpegFolder;
    }
    
    /**
     * We only allow 1 catalog, so our instantiation of the Cumulus configuration has only one catalog in its 
     * list of catalogs. Thus returning the first and only catalog.
     * @return The Cumulus catalog.
     */
    public String getCumulusCatalog() {
        return cumulusConf.getCatalogs().get(0);
    }
}
