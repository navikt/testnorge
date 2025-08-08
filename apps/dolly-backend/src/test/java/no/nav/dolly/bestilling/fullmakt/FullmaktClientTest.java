package no.nav.dolly.bestilling.fullmakt;

import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FullmaktClientTest {

    private static final String NAVN = "Fornavn Etternavn";
    private static final String IDENT = "12345611111";
    private static final String RELASJON_IDENT = "12345622222";

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
        var bestilling = new RsDollyUtvidetBestilling();

        bestilling.setFullmakt(List.of(RsFullmakt.builder()
                .omraade(singletonList(RsFullmakt.Omraade.builder()
                        .tema("AAP")
                        .handling(singletonList("LES"))
                        .build()))
                .fullmektigsNavn(NAVN)
                .gyldigFraOgMed(LocalDateTime.of(2021, 1, 1, 0, 0))
                .gyldigTilOgMed(LocalDateTime.of(2022, 1, 1, 0, 0))
                .build()));

        var dollyPerson = DollyPerson.builder().ident(IDENT).build();
        var progress = new BestillingProgress();

        var fullPersonDTO = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident(IDENT)
                        .build())
                .relasjoner(List.of(
                        FullPersonDTO.RelasjonDTO.builder()
                                .relasjonType(RelasjonType.FULLMEKTIG)
                                .relatertPerson(PersonDTO.builder()
                                        .navn(singletonList(NavnDTO.builder()
                                                .fornavn("Fornavn")
                                                .etternavn("Etternavn")
                                                .build()))
                                        .ident(RELASJON_IDENT).build())
                                .build()
                ))
                .build();

        when(pdlDataConsumer.getPersoner(any())).thenReturn(Flux.just(fullPersonDTO));

        var fullmaktResponse = FullmaktResponse.builder().fullmakt(emptyList()).build();
        when(fullmaktConsumer.createFullmaktData(any(), any())).thenReturn(Flux.just(fullmaktResponse));

        var result = fullmaktClient.gjenopprett(bestilling, dollyPerson, progress, true);

//        var clientFutures = result.collectList().block();
//        assertEquals(1, clientFutures.size());

        var fullmaktCaptor = ArgumentCaptor.forClass(List.class);
        verify(fullmaktConsumer).createFullmaktData(fullmaktCaptor.capture(), any());

        List<RsFullmakt> capturedFullmaktList = fullmaktCaptor.getValue();
        assertEquals(RELASJON_IDENT, capturedFullmaktList.getFirst().getFullmektig());
        assertEquals(NAVN, capturedFullmaktList.getFirst().getFullmektigsNavn());
    }
}