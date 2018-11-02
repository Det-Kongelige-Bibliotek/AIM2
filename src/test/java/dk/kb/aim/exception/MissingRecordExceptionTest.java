package dk.kb.aim.exception;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class MissingRecordExceptionTest {

    @Test
    public void testInstantiationWithoutCause() {
        String msg = UUID.randomUUID().toString();
        String recordName = UUID.randomUUID().toString();
        MissingRecordException e = new MissingRecordException(msg, recordName);
        
        Assert.assertEquals(e.getMessage(), msg);
        Assert.assertEquals(e.getRecordName(), recordName);
        Assert.assertNull(e.getCause());
    }
    
    @Test
    public void testInstantiationWithCause() {
        String msg = UUID.randomUUID().toString();
        String recordName = UUID.randomUUID().toString();
        Throwable t = new Throwable();
        MissingRecordException e = new MissingRecordException(msg, recordName, t);
        
        Assert.assertEquals(e.getMessage(), msg);
        Assert.assertEquals(e.getRecordName(), recordName);
        Assert.assertEquals(e.getCause(), t);
    }
}
