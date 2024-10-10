package no.nav.dolly.bestilling.fullmakt;

import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FullmaktClientTest {

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private FullmaktConsumer fullmaktConsumer;

    @Mock
    private PdlDataConsumer pdlDataConsumer;

    @InjectMocks
    private FullmaktClient fullmaktClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSetMissingFullmektigFromPdlRelasjon() {
        RsDollyUtvidetBestilling bestilling = new RsDollyUtvidetBestilling();

        bestilling.setFullmakt(List.of(RsFullmakt.builder()
                .omraade(singletonList(RsFullmakt.Omraade.builder()
                        .tema("AAP")
                        .handling(singletonList("LES"))
                        .build()))
                .gyldigFraOgMed(LocalDateTime.of(2021, 1, 1, 0, 0))
                .gyldigTilOgMed(LocalDateTime.of(2022, 1, 1, 0, 0))
                .build()));

        DollyPerson dollyPerson = DollyPerson.builder().ident("12345678901").build();
        BestillingProgress progress = new BestillingProgress();

        FullPersonDTO fullPersonDTO = FullPersonDTO.builder()
                .relasjoner(List.of(
                        FullPersonDTO.RelasjonDTO.builder()
                                .relasjonType(RelasjonType.FULLMEKTIG)
                                .relatertPerson(PersonDTO.builder().ident("12345678902").build())
                                .build()
                ))
                .build();

        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fullPersonDTO));

        FullmaktResponse fullmaktResponse = FullmaktResponse.builder().status(HttpStatus.OK).build();
        when(fullmaktConsumer.createFullmaktData(any(), any())).thenReturn(Flux.just(fullmaktResponse));

        Flux<ClientFuture> result = fullmaktClient.gjenopprett(bestilling, dollyPerson, progress, true);

        List<ClientFuture> clientFutures = result.collectList().block();
        assertEquals(1, clientFutures.size());

        ArgumentCaptor<List<RsFullmakt>> fullmaktCaptor = ArgumentCaptor.forClass(List.class);
        verify(fullmaktConsumer).createFullmaktData(fullmaktCaptor.capture(), any());

        List<RsFullmakt> capturedFullmaktList = fullmaktCaptor.getValue();
        assertEquals("12345678902", capturedFullmaktList.get(0).getFullmektig());
    }
}