package no.nav.identpool.ajourhold.fasit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.QueueManager;
import no.nav.freg.fasit.utils.domain.ResourceType;
import no.nav.freg.fasit.utils.domain.Zone;

@RunWith(MockitoJUnitRunner.class)
public class FasitClientTest {

    private final static List<String> environments = Arrays.asList("q1", "q2", "q4");

    @Mock
    private FasitService fasitService;

    @Before
    public void before() {
        when(fasitService.find("mqGateway", ResourceType.QUEUE_MANAGER, "", null, Zone.FSS, QueueManager.class))
                .thenReturn(new QueueManager("name", "host", "port"));
        when(fasitService.findEnvironmentNames("q")).thenReturn(environments);
    }

    @Test
    public void getQueueManagerTest() {
        FasitClient fasitClient = new FasitClient(fasitService);
        QueueManager queueManager = fasitClient.getQueueManager("");
        assertThat(queueManager.getName(), is("name"));
        assertThat(queueManager.getHostname(), is("host"));
        assertThat(queueManager.getPort(), is("port"));
        verify(fasitService, times(1)).find("mqGateway", ResourceType.QUEUE_MANAGER, "", null, Zone.FSS, QueueManager.class);
    }

    @Test
    public void findEnvironmentNamesTest() {
        FasitClient fasitClient = new FasitClient(fasitService);
        List<String> environments = fasitClient.getAllEnvironments("q");
        assertThat(environments, containsInAnyOrder(environments.toArray()));
    }
}
