package no.nav.identpool.fasit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import no.nav.identpool.test.mockito.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tester kobling mot Fasit")
class FasitClientTest {

    private final static List<String> environments = Arrays.asList("q1", "q2", "q4");

    @Mock
    private FasitService fasitService;

    @InjectMocks
    private FasitClient fasitClient;

    @BeforeEach
    void before() {
        when(fasitService.find("mqGateway", ResourceType.QUEUE_MANAGER, "", null, Zone.FSS, QueueManager.class))
                .thenReturn(new QueueManager("name", "host", "port"));
        when(fasitService.findEnvironmentNames("q")).thenReturn(environments);
    }

    @Test
    @DisplayName("Sjekker at det fungerer å hente QueueManager fra Fasit")
    void getQueueManagerTest() {
        QueueManager queueManager = fasitClient.getQueueManager("");
        assertThat(queueManager.getName(), is("name"));
        assertThat(queueManager.getHostname(), is("host"));
        assertThat(queueManager.getPort(), is("port"));
        verify(fasitService, times(1)).find("mqGateway", ResourceType.QUEUE_MANAGER, "", null, Zone.FSS, QueueManager.class);
    }

    @Test
    @DisplayName("Sjekker at miljøer hentes ut korrekt")
    void findEnvironmentNamesTest() {
        List<String> environments = fasitClient.getAllEnvironments("q");
        assertFalse(environments.isEmpty());
    }
}
