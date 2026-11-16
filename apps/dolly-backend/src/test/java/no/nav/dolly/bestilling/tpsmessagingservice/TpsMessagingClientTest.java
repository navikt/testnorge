package no.nav.dolly.bestilling.tpsmessagingservice;

import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TpsMessagingClientTest {

    private static final String IDENT = "12345678901";

    @Mock
    private TpsMessagingConsumer tpsMessagingConsumer;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private MiljoerConsumer miljoerConsumer;

    @Mock
    private ApplicationConfig applicationConfig;

    @InjectMocks
    private TpsMessagingClient tpsMessagingClient;

    @Test
    void shouldSkipStartedStatusWhenBestillingHasNoTpsData() {

        var bestilling = RsDollyUtvidetBestilling.builder().build();
        var progress = new BestillingProgress();
        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(List.of("q1")));
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT))).thenReturn(Flux.empty());
        when(applicationConfig.getClientTimeout()).thenReturn(10L);
        when(transactionHelperService.persister(eq(progress), any(), any(), eq(""), eq("$")))
                .thenReturn(Mono.just(progress));

        StepVerifier.create(tpsMessagingClient.gjenopprett(bestilling,
                        DollyPerson.builder().ident(IDENT).build(), progress))
                .expectNext(progress)
                .verifyComplete();

        verify(transactionHelperService).persister(eq(progress), any(), any(), eq(""), eq("$"));
        verifyNoInteractions(tpsMessagingConsumer);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldSendTpsDataAndUpdateStatusOnlyOutsideOrderMode(boolean ordre) {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .bankkonto(BankkontoData.builder()
                        .norskBankkonto(BankkontonrNorskDTO.builder()
                                .kontonummer("12345678903")
                                .build())
                        .build())
                .build();
        var progress = new BestillingProgress();

        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(List.of("q1")));
        if (!ordre) {
            when(transactionHelperService.persister(eq(progress), any(), any(), anyString(), eq("$")))
                    .thenReturn(Mono.just(progress));
        }
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT))).thenReturn(Flux.empty());
        when(tpsMessagingConsumer.sendNorskBankkontoRequest(eq(IDENT), isNull(),
                eq(bestilling.getBankkonto().getNorskBankkonto())))
                .thenReturn(Flux.just(TpsMeldingResponseDTO.builder().miljoe("q1").status("OK").build()));
        when(applicationConfig.getClientTimeout()).thenReturn(10L);

        StepVerifier.create(tpsMessagingClient.gjenopprett(bestilling,
                        DollyPerson.builder().ident(IDENT).isOrdre(ordre).build(), progress))
                .expectNext(progress)
                .verifyComplete();

        verify(tpsMessagingConsumer).sendNorskBankkontoRequest(eq(IDENT), isNull(),
                eq(bestilling.getBankkonto().getNorskBankkonto()));
        if (ordre) {
            verifyNoInteractions(transactionHelperService);
        } else {
            verify(transactionHelperService).persister(eq(progress), any(), any(), startsWith("Startet#q1:"), eq("$"));
            verify(transactionHelperService).persister(eq(progress), any(), any(), eq("NorskBankkonto#q1:OK"), eq("$"));
        }
    }
}
