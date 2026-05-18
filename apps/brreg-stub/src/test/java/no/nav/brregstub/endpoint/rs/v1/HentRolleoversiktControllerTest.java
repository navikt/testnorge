package no.nav.brregstub.endpoint.rs.v1;

import no.nav.brregstub.database.domene.Rolleoversikt;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollyServletSpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HentRolleoversiktControllerTest {

    public static final String API_V_1_ROLLEUTSKRIFT = "/api/v1/rolleoversikt";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolleoversiktRepository repository;

    @Test
    @DisplayName("GET rolleoversikt returnerer 404 hvis ikke eksisterer")
    void shouldReturnNotFoundWhenMissing() throws Exception {
        Mockito.when(repository.findByIdent("eksister ikke")).thenReturn(Optional.empty());

        mockMvc.perform(get(API_V_1_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "eksister ikke")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 400 hvis input mangler")
    void shouldReturnBadRequestWhenHeaderMissing() throws Exception {
        mockMvc.perform(get(API_V_1_ROLLEUTSKRIFT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET rolleoversikt returnerer 200 hvis eksisterer")
    void shouldReturnOkWhenExists() throws Exception {
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent("ident");
        nyRolle.setJson("{\"fnr\":\"ident\"}");
        Mockito.when(repository.findByIdent("ident")).thenReturn(Optional.of(nyRolle));

        mockMvc.perform(get(API_V_1_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "ident")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fnr").value("ident"));
    }

    @Test
    @DisplayName("DELETE rolleoversikt skal slette rolleoversikt")
    void shouldDelete() throws Exception {
        var nyRolle = new Rolleoversikt();
        nyRolle.setIdent("slettes");
        nyRolle.setJson("{\"fnr\":\"slettes\"}");

        Mockito.when(repository.findByIdent("slettes")).thenReturn(Optional.of(nyRolle));
        Mockito.doNothing().when(repository).delete(any(Rolleoversikt.class));

        mockMvc.perform(delete(API_V_1_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "slettes"))
                .andExpect(status().isOk());

        Mockito.when(repository.findByIdent("slettes")).thenReturn(Optional.empty());

        mockMvc.perform(get(API_V_1_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "slettes"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE rolleoversikt returnerer 400 hvis input mangler")
    void deleteShouldReturnBadRequestWhenHeaderMissing() throws Exception {
        mockMvc.perform(delete(API_V_1_ROLLEUTSKRIFT))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST rolleoversikt skal opprette ny rolleoversikt")
    void shouldCreate() throws Exception {
        var requestBody = "{\"fnr\":\"ny\",\"hovedstatus\":1,\"navn\":{\"navn1\":\"Navn\"},\"adresse\":{\"adresse1\":\"Adresse 1\",\"landKode\":\"NO\"},\"enheter\":[{\"orgNr\":99887765,\"rollebeskrivelse\":\"beskrivelse\",\"registreringsdato\":\"2020-01-01\"}]}";

        Mockito.when(repository.save(any(Rolleoversikt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post(API_V_1_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "ny")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fnr").value("ny"));
    }

    @Test
    @DisplayName("POST rolleoversikt skal returnere bad request ved mangle input")
    void postShouldReturnBadRequestWhenInvalid() throws Exception {
        mockMvc.perform(post(API_V_1_ROLLEUTSKRIFT)
                        .header("Nav-Personident", "ny")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
