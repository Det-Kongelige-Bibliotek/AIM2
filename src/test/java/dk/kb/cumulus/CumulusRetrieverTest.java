package dk.kb.cumulus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import com.canto.cumulus.CategoryItem;

public class CumulusRetrieverTest extends ExtendedTestCase {

    
    @Test
    public void testStuff() throws Exception {
        String serverUrl = "cumulus-core-test-01.kb.dk";
        String userName = "bevaring";
        String userPassword = "4kYn_-JrF933";
        List<String> catalogs = Arrays.asList("Samlingsbilleder");
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
