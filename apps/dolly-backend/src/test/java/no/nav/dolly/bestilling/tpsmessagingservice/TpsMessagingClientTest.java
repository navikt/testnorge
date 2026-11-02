package no.nav.dolly.bestilling.tpsmessagingservice;

import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    void shouldSkipStatusWhenBestillingHasNoTpsData() {

        var pdldata = new PdlPersondata();
        pdldata.setPerson(new PersonDTO());

        var bestilling = RsDollyUtvidetBestilling.builder()
                .pdldata(pdldata)
                .bankkonto(new BankkontoData())
                .skjerming(RsSkjerming.builder().build())
                .build();
        var progress = new BestillingProgress();

        StepVerifier.create(tpsMessagingClient.gjenopprett(bestilling,
                        DollyPerson.builder().ident(IDENT).build(), progress, true))
                .expectNext(progress)
                .verifyComplete();

        verifyNoInteractions(miljoerConsumer, transactionHelperService, personServiceConsumer, tpsMessagingConsumer);
    }

    @Test
    void shouldPrepareStatusWhenBestillingHasTpsData() {

        var bestilling = RsDollyUtvidetBestilling.builder()
                .bankkonto(BankkontoData.builder()
                        .norskBankkonto(BankkontonrNorskDTO.builder()
                                .kontonummer("12345678903")
                                .build())
                        .build())
                .build();
        var progress = new BestillingProgress();

        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(List.of("q1")));
        when(transactionHelperService.persister(eq(progress), any(), any(), anyString(), eq("$")))
                .thenReturn(Mono.just(progress));
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.empty());
        when(tpsMessagingConsumer.sendNorskBankkontoRequest(eq(IDENT), any(), any())).thenReturn(Flux.empty());
        when(applicationConfig.getClientTimeout()).thenReturn(10L);

        StepVerifier.create(tpsMessagingClient.gjenopprett(bestilling,
                        DollyPerson.builder().ident(IDENT).build(), progress, true))
                .expectNext(progress)
                .verifyComplete();

        verify(transactionHelperService).persister(eq(progress), any(), any(), startsWith("Startet#q1:"), eq("$"));
    }
}
