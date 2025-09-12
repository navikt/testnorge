package no.nav.dolly.bestilling.organisasjonforvalter;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import no.nav.dolly.service.OrganisasjonBestillingService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisasjonClientTest {

    private static final String ORG_NUMMER = "123456789";
    private static final Long BESTILLING_ID = 123L;

    @Mock
    private static OrganisasjonConsumer organisasjonConsumer;

    @Mock
    private OrganisasjonProgressService organisasjonProgressService;

    @Mock
    private OrganisasjonBestillingService organisasjonBestillingService;

    @Mock
    private OrganisasjonBestillingProgressRepository organisasjonBestillingProgressRepository;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private OrganisasjonClient organisasjonClient;

    private RsOrganisasjonBestilling bestilling;

    @BeforeEach
    void setUp() {

        BestillingRequest.SyntetiskOrganisasjon requestOrganisasjon = BestillingRequest.SyntetiskOrganisasjon.builder()
                .formaal("Testing")
                .build();

        BestillingRequest.SyntetiskOrganisasjon underOrganisasjon = BestillingRequest.SyntetiskOrganisasjon.builder()
                .formaal("underenhet")
                .build();

        requestOrganisasjon.setUnderenheter(Collections.singletonList(underOrganisasjon));

        RsOrganisasjonBestilling.SyntetiskOrganisasjon.Adresse adresse = RsOrganisasjonBestilling.SyntetiskOrganisasjon.Adresse.builder()
                .postnr("1234")
                .landkode("NO")
                .kommunenr("123")
                .adresselinjer(Collections.singletonList("Gate 1"))
                .build();

        bestilling = RsOrganisasjonBestilling.builder()
                .environments(singleton("q1"))
                .organisasjon(
                        RsOrganisasjonBestilling.SyntetiskOrganisasjon.builder()
                                .forretningsadresse(adresse)
                                .postadresse(adresse)
                                .build())
                .build();

        Set<String> orgnummer = new HashSet<>();
        orgnummer.add(ORG_NUMMER);

        when(mapperFacade.map(any(), eq(BestillingRequest.SyntetiskOrganisasjon.class))).thenReturn(requestOrganisasjon);
        when(organisasjonConsumer.postOrganisasjon(any())).thenReturn(Mono.just(new BestillingResponse(orgnummer)));
           }

    @Test
    void should_run_deploy_organisasjon_exactly_one_time_for_one_hovedorganisasjon() {

        var deployResponse = DeployResponse.builder().build();

        when(organisasjonConsumer.deployOrganisasjon(any())).thenReturn(Mono.just(deployResponse));
        when(organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(any())).thenReturn(Flux.just(new OrganisasjonBestillingProgress()));
        when(organisasjonBestillingProgressRepository.save(any())).thenReturn(Mono.just(new OrganisasjonBestillingProgress()));

        StepVerifier.create(organisasjonClient.opprett(bestilling, OrganisasjonBestilling.builder()
                .id(BESTILLING_ID)
                .build()))
                .expectNextCount(0)
                .verifyComplete();

        verify(organisasjonConsumer, times(1)
                .description("Skal bare deploye organisasjoner en gang for en hoved organisasjon"))
                .deployOrganisasjon(any());
    }

    @Test
    void should_set_bestillingfeil_for_invalid_orgnummer_response() {

        when(organisasjonConsumer.postOrganisasjon(any())).thenThrow(new RuntimeException("Feil ved opprettelse av organisasjon"));
        when(organisasjonBestillingService.setBestillingFeil(any(), any())).thenReturn(Mono.empty());
        when(organisasjonProgressService.setBestillingFeil(anyLong(), any())).thenReturn(Mono.empty());

        StepVerifier.create(organisasjonClient.opprett(bestilling, OrganisasjonBestilling.builder()
                .id(BESTILLING_ID)
                .build()))
                .expectNextCount(0)
                .verifyComplete();

        verify(organisasjonConsumer).postOrganisasjon(any());
        verify(organisasjonBestillingService).setBestillingFeil(any(), any());
        verify(organisasjonProgressService).setBestillingFeil(anyLong(), any());
    }
}
