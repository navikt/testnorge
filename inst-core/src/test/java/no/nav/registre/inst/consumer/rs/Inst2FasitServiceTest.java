package no.nav.registre.inst.consumer.rs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.inst.fasit.FasitApiConsumer;
import no.nav.registre.inst.fasit.FasitResourceScope;
import no.nav.registre.inst.fasit.FasitResourceWithUnmappedProperties;
import no.nav.registre.inst.service.Inst2FasitService;

@RunWith(MockitoJUnitRunner.class)
public class Inst2FasitServiceTest {

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private Inst2FasitService inst2FasitService;

    @Before
    public void setUp() {
        Map<String, String> map = new HashMap<>();
        map.put("url", "BaseUrl");

        when(fasitApiConsumer.fetchResources(anyString(), anyString()))
                .thenReturn(new FasitResourceWithUnmappedProperties[] {
                        FasitResourceWithUnmappedProperties.builder()
                                .scope(FasitResourceScope.builder()
                                        .environment("t1")
                                        .zone("fss")
                                        .build())
                                .properties(map)
                                .build()
                });
    }

    @Test
    public void fetchWsUrslAllEnvironments_OK() {
        String urlResult = inst2FasitService.getUrlForEnv("t1");

        assertThat(urlResult, is(equalTo("BaseUrl")));
    }
}