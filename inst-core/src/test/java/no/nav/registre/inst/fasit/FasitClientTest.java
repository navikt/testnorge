package no.nav.registre.inst.fasit;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import no.nav.freg.fasit.utils.FasitService;

@RunWith(MockitoJUnitRunner.class)
public class FasitClientTest {

    @Mock
    private FasitService fasitService;

    @InjectMocks
    private FasitClient fasitClient;

    private final static List<String> environments = Arrays.asList("q1", "q2", "q4");

    @Test
    public void findEnvironmentNamesTest() {
        when(fasitService.findEnvironmentNames("q")).thenReturn(environments);

        List<String> environments = fasitClient.getAllEnvironments("q");
        assertFalse(environments.isEmpty());
    }
}