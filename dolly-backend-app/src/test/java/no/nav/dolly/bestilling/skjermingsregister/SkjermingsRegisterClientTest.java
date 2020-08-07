package no.nav.dolly.bestilling.skjermingsregister;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;

@RunWith(MockitoJUnitRunner.class)
public class SkjermingsRegisterClientTest {

    @Mock
    private SkjermingsRegisterConsumer skjermingsRegisterConsumer;

    @InjectMocks
    private SkjermingsRegisterClient skjermingsRegisterClient;

    private SkjermingsDataRequest request;
    private SkjermingsDataResponse response;
    private LocalDateTime yesterday;

    @Ignore
    @Test
    private should_return_ok_for_post_aktiv_egenansatt() {

        when(skjermingsRegisterConsumer.postSkjerming(Collections.singletonList(request))).thenReturn()
    }

    @Before
    private void setUp() {

        yesterday = now().minusDays(1);

        request = SkjermingsDataRequest.builder()
                .fornavn("Synt")
                .etternavn("Syntesen")
                .personident("11111156789")
                .skjermetFra(yesterday)
                .build();

        response = SkjermingsDataResponse.builder()
                .skjermetFra(yesterday.toString())
                .build();
    }
}
