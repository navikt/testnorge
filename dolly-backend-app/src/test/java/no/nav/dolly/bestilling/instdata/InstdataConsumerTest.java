package no.nav.dolly.bestilling.instdata;

import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class InstdataConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENVIRONMENT = "U2";

    @Mock
    private ProvidersProps providersProps;

    @Mock RestTemplate restTemplate;

    @InjectMocks
    private InstdataConsumer instdataConsumer;

    @Before
    public void setup() {
        when(providersProps.getInstdata()).thenReturn(
                ProvidersProps.Instdata.builder()
                .url("localhost")
                .build());
    }

    @Test
    public void deleteInstdata() {

        instdataConsumer.deleteInstdata(IDENT, ENVIRONMENT);

        verify(providersProps).getInstdata();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(InstdataResponse[].class));
    }

    @Test
    public void postInstdata() {

        instdataConsumer.postInstdata(newArrayList(Instdata.builder().build()), ENVIRONMENT);

        verify(providersProps).getInstdata();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(InstdataResponse[].class));
    }
}