package no.nav.identpool.providers.v1;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.dto.TpsStatusDTO;
import no.nav.identpool.util.PersonidentUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

class IdentpoolControllerComponentTest extends ComponentTestbase {

    private static final String API_V1_IDENT_IBRUK = IDENT_V1_BASEURL + "/bruk";
    private static final String API_V1_IDENT_LEDIG = IDENT_V1_BASEURL + "/ledig";

    private static final String FNR_LEDIG = "10108000398";
    private static final String DNR_LEDIG = "50108000381";
    private static final String FNR_IBRUK = "11108000327";
    private static final String NYTT_FNR_LEDIG = "20018049946";

    @BeforeEach
    void populerDatabaseMedTestidenter() {

        identRepository.deleteAll();
        identRepository.saveAll(Arrays.asList(
                createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10),
                createIdentEntity(Identtype.DNR, DNR_LEDIG, Rekvireringsstatus.LEDIG, 20),
                createIdentEntity(Identtype.FNR, FNR_IBRUK, Rekvireringsstatus.I_BRUK, 11),
                createIdentEntity(Identtype.DNR, "12108000366", Rekvireringsstatus.I_BRUK, 12)
        ));
    }

    @AfterEach
    void clearDatabase() {
        identRepository.deleteAll();
    }

    @Test
    void hentLedigFnr() throws Exception {

        String request = "{\"antall\":\"1\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\" }";

        var result = mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        var identer = (List<String>) objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(PersonidentUtil.getIdentType(identer.get(0)), is(Identtype.FNR));
        assertThat(identer, hasSize(1));
    }

    @Test
    void hentLedigDnr() throws Exception {

        String request = "{\"antall\":\"2\", \"identtype\":\"DNR\",\"foedtEtter\":\"1900-01-01\" }";

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Set.of(
                        TpsStatusDTO.builder().ident("64038000169").build(),
                        TpsStatusDTO.builder().ident("53061600147").build()));

        var result = mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        var identer = (List<String>) objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(PersonidentUtil.getIdentType(identer.get(0)), is(Identtype.DNR));
        assertThat(PersonidentUtil.getIdentType(identer.get(1)), is(Identtype.DNR));
        assertThat(identer, hasSize(2));
    }

    @Test
    void hentLedigIdent() throws Exception {

        String request = "{\"antall\":\"3\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\",\"foedtFoer\":\"1950-01-01\"}";

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Set.of(
                TpsStatusDTO.builder().ident("15103300123").build(),
                TpsStatusDTO.builder().ident("16022400197").build(),
                TpsStatusDTO.builder().ident("09021000121").build()));

        var result = mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        var identer = (List<String>) objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(identer, hasSize(3));

        long countDb = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
                LocalDate.of(1900, 1, 1),
                LocalDate.of(1950, 1, 1),
                Identtype.FNR,
                Rekvireringsstatus.I_BRUK,
                false);

        assertThat(countDb, is(3L));
    }

    @Test
    void hentForMangeIdenterSomIkkeFinnesIDatabasen() throws Exception {

        String request = "{\"antall\":\"200\", \"foedtEtter\":\"1900-01-01\"}";

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Set.of(
                TpsStatusDTO.builder().ident("15103300123").build()));

        mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void skalFeileNaarUgyldigIdenttypeBrukes() throws Exception {

        String request = "{\"antall\":\"1\", \"identtype\":\"buksestoerrelse\" }";

        mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void markerIBrukPaaIdentAlleredeIBruk() throws Exception {

        String request = "{\"personidentifikator\":\"" + FNR_IBRUK + "\", \"bruker\":\"TesterMcTestFace\" }";

        mockMvc.perform(MockMvcRequestBuilders.post(API_V1_IDENT_IBRUK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn();
    }

    @Test
    void markerEksisterendeLedigIdentIBruk() throws Exception {

        assertThat(identRepository.findTopByPersonidentifikator(FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG));

        String request = "{\"personidentifikator\":\"" + FNR_LEDIG + "\", \"bruker\":\"TesterMcTestFace\" }";

        mockMvc.perform(MockMvcRequestBuilders.post(API_V1_IDENT_IBRUK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(identRepository.findTopByPersonidentifikator(FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
    }

    @Test
    void markerNyLedigIdentIBruk() throws Exception {

        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG), is(nullValue()));

        String request = "{\"personidentifikator\":\"" + NYTT_FNR_LEDIG + "\", \"bruker\":\"TesterMcTestFace\" }";

        mockMvc.perform(MockMvcRequestBuilders.post(API_V1_IDENT_IBRUK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
        assertThat(identRepository.findTopByPersonidentifikator(NYTT_FNR_LEDIG).getIdenttype(), is(Identtype.FNR));
    }

    @Test
    void sjekkOmLedigIdentErLedig() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.get(API_V1_IDENT_LEDIG)
                        .header("personidentifikator", FNR_LEDIG))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(Boolean.parseBoolean(result.getResponse().getContentAsString()), is(true));
    }

    @Test
    void sjekkOmUledigIdentErLedig() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.get(API_V1_IDENT_LEDIG)
                        .header("personidentifikator", FNR_IBRUK))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(Boolean.parseBoolean(result.getResponse().getContentAsString()), is(false));
    }

    @Test
    void eksistererIkkeIDbOgLedigITps() throws Exception {

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Set.of(
                        TpsStatusDTO.builder().ident(NYTT_FNR_LEDIG).build()));

        var result = mockMvc.perform(MockMvcRequestBuilders.get(API_V1_IDENT_LEDIG)
                        .header("personidentifikator", NYTT_FNR_LEDIG))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(Boolean.parseBoolean(result.getResponse().getContentAsString()), is(true));
    }

    @Test
    void lesIdenterTest() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.get(IDENT_V1_BASEURL)
                        .header("personidentifikator", FNR_LEDIG))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Ident expected = createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10);
        assertThat(objectMapper.readValue(result.getResponse().getContentAsString(), Ident.class), is(expected));
    }
}
