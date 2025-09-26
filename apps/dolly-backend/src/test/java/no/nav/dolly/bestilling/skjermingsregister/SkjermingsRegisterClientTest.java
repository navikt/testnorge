package no.nav.dolly.bestilling.skjermingsregister;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isSkjerming;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isTpsMessagingEgenansatt;
import static no.nav.dolly.util.DateZoneUtil.CET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkjermingsRegisterClientTest {

    @Mock
    private SkjermingsRegisterConsumer skjermingsRegisterConsumer;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private SkjermingsRegisterClient skjermingsRegisterClient;

    @Test
    void whenGjenopprettWithSkjerming_thenClientFuture() {

        try (MockedStatic<SkjermingUtil> skjermingUtilMockedStatic = mockStatic(SkjermingUtil.class)) {
            RsDollyUtvidetBestilling bestilling = mock(RsDollyUtvidetBestilling.class);
            DollyPerson dollyPerson = DollyPerson.builder().ident("12345678901").build();
            BestillingProgress progress = new BestillingProgress();

            skjermingUtilMockedStatic.when(() -> isSkjerming(bestilling)).thenReturn(true);
            when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder()
                    .data(PdlPersonBolk.Data.builder()
                            .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                    .ident("12345678901")
                                    .person(new PdlPerson.Person())
                                    .build()))
                            .build())
                    .build()));
            when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.just(progress));
            when(mapperFacade.map(any(), eq(SkjermingDataRequest.class), any())).thenReturn(SkjermingDataRequest.builder()
                    .personident("12345678901")
                    .skjermetFra(LocalDate.now(CET).atStartOfDay())
                    .skjermetTil(LocalDate.now(CET).plusYears(1).atStartOfDay())
                    .build());
            when(skjermingsRegisterConsumer.oppdaterPerson(any())).thenReturn(Mono.just(new SkjermingDataResponse()));

            StepVerifier.create(skjermingsRegisterClient.gjenopprett(bestilling, dollyPerson, progress, true))
                    .expectNextCount(1)
                    .verifyComplete();
        }
    }

    @Test
    void whenGjenopprettWithoutSkjerming_thenEmptyClientFuture() {

        try (MockedStatic<SkjermingUtil> skjermingUtilMockedStatic = mockStatic(SkjermingUtil.class)) {

            var bestilling = new RsDollyUtvidetBestilling();
            var dollyPerson = DollyPerson.builder().ident("12345678901").build();
            var progress = new BestillingProgress();

            skjermingUtilMockedStatic.when(() -> isSkjerming(bestilling)).thenReturn(false);
            skjermingUtilMockedStatic.when(() -> isTpsMessagingEgenansatt(bestilling)).thenReturn(false);

            StepVerifier.create(skjermingsRegisterClient.gjenopprett(bestilling, dollyPerson, progress, true))
                    .expectNextCount(0)
                    .verifyComplete();
        }
    }

    @Test
    void whenTpsMessagingHasSkjerming_thenRequestIsMappedCorrectly() {

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setTpsMessaging(RsTpsMessaging.builder()
                .egenAnsattDatoFom(LocalDate.of(2010, 1, 1))
                .egenAnsattDatoTom(LocalDate.of(2020, 1, 1))
                .build());

        DollyPerson dollyPerson = DollyPerson.builder().ident("12345678901").build();
        BestillingProgress progress = new BestillingProgress();

        PdlPersonBolk.PersonBolk personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(new PdlPerson.Person())
                .build();

        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build()));

        when(mapperFacade.map(any(), eq(SkjermingDataRequest.class), any())).thenReturn(SkjermingDataRequest.builder()
                .personident("12345678901")
                .skjermetFra(LocalDate.of(2010, 1, 1).atStartOfDay())
                .skjermetTil(LocalDate.of(2020, 1, 1).atStartOfDay())
                .build());

        when(skjermingsRegisterConsumer.oppdaterPerson(any())).thenReturn(Mono.just(new SkjermingDataResponse()));
        when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.just(progress));

        try (MockedStatic<SkjermingUtil> skjermingUtilMockedStatic = mockStatic(SkjermingUtil.class)) {
            skjermingUtilMockedStatic.when(() -> isSkjerming(bestilling)).thenReturn(false);
            skjermingUtilMockedStatic.when(() -> isTpsMessagingEgenansatt(bestilling)).thenReturn(true);

            StepVerifier.create(skjermingsRegisterClient.gjenopprett(bestilling, dollyPerson, progress, true))
                    .expectNextCount(1)
                    .verifyComplete();

            var requestCaptor = ArgumentCaptor.forClass(SkjermingDataRequest.class);
            verify(skjermingsRegisterConsumer).oppdaterPerson(requestCaptor.capture());

            var capturedRequest = requestCaptor.getValue();
            assertEquals("12345678901", capturedRequest.getPersonident());
            assertEquals(LocalDate.of(2010, 1, 1).atStartOfDay(), capturedRequest.getSkjermetFra());
            assertEquals(LocalDate.of(2020, 1, 1).atStartOfDay(), capturedRequest.getSkjermetTil());
        }
    }

    @Test
    void whenRelease_thenSkjermingIsDeleted() {

        var identer = List.of("12345678901");

        when(skjermingsRegisterConsumer.deleteSkjerming(any())).thenReturn(Mono.empty());

        skjermingsRegisterClient.release(identer);

        verify(skjermingsRegisterConsumer, times(1)).deleteSkjerming(identer);
    }

    @Test
    void whenGetStatus_thenCorrectStatusReturned() {

        var response = new SkjermingDataResponse();
        response.setError(null);

        var status = skjermingsRegisterClient.getStatus(response);
        assertEquals("OK", status);

        response.setError("Some error");
        when(errorStatusDecoder.getErrorText(null, "Some error")).thenReturn("Error text");

        status = skjermingsRegisterClient.getStatus(response);
        assertEquals("Error text", status);
    }
}