package no.nav.brregstub.endpoint.rs.v2;

import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.dolly.libs.test.DollyServletSpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollyServletSpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RolleoversiktControllerTest {

    public static final String API_V_2_ROLLEUTSKRIFT = "/api/v2/rolleoversikt";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolleoversiktRepository rolleoversiktRepository;

    @MockitoBean
    private HentRolleRepository rolleRepository;

    @Test
    @DisplayName("GET rolleoversikt returnerer 404 hvis ikke eksisterer")
    void skalKasteNotFoundHvisRolleIkkeEksister() throws Exception {
        Mockito.when(rolleoversiktRepository.findByIdent("eksister ikke")).thenReturn(Optional.empty());

        mockMvc.perform(get(API_V_2_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "eksister ikke")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 400 hvis input mangler")
    void skalKasteBadRequestHvisInputMangler() throws Exception {
        mockMvc.perform(get(API_V_2_ROLLEUTSKRIFT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 200 hvis eksisterer")
    void skalHenteRolleutskriftFraDatabase() throws Exception {
        var fnr = "03030303030";
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent(fnr);
        nyRolle.setJson("{\"fnr\":\"" + fnr + "\"}");
        Mockito.when(rolleoversiktRepository.findByIdent(nyRolle.getIdent())).thenReturn(Optional.of(nyRolle));

        mockMvc.perform(get(API_V_2_ROLLEUTSKRIFT)
                        .header("Nav-Personident", fnr)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fnr").value(fnr));
    }

    @Test
    @DisplayName("DELETE rolleoversikt skal slette rolleoversikt")
    void skalSletteRolleutskrift() throws Exception {
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent("slettes");
        nyRolle.setJson("{\"fnr\":\"slettes\"}");
        Mockito.when(rolleoversiktRepository.findByIdent(nyRolle.getIdent())).thenReturn(Optional.of(nyRolle));
        Mockito.doNothing().when(rolleoversiktRepository).delete(any(Rolleoversikt.class));

        mockMvc.perform(delete(API_V_2_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "slettes"))
                .andExpect(status().isOk());

        Mockito.when(rolleoversiktRepository.findByIdent(nyRolle.getIdent())).thenReturn(Optional.empty());

        mockMvc.perform(get(API_V_2_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "slettes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE rolleoversikt returnerer 400 hvis input mangler")
    void deleteSkalKasteBadRequestHvisInputMangler() throws Exception {
        mockMvc.perform(delete(API_V_2_ROLLEUTSKRIFT))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST rolleoversikt skal opprette ny rolleoversikt")
    void skalLagreRequestIDatabase() throws Exception {
        var fnr = "01010101010";
        var requestBody = "{\"fnr\":\"" + fnr + "\",\"hovedstatus\":1,\"navn\":{\"navn1\":\"Navn\"},\"adresse\":{\"adresse1\":\"Adresse 1\",\"landKode\":\"NO\"},\"enheter\":[{\"orgNr\":99112345,\"rolle\":\"DELT\",\"registreringsdato\":\"2020-01-01\",\"personRolle\":[{\"egenskap\":\"DELTAGER\",\"fratraadt\":false}]}]}";

        Mockito.when(rolleoversiktRepository.save(any(Rolleoversikt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post(API_V_2_ROLLEUTSKRIFT)
                        .header("Nav-Personident", fnr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fnr").value(fnr));
    }

    @Test
    @DisplayName("POST rolleoversikt skal returnere bad request ved mangle input")
    void skalReturnereBadRequestVedPost() throws Exception {
        var fnr = "01010101010";

        mockMvc.perform(post(API_V_2_ROLLEUTSKRIFT)
                        .header("Nav-Personident", fnr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
