package dk.kb.aim;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.canto.cumulus.CategoryItem;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.TestUtils;
import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;

@SpringBootTest
public class CumulusRetrieverTest {

    static Configuration conf;
    
    @BeforeClass
    public static void setup() throws Exception {
        conf = TestUtils.getTestConfiguration();
    }
    
    @Test
    @Ignore
    public void testConnection() throws Exception {
        try (CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf())) {
            CumulusRetriever retriever = new CumulusRetriever();
            retriever.setCumulusServer(cumulusServer);
            CumulusRecordCollection aimRecords = retriever.getReadyForAIMRecords(conf.getCumulusCatalog());
            Assert.assertEquals(aimRecords.getCount(), 1);
            
            CumulusRecordCollection frontBackRecords = retriever.getReadyForFrontBackRecords(conf.getCumulusCatalog());
            Assert.assertEquals(frontBackRecords.getCount(), 1);
        }        
    }
    
    @Test
    @Ignore
    public void testStuff() throws Exception {
        try (CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf())) {
            CumulusRecord record = cumulusServer.findCumulusRecordByName(conf.getCumulusCatalog(), "album_0018_1_001.tif");
            
            System.err.println("The file: " + record.getFieldValue(Constants.FieldNames.RECORD_NAME));
            System.err.println("The categories: " + record.getFieldValue(Constants.DeprecatedFieldNames.CATEGORIES));
            System.err.println("The categories: " + record.getCategories());
            for(int id : record.getCategories()) {
                CategoryItem category = cumulusServer.getCategory(conf.getCumulusCatalog(), id);
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
