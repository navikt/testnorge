package no.nav.registre.sdForvalter.consumer.rs;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@Profile("test")
@Ignore
@ContextConfiguration(classes = HodejegerenConsumer.class)
public class HodejegerenConsumerTest {

    @Mock
    RestTemplate restTemplate;
    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;
    @InjectMocks
    private HodejegerenConsumer hodejegerenConsumer;

    @Test
    public void getPlaygroupFnrs() {
        HashSet<String> data = new HashSet<>();
        data.add("123");
        when(restTemplate.getForObject("testnorge-hodejegeren/api/v1/alle-identer/" + staticDataPlaygroup, Set.class)).thenReturn(data);

        Set<String> fnrs = hodejegerenConsumer.getPlaygroupFnrs(staticDataPlaygroup);

        assertTrue(fnrs.contains("123"));

    }
}