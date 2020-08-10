package no.nav.dolly.bestilling.skjermingsregister;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TpsfPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;

@RunWith(MockitoJUnitRunner.class)
public class SkjermingsRegisterClientTest {

    @Mock
    private SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    @Mock
    private TpsfPersonCache tpsfPersonCache;
    @Mock
    private TransaksjonMappingService transaksjonMappingService;
    @Mock
    private ErrorStatusDecoder errorStatusDecoder;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MapperFacade mapperFacade;
    @InjectMocks
    private SkjermingsRegisterClient skjermingsRegisterClient;
    private SkjermingsDataResponse response;
    private RsDollyUtvidetBestilling bestilling;
    private TpsPerson person;

    @Test
    public void should_return_ok_for_get_aktiv_skjermet_eksisterer_allerede() {

        BestillingProgress progress = new BestillingProgress();
        when(skjermingsRegisterConsumer.postSkjerming(any())).thenReturn(new ResponseEntity<>(Collections.singletonList(response), HttpStatus.BAD_REQUEST));
        when(skjermingsRegisterConsumer.getSkjerming(any())).thenReturn(new ResponseEntity<>(Collections.singletonList(response), HttpStatus.OK));

        skjermingsRegisterClient.gjenopprett(bestilling, person, progress, false);
        assertThat(progress.getSkjermingsregisterStatus()).isNotNull().contains("eksisterer allerede i skjermingsregister");
    }

    @Test
    public void should_return_created_for_post_aktiv_skjermet() {

        ResponseEntity<List<SkjermingsDataResponse>> responseEntity;
        when(skjermingsRegisterConsumer.postSkjerming(any())).thenReturn(responseEntity = new ResponseEntity<>(Collections.singletonList(response), HttpStatus.CREATED));

        skjermingsRegisterClient.gjenopprett(bestilling, person, new BestillingProgress(), false);
        assertThat(responseEntity.getStatusCode()).isNotNull().isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void should_return_ok_for_put_tidligere_skjermet() {

        person.getPersondetaljer().get(0).setEgenAnsattDatoTom(now().minusDays(1));
        ResponseEntity<String> responseEntity;
        when(skjermingsRegisterConsumer.putSkjerming(any())).thenReturn(responseEntity = new ResponseEntity<>("", HttpStatus.OK));

        skjermingsRegisterClient.gjenopprett(bestilling, person, new BestillingProgress(), false);
        assertThat(responseEntity.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
    }

    @Test
    public void should_return_empty_status_for_ikke_skjermet() {

        person.getPersondetaljer().get(0).setEgenAnsattDatoFom(null);
        BestillingProgress status = new BestillingProgress();

        skjermingsRegisterClient.gjenopprett(bestilling, person, status, false);
        assertThat(status.getSkjermingsregisterStatus()).isNotNull().isEmpty();
    }

    @Before
    public void setUp() {

        LocalDateTime yesterday = now().minusDays(1);

        response = SkjermingsDataResponse.builder()
                .build();

        bestilling = new RsDollyUtvidetBestilling();
        bestilling.setTpsf(RsTpsfUtvidetBestilling.builder()
                .identtype(IdentType.FNR.name())
                .build());

        String etternavn = "Syntesen";
        String fornavn = "Synt";
        String ident = "11111123456";
        person = TpsPerson.builder()
                .hovedperson(ident)
                .persondetaljer(Collections.singletonList(Person.builder()
                        .ident(ident)
                        .fornavn(fornavn)
                        .etternavn(etternavn)
                        .egenAnsattDatoFom(yesterday)
                        .build()))
                .build();

        when(tpsfPersonCache.fetchIfEmpty(any())).thenReturn(TpsPerson.builder()
                .hovedperson(ident)
                .build());

        when(mapperFacade.map(any(), any())).thenReturn(new SkjermingsDataRequest());
    }
}
