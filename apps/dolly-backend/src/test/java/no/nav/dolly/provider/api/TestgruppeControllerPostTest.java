package no.nav.dolly.provider.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.tpsmessagingservice.MiljoerConsumer;
import no.nav.dolly.common.repository.GruppeTestRepository;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilInnsendingType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.YtelseType;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.securitycore.domain.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /api/v1/gruppe")
class TestgruppeControllerPostTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

//    @MockitoBean
//    private GetUserInfo getUserInfo;

    @MockitoBean
    private MiljoerConsumer miljoerConsumer;

    @Autowired
    private GruppeTestRepository gruppeRepository;

    @Autowired
    private BestillingRepository bestillingRepository;

    @Test
    @DisplayName("Returnerer opprettet Testgruppe med innlogget bruker som eier")
    void createTestgruppeAndSetCurrentUserAsOwner()
            throws Exception {

        var bruker = super.createBruker();
//        when(getUserInfo.call())
//                .thenReturn(Optional.of(new UserInfo(bruker.getBrukerId(), "", "", bruker.getBrukernavn())));

        var request = RsOpprettEndreTestgruppe
                .builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();
        mockMvc
                .perform(
                        post("/api/v1/gruppe")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
//                    var response = objectMapper.readValue(result.getResponse().getContentAsString(), RsTestgruppeMedBestillingId.class);
//                    assertThat(response.getId()).isNotNull();
//                    assertThat(response.getNavn()).isEqualTo("mingruppe");
//                    assertThat(response.getHensikt()).isEqualTo("hensikt");
//                    assertThat(response.getOpprettetAv().getBrukerId()).isEqualTo(bruker.getBrukerId());
//                    assertThat(response.getSistEndretAv().getBrukerId()).isEqualTo(bruker.getBrukerId());
                });

    }

    @Test
    @DisplayName("Kjører en bestilling gjennom TestGruppeController")
    void createBestilling()
            throws Exception {

        var bruker = super.createBruker();
//        when(getUserInfo.call())
//                .thenReturn(Optional.of(new UserInfo(bruker.getBrukerId(), "", "", bruker.getBrukernavn())));

        when(miljoerConsumer.getMiljoer())
                .thenReturn(Mono.just(List.of("q1", "q2", "q4", "qx")));

        Testgruppe testgruppe = Testgruppe
                .builder()
                .navn("gruppe")
                .hensikt("hensikt")
//                .opprettetAv(bruker)
                .datoEndret(LocalDate.now())
//                .sistEndretAv(bruker)
                .build();
        testgruppe = gruppeRepository.save(testgruppe);

        var bestilling = RsDollyBestillingRequest
                .builder()
                .environments(Set.of("q1", "q2"))
                .aareg(List.of(
                        RsAareg
                                .builder()
                                .arbeidsforholdstype("ordinaertArbeidsforhold")
                                .ansettelsesPeriode(
                                        RsAnsettelsesPeriode
                                                .builder()
                                                .fom(LocalDateTime.now())
                                                .build())
                                .arbeidsavtale(
                                        RsArbeidsavtale
                                                .builder()
                                                .arbeidstidsordning("ikkeSkift")
                                                .avtaltArbeidstimerPerUke(37.5)
                                                .stillingsprosent(100.0)
                                                .yrke("2521106")
                                                .ansettelsesform("fast")
                                                .build())
                                .arbeidsgiver(
                                        RsOrganisasjon
                                                .builder()
                                                .aktoertype("ORG")
                                                .orgnummer("896929119")
                                                .build())
                                .build()))
                .pdldata(
                        PdlPersondata
                                .builder()
                                .opprettNyPerson(
                                        PdlPersondata.PdlPerson
                                                .builder()
                                                .identtype(Identtype.FNR)
                                                .build())
                                .build())
                .inntektsmelding(
                        RsInntektsmelding
                                .builder()
                                .inntekter(List.of(
                                        RsInntektsmelding.Inntektsmelding
                                                .builder()
                                                .aarsakTilInnsending(AarsakTilInnsendingType.NY)
                                                .arbeidsforhold(
                                                        RsInntektsmelding.RsArbeidsforhold
                                                                .builder()
                                                                .beregnetInntekt(
                                                                        RsInntektsmelding.RsAltinnInntekt
                                                                                .builder()
                                                                                .beloep(1.0)
                                                                                .build())
                                                                .build())
                                                .arbeidsgiver(
                                                        RsInntektsmelding.RsArbeidsgiver
                                                                .builder()
                                                                .virksomhetsnummer("896929119")
                                                                .build())
                                                .avsendersystem(
                                                        RsInntektsmelding.RsAvsendersystem
                                                                .builder()
                                                                .innsendingstidspunkt(LocalDateTime.now())
                                                                .build())
                                                .ytelse(YtelseType.PLEIEPENGER_BARN)
                                                .build()))
                                .joarkMetadata(
                                        RsInntektsmelding.JoarkMetadata
                                                .builder()
                                                .tema("OMS")
                                                .build())
                                .build())
                .build();

        mockMvc.perform(
                        post("/api/v1/gruppe/{gruppeId}/bestilling", testgruppe.getId())
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bestilling)))
                .andExpect(status().isCreated());

        var lagredeBestillinger = bestillingRepository.findByGruppenavnContaining("gruppe");
        assertThat(lagredeBestillinger).isNotNull();
//        assertThat(lagredeBestillinger).hasSize(1);
//        var lagretBestilling = bestillingRepository
//                .findById(lagredeBestillinger.getFirst().getid())
//                .orElseThrow(() -> new NotFoundException("Finner ikke bestilling"));
//        assertThat(lagretBestilling).isNotNull();
//        assertThat(lagretBestilling.getMiljoer()).isEqualTo("q1,q2");

    }

}
