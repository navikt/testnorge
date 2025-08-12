package no.nav.dolly.bestilling.fullmakt;

import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FullmaktClientTest {

    private static final String NAVN = "Fornavn Etternavn";
    private static final String IDENT = "12345611111";
    private static final String RELASJON_IDENT = "12345622222";

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private FullmaktConsumer fullmaktConsumer;

    @Mock
    private PdlDataConsumer pdlDataConsumer;

    @InjectMocks
    private FullmaktClient fullmaktClient;

    @Test
    void shouldSetMissingFullmektigFromPdlRelasjon() {
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
        when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.just(progress));

        ArgumentCaptor<List<RsFullmakt>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        StepVerifier.create(fullmaktClient.gjenopprett(bestilling, dollyPerson, progress, true))
                .assertNext(status -> {
                    verify(fullmaktConsumer).createFullmaktData(argumentCaptor.capture(), any());
                    assertEquals(RELASJON_IDENT, argumentCaptor.getValue().getFirst().getFullmektig());
                    assertEquals(NAVN,  argumentCaptor.getValue().getFirst().getFullmektigsNavn());
                })
                .verifyComplete();
        }
}