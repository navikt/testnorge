package no.nav.registre.testnorge.synt.arbeidsforhold.provider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.statiskedataforvalter.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.dto.statiskedataforvalter.v1.OrganisasjonListeDTO;
import no.nav.registre.testnorge.libs.dto.synt.arbeidsforhold.v1.SyntArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.AnsettelsePeriodeDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.ArbeidsavtaleDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.ArbeidstakerDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.KodeverkDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.SyntDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
class SyntArbeidsforholdControllerIntegrationTest {
    private final String ORGNUMMER = "123456789";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @BeforeEach
    void setup() {
        stubOrganisajonser(
                OrganisasjonListeDTO
                        .builder()
                        .liste(Collections.singletonList(OrganisasjonDTO
                                .builder()
                                .kanHaArbeidsforhold(true)
                                .orgnr(ORGNUMMER)
                                .build())
                        ).build()
        );
    }

    @Test
    @SneakyThrows
    void should_create_arbeidesforhold() {
        LocalDate foedselsdato = LocalDate.now().minusYears(26);
        String ident = "12312012354";
        LocalDate fom = foedselsdato.plusYears(18);
        LocalDate tom = fom.plusYears(20);
        String yrke = "12345";
        String arbeidsforholdID = "12334532";

        var generated = no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.SyntArbeidsforholdDTO
                .builder()
                .ansettelsesPeriode(AnsettelsePeriodeDTO.builder().fom(fom).tom(tom).build())
                .arbeidsavtale(ArbeidsavtaleDTO.builder().stillingsprosent(100.0F).yrke(yrke).build())
                .arbeidsforholdID(arbeidsforholdID)
                .arbeidsforholdstype("ordinaertArbeidsforhold")
                .arbeidstaker(ArbeidstakerDTO.builder().ident(ident).build())
                .build();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/generate/aareg")
                .withResponseBody(Collections.singletonList(new SyntDTO(generated)))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/kodeverk/Yrker/koder/betydninger")
                .withResponseBody(createKodeverk(yrke))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/arbeidsforhold")
                .stubPost();

        var request = SyntArbeidsforholdDTO
                .builder()
                .foedselsdato(foedselsdato)
                .ident(ident)
                .build();

        mvc.perform(post("/api/v1/synt-arbeidsforhold")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated());

        JsonWiremockHelper
                .builder(objectMapper)
                .withRequestBody(ArbeidsforholdDTO
                        .builder()
                        .fom(fom)
                        .ident(ident)
                        .tom(tom)
                        .arbeidsforholdId(arbeidsforholdID)
                        .orgnummer(ORGNUMMER)
                        .stillingsprosent(100.0F)
                        .yrke(yrke)
                        .build())
                .verifyPost();
    }

    @Test
    @SneakyThrows
    void should_not_start_arbeidforhold_before_16_years_old_instead_start_at_18_years() {
        LocalDate foedselsdato = LocalDate.now().minusYears(26);
        String ident = "12312012354";
        LocalDate fom = foedselsdato.plusYears(15);
        LocalDate tom = fom.plusYears(20);
        String yrke = "12345";
        String arbeidsforholdID = "12334532";

        var generated = no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.SyntArbeidsforholdDTO
                .builder()
                .ansettelsesPeriode(AnsettelsePeriodeDTO.builder().fom(fom).tom(tom).build())
                .arbeidsavtale(ArbeidsavtaleDTO.builder().stillingsprosent(100.0F).yrke(yrke).build())
                .arbeidsforholdID(arbeidsforholdID)
                .arbeidsforholdstype("ordinaertArbeidsforhold")
                .arbeidstaker(ArbeidstakerDTO.builder().ident(ident).build())
                .build();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/generate/aareg")
                .withResponseBody(Collections.singletonList(new SyntDTO(generated)))
                .stubPost();


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/kodeverk/Yrker/koder/betydninger")
                .withResponseBody(createKodeverk(yrke))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/arbeidsforhold")
                .stubPost();

        var request = SyntArbeidsforholdDTO
                .builder()
                .foedselsdato(foedselsdato)
                .ident(ident)
                .build();

        mvc.perform(post("/api/v1/synt-arbeidsforhold")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated());

        JsonWiremockHelper
                .builder(objectMapper)
                .withRequestBody(ArbeidsforholdDTO
                        .builder()
                        .fom(foedselsdato.plusYears(18))
                        .ident(ident)
                        .tom(tom)
                        .arbeidsforholdId(arbeidsforholdID)
                        .orgnummer(ORGNUMMER)
                        .stillingsprosent(100.0F)
                        .yrke(yrke)
                        .build())
                .verifyPost();
    }


    @Test
    @SneakyThrows
    void should_change_invalid_yrke_with_valid_yrke_from_kodeverk() {
        LocalDate foedselsdato = LocalDate.now().minusYears(26);
        String ident = "12312012354";
        LocalDate fom = foedselsdato.plusYears(18);
        LocalDate tom = fom.plusYears(20);
        String invalidYrke = "12345";
        String validYrke = "54321";
        String arbeidsforholdID = "12334532";

        var generated = no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.SyntArbeidsforholdDTO
                .builder()
                .ansettelsesPeriode(AnsettelsePeriodeDTO.builder().fom(fom).tom(tom).build())
                .arbeidsavtale(ArbeidsavtaleDTO.builder().stillingsprosent(100.0F).yrke(invalidYrke).build())
                .arbeidsforholdID(arbeidsforholdID)
                .arbeidsforholdstype("ordinaertArbeidsforhold")
                .arbeidstaker(ArbeidstakerDTO.builder().ident(ident).build())
                .build();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/generate/aareg")
                .withResponseBody(Collections.singletonList(new SyntDTO(generated)))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/kodeverk/Yrker/koder/betydninger")
                .withResponseBody(createKodeverk(validYrke))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/v1/arbeidsforhold")
                .stubPost();

        var request = SyntArbeidsforholdDTO
                .builder()
                .foedselsdato(foedselsdato)
                .ident(ident)
                .build();

        mvc.perform(post("/api/v1/synt-arbeidsforhold")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated());

        JsonWiremockHelper
                .builder(objectMapper)
                .withRequestBody(ArbeidsforholdDTO
                        .builder()
                        .fom(fom)
                        .ident(ident)
                        .tom(tom)
                        .arbeidsforholdId(arbeidsforholdID)
                        .orgnummer(ORGNUMMER)
                        .stillingsprosent(100.0F)
                        .yrke(validYrke)
                        .build())
                .verifyPost();
    }


    @AfterEach
    void cleanup() {
        WireMock.reset();
    }

    @SneakyThrows
    private void stubOrganisajonser(OrganisasjonListeDTO organisasjonListeDTO) {
        JsonWiremockHelper.builder(objectMapper)
                .withUrlPathMatching("/api/v1/organisasjons")
                .withResponseBody(organisasjonListeDTO)
                .stubGet();
    }


    private KodeverkDTO createKodeverk(String... koder) {
        return new KodeverkDTO(Set.of(koder).stream().collect(Collectors.toMap(value -> value, value -> new Object[]{})));
    }


}