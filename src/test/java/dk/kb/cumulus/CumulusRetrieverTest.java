package dk.kb.cumulus;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.yaml.snakeyaml.Yaml;

import com.canto.cumulus.CategoryItem;

@SpringBootTest
public class CumulusRetrieverTest {

    static String testServerUrl;
    static String testUserName;
    static String testUserPassword;
    static String testCatalog;
    
    @BeforeClass
    public static void setup() throws Exception {
        File f = new File(System.getenv("HOME") + "/cumulus-password.yml");
        Assume.assumeTrue("Coult not find a YAML at '" + f.getAbsolutePath() + "'", f.exists());
        Object o = new Yaml().load(new FileInputStream(f));
        Assume.assumeTrue("Could not read YAML file '" + f.getAbsolutePath() + "'", (o instanceof LinkedHashMap));
        LinkedHashMap<String, Object> settings = (LinkedHashMap<String, Object>) o;
        
        testServerUrl = (String) settings.get("server_url");
        testUserName = (String) settings.get("login");
        testUserPassword = (String) settings.get("password");
        testCatalog = (String) settings.get("catalog");
    }
    
    @Test
    @Ignore
    public void testStuff() throws Exception {
        String serverUrl = testServerUrl;
        String userName = testUserName;
        String userPassword = testUserPassword;
        List<String> catalogs = Arrays.asList(testCatalog);
        boolean writeAccess = false;
        
        try (CumulusServer cumulusServer = new CumulusServer(serverUrl, userName, userPassword, catalogs, writeAccess)) {
            CumulusRecord record = cumulusServer.findCumulusRecordByName(catalogs.get(0), "album_0018_1_001.tif");
            
            System.err.println("The file: " + record.getFieldValue(Constants.FieldNames.RECORD_NAME));
            System.err.println("The categories: " + record.getFieldValue(Constants.DeprecatedFieldNames.CATEGORIES));
            System.err.println("The categories: " + record.getCategories());
            for(int id : record.getCategories()) {
                CategoryItem category = cumulusServer.getCategory(catalogs.get(0), id);
                System.err.println("Category id: " + getCategoryPath(category));
            }
            
//            Map<Integer, String> categories = cumulusServer.getCategories(catalogs.get(0));
//            for(Map.Entry<Integer, String> category : categories.entrySet()) {
//                System.err.println(category.getKey() + " -> " + category.getValue());
//            }
        }
    }
    
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
