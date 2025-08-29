package no.nav.dolly.bestilling.skjermingsregister;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
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
import no.nav.dolly.util.TransactionHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isSkjerming;
import static no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil.isTpsMessagingEgenansatt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGjenopprettWithSkjerming_thenClientFuture() {
        try (MockedStatic<SkjermingUtil> skjermingUtilMockedStatic = mockStatic(SkjermingUtil.class)) {
            RsDollyUtvidetBestilling bestilling = mock(RsDollyUtvidetBestilling.class);
            DollyPerson dollyPerson = DollyPerson.builder().ident("12345678901").build();
            BestillingProgress progress = new BestillingProgress();

            skjermingUtilMockedStatic.when(() -> isSkjerming(bestilling)).thenReturn(true);
            when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(new PdlPersonBolk()));
            when(skjermingsRegisterConsumer.oppdaterPerson(any())).thenReturn(Mono.just(new SkjermingDataResponse()));

            Flux<ClientFuture> result = skjermingsRegisterClient.gjenopprett(bestilling, dollyPerson, progress, true);

            List<ClientFuture> clientFutures = result.collectList().block();
            assertEquals(1, clientFutures.size());
        }
    }

    @Test
    void whenGjenopprettWithoutSkjerming_thenEmptyClientFuture() {
        try (MockedStatic<SkjermingUtil> skjermingUtilMockedStatic = mockStatic(SkjermingUtil.class)) {
            RsDollyUtvidetBestilling bestilling = mock(RsDollyUtvidetBestilling.class);
            DollyPerson dollyPerson = DollyPerson.builder().ident("12345678901").build();
            BestillingProgress progress = new BestillingProgress();

            skjermingUtilMockedStatic.when(() -> isSkjerming(bestilling)).thenReturn(false);
            skjermingUtilMockedStatic.when(() -> isTpsMessagingEgenansatt(bestilling)).thenReturn(false);

            Flux<ClientFuture> result = skjermingsRegisterClient.gjenopprett(bestilling, dollyPerson, progress, true);

            List<ClientFuture> clientFutures = result.collectList().block();
            assertTrue(clientFutures.isEmpty());
        }
    }

    @Test
    void whenTpsMessagingHasSkjerming_thenRequestIsMappedCorrectly() {

        RsDollyUtvidetBestilling bestilling = new RsDollyUtvidetBestilling();
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

        try (MockedStatic<SkjermingUtil> skjermingUtilMockedStatic = mockStatic(SkjermingUtil.class)) {
            skjermingUtilMockedStatic.when(() -> isSkjerming(bestilling)).thenReturn(false);
            skjermingUtilMockedStatic.when(() -> isTpsMessagingEgenansatt(bestilling)).thenReturn(true);

            Flux<ClientFuture> result = skjermingsRegisterClient.gjenopprett(bestilling, dollyPerson, progress, true);
            List<ClientFuture> clientFutures = result.collectList().block();

            ArgumentCaptor<SkjermingDataRequest> requestCaptor = ArgumentCaptor.forClass(SkjermingDataRequest.class);
            verify(skjermingsRegisterConsumer).oppdaterPerson(requestCaptor.capture());


            SkjermingDataRequest capturedRequest = requestCaptor.getValue();
            assertEquals("12345678901", capturedRequest.getPersonident());
            assertEquals(LocalDate.of(2010, 1, 1).atStartOfDay(), capturedRequest.getSkjermetFra());
            assertEquals(LocalDate.of(2020, 1, 1).atStartOfDay(), capturedRequest.getSkjermetTil());

            assertEquals(1, clientFutures.size());
        }
    }

    @Test
    void whenRelease_thenSkjermingIsDeleted() {
        List<String> identer = List.of("12345678901");

        when(skjermingsRegisterConsumer.deleteSkjerming(any())).thenReturn(Mono.empty());

        skjermingsRegisterClient.release(identer);

        verify(skjermingsRegisterConsumer, times(1)).deleteSkjerming(identer);
    }

    @Test
    void whenFuturePersist_thenProgressPersisted() {
        BestillingProgress progress = new BestillingProgress();
        String status = "OK";

        ClientFuture clientFuture = skjermingsRegisterClient.futurePersist(progress, status);
        BestillingProgress result = clientFuture.get();

        ArgumentCaptor<BiConsumer<BestillingProgress, String>> captor = ArgumentCaptor.forClass(BiConsumer.class);
        verify(transactionHelperService, times(1)).persister(eq(progress), captor.capture(), eq(status));

        BiConsumer<BestillingProgress, String> capturedLambda = captor.getValue();
        capturedLambda.accept(progress, status);

        assertEquals(progress, result);
    }

    @Test
    void whenGetStatus_thenCorrectStatusReturned() {
        SkjermingDataResponse response = new SkjermingDataResponse();
        response.setError(null);

        String status = skjermingsRegisterClient.getStatus(response);
        assertEquals("OK", status);

        response.setError("Some error");
        when(errorStatusDecoder.getErrorText(null, "Some error")).thenReturn("Error text");

        status = skjermingsRegisterClient.getStatus(response);
        assertEquals("Error text", status);
    }
}