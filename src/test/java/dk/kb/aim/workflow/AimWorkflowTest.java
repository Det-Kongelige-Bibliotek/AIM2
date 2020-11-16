package dk.kb.aim.workflow;

import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import dk.kb.aim.Configuration;
import dk.kb.aim.CumulusRetriever;
import dk.kb.aim.google.GoogleRetreiver;
import dk.kb.aim.repository.ImageRepository;
import dk.kb.aim.repository.WordRepository;
import dk.kb.aim.utils.ImageConverter;
import dk.kb.aim.workflow.AimWorkflow.WorkflowState;
import dk.kb.aim.workflow.steps.FindFinishedImagesStep;
import dk.kb.aim.workflow.steps.FrontBackStep;
import dk.kb.aim.workflow.steps.ImportToAimStep;
import dk.kb.aim.workflow.steps.WorkflowStep;

public class AimWorkflowTest {


    @Test
    public void testInit() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;
        
        Assert.assertEquals(aimWorkflow.getSteps().size(), 0);

        aimWorkflow.init();
        
        Assert.assertEquals(aimWorkflow.getSteps().size(), 3);
        
        Assert.assertTrue(aimWorkflow.steps.get(0) instanceof FrontBackStep);
        Assert.assertTrue(aimWorkflow.steps.get(1) instanceof ImportToAimStep);
        Assert.assertTrue(aimWorkflow.steps.get(2) instanceof FindFinishedImagesStep);
        
        verify(conf, times(3)).getCumulusCatalog();
        verify(conf).getWorkflowInterval();
        verifyNoMoreInteractions(conf);
        
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);
    }
    
    @Test
    public void testReadyForNextRunPositiveInterval() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;
        
        aimWorkflow.state = WorkflowState.RUNNING;
        
        long interval = 123467890L;
        long currentTime = System.currentTimeMillis();
        
        when(conf.getWorkflowInterval()).thenReturn(interval);
        
        Assert.assertEquals(aimWorkflow.getState(), WorkflowState.RUNNING);

        aimWorkflow.readyForNextRun();
        
        // Must change to state WAITING and time to 'interval' from now (check +/- 1 sec due to test-run-delay)
        Assert.assertEquals(aimWorkflow.getState(), WorkflowState.WAITING);
        Assert.assertTrue(Math.abs(currentTime + interval - aimWorkflow.getNextRunDate().getTime()) < 1000);
        
        verify(conf, times(2)).getWorkflowInterval();
        verifyNoMoreInteractions(conf);
        
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);
    }
    
    @Test
    public void testReadyForNextRunNegativeInterval() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;
        
        aimWorkflow.state = WorkflowState.RUNNING;
        
        long interval = -1L;
        
        when(conf.getWorkflowInterval()).thenReturn(interval);
        
        Assert.assertEquals(aimWorkflow.getState(), WorkflowState.RUNNING);

        aimWorkflow.readyForNextRun();
        
        // Must change to state WAITING and time to 'interval' from now (check +/- 1 sec due to test-run-delay)
        Assert.assertEquals(aimWorkflow.getState(), WorkflowState.WAITING);
        Assert.assertEquals(aimWorkflow.getNextRunDate().getTime(), Long.MAX_VALUE);
        
        verify(conf).getWorkflowInterval();
        verifyNoMoreInteractions(conf);
        
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);
    }

    @Test
    public void testRunWorkflowStepsSuccess() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;

        String status = UUID.randomUUID().toString();
        WorkflowStep step = mock(WorkflowStep.class);
        aimWorkflow.steps.add(step);
        aimWorkflow.status = status;
        
        aimWorkflow.runWorkflowSteps();
        
        Assert.assertEquals(aimWorkflow.status, status);
        
        verify(step).run();
        verifyNoMoreInteractions(step);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);
    }

    @Test
    public void testRunWorkflowStepsFailure() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;

        WorkflowStep step = mock(WorkflowStep.class);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new RuntimeException("TEST ERROR");
            }
        }).when(step).run();
        aimWorkflow.steps.add(step);
        String status = UUID.randomUUID().toString();
        aimWorkflow.status = status;
        
        aimWorkflow.runWorkflowSteps();

        Assert.assertFalse(aimWorkflow.status.equalsIgnoreCase(status));

        verify(step).run();
        verifyNoMoreInteractions(step);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);
    }
    
    @Test
    public void testStartManually() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;
        aimWorkflow.nextRun = new Date(0);
        
        Assert.assertEquals(aimWorkflow.getNextRunDate().getTime(), 0);
        
        Long currentDate = System.currentTimeMillis();
        
        aimWorkflow.startManually();
        
        // Test time within 1 second
        Assert.assertTrue(Math.abs(aimWorkflow.getNextRunDate().getTime() - currentDate) < 1000);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);        
    }
    
    @Test
    public void testRunSuccess() {
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;

        when(conf.getWorkflowInterval()).thenReturn(-1L);
        
        WorkflowStep step = mock(WorkflowStep.class);
        aimWorkflow.steps.add(step);
        // set state and date ready for running.
        aimWorkflow.nextRun = new Date(0);
        aimWorkflow.state = WorkflowState.WAITING;
        
        aimWorkflow.run();
        
        verify(step).run();
        verifyNoMoreInteractions(step);
        
        verify(conf).getWorkflowInterval();
        verifyNoMoreInteractions(conf);
        
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);  
    }
    
    @Test
    public void testRunFailureState() {
        // Check when the state does not allow to run.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;

        when(conf.getWorkflowInterval()).thenReturn(-1L);
        
        WorkflowStep step = mock(WorkflowStep.class);
        aimWorkflow.steps.add(step);
        // set date ready for running, but state not ready to run
        aimWorkflow.nextRun = new Date(0);
        aimWorkflow.state = WorkflowState.RUNNING;
        
        aimWorkflow.run();
        
        verifyZeroInteractions(step);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);  
    }
    
    @Test
    public void testRunFailureDate() {
        // Check when the date does not allow to run.
        Configuration conf = mock(Configuration.class);
        CumulusRetriever cumulusRetriever = mock(CumulusRetriever.class);;
        ImageConverter imageConverter = mock(ImageConverter.class);;
        ImageRepository imageRepo = mock(ImageRepository.class);;
        WordRepository wordRepo = mock(WordRepository.class);;
        GoogleRetreiver googleRetriever = mock(GoogleRetreiver.class);;

        AimWorkflow aimWorkflow = new AimWorkflow();
        aimWorkflow.conf = conf;
        aimWorkflow.cumulusRetriever = cumulusRetriever;
        aimWorkflow.imageConverter = imageConverter;
        aimWorkflow.imageRepo = imageRepo;
        aimWorkflow.wordRepo = wordRepo;
        aimWorkflow.googleRetriever = googleRetriever;

        when(conf.getWorkflowInterval()).thenReturn(-1L);
        
        WorkflowStep step = mock(WorkflowStep.class);
        aimWorkflow.steps.add(step);
        // set state ready for running, but date not ready to run
        aimWorkflow.nextRun = new Date(Long.MAX_VALUE);
        aimWorkflow.state = WorkflowState.WAITING;
        
        aimWorkflow.run();
        
        verifyZeroInteractions(step);
        
        verifyZeroInteractions(conf);
        verifyZeroInteractions(cumulusRetriever);
        verifyZeroInteractions(imageConverter);
        verifyZeroInteractions(imageRepo);
        verifyZeroInteractions(wordRepo);
        verifyZeroInteractions(googleRetriever);  
    }
}
