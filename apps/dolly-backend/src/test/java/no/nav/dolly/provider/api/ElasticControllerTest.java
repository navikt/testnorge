package no.nav.dolly.provider.api;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles({"test", "integrationtest"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ElasticControllerTest {

    private static final String BASE_URL = "/api/v1/elastic";
    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String IDENT3 = "33333333333";
    private static final String IDENT4 = "44444444444";
    private static final String IDENT5 = "55555555555";
    private static final String IDENT6 = "66666666666";

    @Autowired
    private BestillingElasticRepository bestillingElasticRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setup() {

        bestillingElasticRepository.saveAll(getTestBestillinger());
    }

    @AfterEach
    void teardown() {

        bestillingElasticRepository.deleteAll();
    }

    @Test
    void getAllSearchHits_OK() throws Exception {

        mockMvc
                .perform(post(BASE_URL + "/search-hits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHits").value(2L));
    }

    @Test
    void getSpecificSearchHit_OK() throws Exception {

        mockMvc
                .perform(get(BASE_URL + "/search-hits/ident/{ident}", IDENT1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHits").value(1L))
                .andExpect(jsonPath("$.searchHits[*].content.identer[*]", hasSize(3)))
                .andExpect(jsonPath("$.searchHits[*].content.identer[*]", hasItem(IDENT1)));
    }

    @Test
    void getSpecificSearchHit_NotFound() throws Exception {

        mockMvc
                .perform(get(BASE_URL + "/search-hits/ident/{ident}", IDENT6)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHits").value(0L));
    }

    @Test
    void getSearchResponseFromSpecifQuery_OK() throws Exception {

        mockMvc
                .perform(get(BASE_URL + "/identer")
                        .queryParam("typer", "BANKKONTO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHits").value(1L))
                .andExpect(jsonPath("$.identer[*]", hasSize(3)))
                .andExpect(jsonPath("$.identer[*]", hasItems(IDENT1, IDENT2, IDENT3)));
    }

    @Test
    void getSearchResponseFromSpecifQuery2_OK() throws Exception {

        mockMvc
                .perform(post(BASE_URL + "/identer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typer\":[\"AAREG\"]}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHits").value(1L))
                .andExpect(jsonPath("$.identer[*]", hasSize(2)))
                .andExpect(jsonPath("$.identer[*]", hasItems(IDENT4, IDENT5)));
    }

    @Test
    void getBestillingFromQuery_OK() throws Exception {

        mockMvc
                .perform(get(BASE_URL + "/bestilling/id/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pdldata.opprettNyPerson.syntetisk").value(true))
                .andExpect(jsonPath("$.bankkonto.utenlandskBankkonto.tilfeldigKontonummer").value(true))
                .andExpect(jsonPath("$.identer[*]", hasSize(3)))
                .andExpect(jsonPath("$.identer[*]", hasItems(IDENT1, IDENT2, IDENT3)));
    }

    @Test
    void deleteBestilling_OK() throws Exception {

        mockMvc
                .perform(delete(BASE_URL + "/bestilling/id/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        var bestilling = bestillingElasticRepository.findById(2L);
        assertThat(bestilling.isPresent(), is(false));
    }

    @Test
    void deleteAlleBestilling_OK() throws Exception {

        mockMvc
                .perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());

        var bestillinger = (PageImpl<ElasticBestilling>) bestillingElasticRepository.findAll();
        assertThat(bestillinger.getTotalElements(), is(equalTo(0L)));
    }

    private List<ElasticBestilling> getTestBestillinger() {

        return List.of(
                ElasticBestilling.builder()
                        .id(1L)
                        .pdldata(PdlPersondata.builder()
                                .opprettNyPerson(PdlPersondata.PdlPerson.builder()
                                        .identtype(Identtype.FNR)
                                        .syntetisk(true)
                                        .build())
                                .build())
                        .bankkonto(BankkontoData.builder()
                                .utenlandskBankkonto(BankkontonrUtlandDTO.builder()
                                        .tilfeldigKontonummer(true)
                                        .build())
                                .build())
                        .identer(List.of(IDENT1, IDENT2, IDENT3))
                        .build(),
                ElasticBestilling.builder()
                        .id(2L)
                        .pdldata(PdlPersondata.builder()
                                .opprettNyPerson(PdlPersondata.PdlPerson.builder()
                                        .identtype(Identtype.FNR)
                                        .syntetisk(true)
                                        .build())
                                .build())
                        .aareg(List.of(RsAareg.builder()
                                .arbeidsforholdstype("forenkletOppgjoersordning")
                                .ansettelsesPeriode(RsAnsettelsesPeriode.builder()
                                        .fom(LocalDateTime.of(2003, 10, 20, 0, 0))
                                        .build())
                                .arbeidsavtale(RsArbeidsavtale.builder()
                                        .yrke("2521106")
                                        .build())
                                .arbeidsgiver(RsOrganisasjon.builder()
                                        .aktoertype("ORG")
                                        .orgnummer("896929119")
                                        .build())
                                .build()))
                        .identer(List.of(IDENT4, IDENT5))
                        .build());
    }
}