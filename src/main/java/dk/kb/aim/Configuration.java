package dk.kb.aim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import dk.kb.aim.utils.FileUtils;
import dk.kb.cumulus.config.CumulusConfiguration;
import dk.kb.cumulus.utils.ArgumentCheck;


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
 *   workflow_interval: $ interval for how often to run the workflows
 *   jpeg_folder: $ The folder where the jpeg compressed are placed
 *   jpeg_size_limit: $ The maximum size of the jpeg file, otherwise compress further.
 *   jpeg_url: $ The URL to the image-server where the jpegs can be found.
 *   test: $ ONLY FOR TESTS - will use local images and it will revert the state after finish
 *       instead of finishing. It must be the path to the test-files.
 *   confidence_limit: $ The lower limit for the confidence of a word belonging to an image. If lower no connection is made
 *   max_results_for_labels: $ The maximum number of labels to retrieve from a Google image
 *   language_hint: $ primarily use the specified language when searching for OCR words in an image
 *   to_lower_case: $ true/false: Set the key words in the Wordcontroller to lower case if true
 */
@Component
public class Configuration {
    /** The log.*/
    protected static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

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
    /** The maximum size of the JPEG file after the convertion. Otherwise it must be compressed further.*/
    protected static final String CONF_JPEG_SIZE_LIMIT = "jpeg_size_limit";
    /** The root URL for the JPEG images.*/
    protected static final String CONF_JPEG_URL = "jpeg_url";
    /** The configuration for whether */
    protected static final String CONF_TEST = "test";
    /** The level of confidence of a word whether to ignore it or not */
    public static final String CONF_CONFIDENCE_LIMIT = "confidence_limit";
    /** The maximum number of labels to retrieve from a Google image */
    public static final String CONF_MAX_RESULT_FOR_LABELS = "max_results_for_labels";
    /** The primary language which is used in the OCR'ed text of an image */
    public static final String CONF_LANGUAGE_HINT = "language_hint";

    public static final String CONF_TO_LOWER_CASE = "to_lower_case";

    /** Whether Cumulus should have write access. */
    protected static final boolean CUMULUS_WRITE_ACCESS = true;

    /** The configuration for Cumulus.*/
    protected final CumulusConfiguration cumulusConf;
    /** The interval for running the workflow.*/
    protected final Long workflowInterval;
    /** The folder for containing the images.*/
    protected final File jpegFolder;
    /** The maximum size of the jpeg files.*/
    protected final Long jpegSizeLimit;
    /** The root URL for the jpegs. */
    protected final String jpegUrl;
    /** Whether or not this is running in test-mode.*/
    protected final Boolean test;
    /** The directory with the test files. Will only have a value in test-mode.*/
    protected File testDir = null;
    /** The Confidence Limit */
    protected final Integer confidenceLimit;
    /** The maximum number of results for the label */
    protected final Integer maxResultsForLabels;
    /** Primarily use the specified language when searching for OCR words in an image,  */
    protected final String languageHint;
    /** Set the key words in the Wordcontroller to lower case if true */
    protected final Boolean toLowerCase;

    /** 
     * Constructor.
     * @param path The path to the YAML file.
     * @throws IOException If it cannot load the configuration from the YAML file.
     */
    @Autowired
    public Configuration(@Value("#{ @environment['AIM_CONF'] ?: 'aim.yml'}") String path) throws IOException {
        File confFile = new File(path);
        
        if(!confFile.isFile()) {
            throw new IllegalArgumentException("No configuration file at: " + confFile.getAbsolutePath());
        }
        
        try (InputStream in = new FileInputStream(confFile)) {
            Object o = new Yaml().load(in);
            if(!(o instanceof LinkedHashMap)) {
                throw new IllegalArgumentException("The file '" + confFile 
                        + "' does not contain a valid AIM configuration.");
            }
            LinkedHashMap<String, Object> rootMap = (LinkedHashMap<String, Object>) o;
            ArgumentCheck.checkTrue(rootMap.containsKey("aim"), 
                    "Configuration must contain the '" + CONF_WORKFLOW_INTERVAL + "' element.");
            
            LinkedHashMap<String, Object> confMap = (LinkedHashMap<String, Object>) rootMap.get("aim");
            
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_WORKFLOW_INTERVAL), 
                    "Configuration must contain the '" + CONF_WORKFLOW_INTERVAL + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_JPEG_FOLDER), 
                    "Configuration must contain the '" + CONF_JPEG_FOLDER + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_JPEG_SIZE_LIMIT), 
                    "Configuration must contain the '" + CONF_JPEG_SIZE_LIMIT + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_JPEG_URL), 
                    "Configuration must contain the '" + CONF_JPEG_URL + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_CUMULUS), 
                    "Configuration must contain the '" + CONF_CUMULUS + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_CONFIDENCE_LIMIT),
                "Configuration must contain the '" + CONF_CONFIDENCE_LIMIT + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_MAX_RESULT_FOR_LABELS),
                "Configuration must contain the '" + CONF_MAX_RESULT_FOR_LABELS + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_LANGUAGE_HINT),
                "Configuration must contain the '" + CONF_LANGUAGE_HINT + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_TO_LOWER_CASE),
                "Configuration must contain the '" + CONF_TO_LOWER_CASE + "' element.");

            this.workflowInterval = Long.valueOf((Integer) confMap.get(CONF_WORKFLOW_INTERVAL));
            this.jpegFolder = FileUtils.getDirectory((String) confMap.get(CONF_JPEG_FOLDER));
            this.jpegSizeLimit = Long.valueOf((Integer) confMap.get(CONF_JPEG_SIZE_LIMIT));
            this.jpegUrl = (String) confMap.get(CONF_JPEG_URL);
            this.cumulusConf = loadCumulusConfiguration((Map<String, Object>) confMap.get(CONF_CUMULUS));
            this.confidenceLimit = (Integer) confMap.get(CONF_CONFIDENCE_LIMIT);
            this.maxResultsForLabels = (Integer) confMap.get(CONF_MAX_RESULT_FOR_LABELS);
            this.languageHint = (String) confMap.get(CONF_LANGUAGE_HINT);
            this.toLowerCase = Boolean.parseBoolean(String.valueOf(confMap.get(CONF_TO_LOWER_CASE)));

            this.test = confMap.containsKey(CONF_TEST);
            if(this.test) {
                LOGGER.info("Running in TEST mode.");
                this.testDir = FileUtils.getDirectory((String) confMap.get(CONF_TEST));
            }
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
    
    /** @return The maximum size of the jpeg images.*/
    public Long getJpegSizeLimit() {
        return jpegSizeLimit;
    }
    
    /** @return The root URL for the jpeg images.*/
    public String getJpegUrl() {
        return jpegUrl;
    }
    
    /** @return Whether or not we are running in test-mode.*/
    public boolean isTest() {
        return test;
    }
    
    /** @return The directory for the test-files.*/
    public File getTestDir() {
        return testDir;
    }

    /**
     * We only allow 1 catalog, so our instantiation of the Cumulus configuration has only one catalog in its 
     * list of catalogs. Thus returning the first and only catalog.
     * @return The Cumulus catalog.
     */
    public String getCumulusCatalog() {
        return cumulusConf.getCatalogs().get(0);
    }

    /**
     * The lower limit for the confidence of a word belonging to an image. If lower than this no connection is made
     * @return The confidence limit for a word
     *
     */
    public Integer getConfidenceLimit() {
        return confidenceLimit;
    }

    /** @return The maximum number of labels to return for an image */
    public Integer getMaxResultsForLabels() {
        return maxResultsForLabels;
    }

    /** @return The primary language to use when searching for words */
    public String getLanguageHint() {
        return languageHint;
    }

    /** @return true or false. Set the key words in the Wordcontroller to lower case if true */
    public boolean getToLowerCase() {
        return toLowerCase;
    }
}
