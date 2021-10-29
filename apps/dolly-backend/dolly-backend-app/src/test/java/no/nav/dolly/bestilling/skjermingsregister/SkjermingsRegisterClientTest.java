package no.nav.dolly.bestilling.skjermingsregister;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
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
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;

@RunWith(MockitoJUnitRunner.class)
public class SkjermingsRegisterClientTest {

    @Mock
    private SkjermingsRegisterConsumer skjermingsRegisterConsumer;
    @Mock
    private DollyPersonCache dollyPersonCache;
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
    private DollyPerson person;
    private BestillingProgress progress;
    private LocalDateTime yesterday;

    @Before
    public void setUp() {

        yesterday = now().minusDays(1);

        response = SkjermingsDataResponse.builder()
                .build();

        progress = new BestillingProgress();

        bestilling = new RsDollyUtvidetBestilling();
        bestilling.setTpsf(RsTpsfUtvidetBestilling.builder()
                .identtype(IdentType.FNR.name())
                .build());
        bestilling.getTpsf().setEgenAnsattDatoFom(yesterday);

        String etternavn = "Syntesen";
        String fornavn = "Synt";
        String ident = "11111123456";
        person = DollyPerson.builder()
                .hovedperson(ident)
                .persondetaljer(Collections.singletonList(Person.builder()
                        .ident(ident)
                        .fornavn(fornavn)
                        .etternavn(etternavn)
                        .egenAnsattDatoFom(yesterday)
                        .build()))
                .build();

        when(dollyPersonCache.fetchIfEmpty(any())).thenReturn(DollyPerson.builder()
                .hovedperson(ident)
                .build());

        when(mapperFacade.map(any(), any())).thenReturn(new SkjermingsDataRequest());
    }

    @Test
    public void should_return_created_for_post_aktiv_skjermet() {

        when(skjermingsRegisterConsumer.postSkjerming(any())).thenReturn(new ResponseEntity<>(Collections.singletonList(response), HttpStatus.CREATED));
        when(skjermingsRegisterConsumer.getSkjerming(any())).thenReturn(new ResponseEntity<>(response, HttpStatus.NOT_FOUND));

        skjermingsRegisterClient.gjenopprett(bestilling, person, progress, false);
        assertThat(progress.getSkjermingsregisterStatus()).isNotNull().isEqualTo("OK");
    }

    @Test
    public void should_return_ok_for_put_tidligere_skjermet() {

        bestilling.getTpsf().setEgenAnsattDatoTom(yesterday);
        when(skjermingsRegisterConsumer.putSkjerming(any())).thenReturn(new ResponseEntity<>("", HttpStatus.OK));
        when(skjermingsRegisterConsumer.getSkjerming(any())).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        skjermingsRegisterClient.gjenopprett(bestilling, person, progress, false);
        assertThat(progress.getSkjermingsregisterStatus()).isNotNull().isEqualTo("OK");
    }

    @Test
    public void should_return_empty_status_for_ikke_skjermet() {

        person.getPersondetaljer().get(0).setEgenAnsattDatoFom(null);

        skjermingsRegisterClient.gjenopprett(bestilling, person, progress, false);
        assertThat(progress.getSkjermingsregisterStatus()).isEqualTo("null");
    }

}
