package no.nav.brregstub.endpoint.rs.v1;

import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.repository.HentRolleRepository;
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
public class HentRollerControllerTest {

    public static final String API_V_1_ROLLER = "/api/v1/hentrolle";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HentRolleRepository repository;

    @Test
    @DisplayName("GET rolle returnerer 404 hvis ikke eksisterer")
    void skalKasteNotFoundHvisRolleIkkeEksister() throws Exception {
        mockMvc.perform(get(API_V_1_ROLLER + "/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET rolle returnerer 200 hvis eksisterer")
    void skalHenteRolleutskriftFraDatabase() throws Exception {
        var nyRolle = new HentRolle();
        nyRolle.setOrgnr(1);
        nyRolle.setJson("{\"orgnr\": 1}");
        Mockito.when(repository.findByOrgnr(1)).thenReturn(Optional.of(nyRolle));

        mockMvc.perform(get(API_V_1_ROLLER + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgnr").value(1));
    }

    @Test
    @DisplayName("DELETE rolle skal slettes fra database")
    void skalSletteRolleutskrift() throws Exception {
        var rolleSomSkalSlettes = new HentRolle();
        rolleSomSkalSlettes.setOrgnr(3);
        rolleSomSkalSlettes.setJson("{}");
        Mockito.when(repository.findByOrgnr(3)).thenReturn(Optional.of(rolleSomSkalSlettes));

        mockMvc.perform(delete(API_V_1_ROLLER + "/3"))
                .andExpect(status().isOk());

        Mockito.when(repository.findByOrgnr(3)).thenReturn(Optional.empty());

        mockMvc.perform(get(API_V_1_ROLLER + "/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST rolle skal opprette ny databaseinnslag")
    void skalLagreRequestIDatabase() throws Exception {
        var requestBody = "{\"orgnr\":4,\"registreringsdato\":\"2020-01-01\"}";

        Mockito.when(repository.save(any(HentRolle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post(API_V_1_ROLLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.path").value("/api/v1/hentrolle/4"));
    }

    @Test
    @DisplayName("POST rolle returnere bad request ved manglende feilt")
    void skalReturnereBadRequestVedValideringsFeil() throws Exception {
        mockMvc.perform(post(API_V_1_ROLLER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
