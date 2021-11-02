package no.nav.dolly.bestilling.organisasjonforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.OrganisasjonBestillingService;
import no.nav.dolly.service.OrganisasjonNummerService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganisasjonClientTest {

    private static final String ORG_NUMMER = "123456789";
    private static final Long BESTILLING_ID = 123L;

    @Mock
    private static OrganisasjonConsumer organisasjonConsumer;

    @Mock
    private OrganisasjonNummerService organisasjonNummerService;

    @Mock
    private OrganisasjonProgressService organisasjonProgressService;

    @Mock
    private OrganisasjonBestillingService organisasjonBestillingService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private OrganisasjonClient organisasjonClient;


    private RsOrganisasjonBestilling bestilling;

    @BeforeEach
    public void setUp() {

        DeployResponse deployResponse = DeployResponse.builder()
                .build();

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
                .environments(List.of("q1"))
                .organisasjon(
                        RsOrganisasjonBestilling.SyntetiskOrganisasjon.builder()
                                .forretningsadresse(adresse)
                                .postadresse(adresse)
                                .build())
                .build();

        Set<String> orgnummer = new HashSet<>();
        orgnummer.add(ORG_NUMMER);

        lenient().when(mapperFacade.map(any(), eq(BestillingRequest.SyntetiskOrganisasjon.class))).thenReturn(requestOrganisasjon);
        lenient().when(organisasjonConsumer.postOrganisasjon(any())).thenReturn(new ResponseEntity<>(new BestillingResponse(orgnummer), HttpStatus.CREATED));
        lenient().when(organisasjonConsumer.deployOrganisasjon(any())).thenReturn(new ResponseEntity<>(deployResponse, HttpStatus.OK));
        lenient().when(organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(any())).thenReturn(Collections.singletonList(new OrganisasjonBestillingProgress()));
    }

    @Test
    public void should_run_deploy_organisasjon_exactly_one_time_for_one_hovedorganisasjon() {

        organisasjonClient.opprett(bestilling, BESTILLING_ID);

        verify(organisasjonConsumer, times(1)
                .description("Skal bare deploye organisasjoner en gang for en hoved organisasjon"))
                .deployOrganisasjon(any());
    }

    @Test
    public void should_run_orgnummer_save_exactly_one_time_for_one_hovedorganisasjon_with_one_underenhet() {

        organisasjonClient.opprett(bestilling, BESTILLING_ID);

        verify(organisasjonNummerService, times(1)
                .description("Skal lagre orgnummer nøyaktig en gang for en hovedorg med en underenhet"))
                .save(any());
    }

    @Test
    public void should_set_bestillingfeil_for_invalid_orgnummer_response() {

        when(organisasjonConsumer.postOrganisasjon(any())).thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        organisasjonClient.opprett(bestilling, BESTILLING_ID);

        verify(organisasjonBestillingService, times(1)
                .description("Skal sette feil på bestillingen nøyaktig en gang"))
                .setBestillingFeil(anyLong(), any());
    }
}
