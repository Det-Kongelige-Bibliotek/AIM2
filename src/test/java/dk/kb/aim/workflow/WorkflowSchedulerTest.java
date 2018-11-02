package dk.kb.aim.workflow;

import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledExecutorService;

import org.junit.Test;

public class WorkflowSchedulerTest {

    @Test
    public void testShutdown() {
        WorkflowScheduler scheduler = new WorkflowScheduler();
        
        AimWorkflow workflow = mock(AimWorkflow.class);
        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        
        scheduler.workflow = workflow;
        scheduler.executorService = executor;
        
        scheduler.shutDown();
        
        verify(workflow).cancel();
        verifyNoMoreInteractions(workflow);
        verify(executor).shutdownNow();
        verifyNoMoreInteractions(executor);
    }
}
