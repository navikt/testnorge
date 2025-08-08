package no.nav.dolly.provider.api;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GET /api/v1/gruppe")
class TestgruppeControllerGetTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrukerService brukerService;

    @BeforeEach
    void setup() {

//        when(brukerService.fetchOrCreateBruker()).thenReturn(new Bruker());
    }

    @Disabled
    @Test
    @DisplayName("Returnerer testgrupper tilknyttet til bruker-ID gjennom favoritter")
    void shouldGetTestgrupperWithNavIdent()
            throws Exception {

        var bruker = super.createBruker();
        when(brukerService.fetchOrCreateBruker(any())).thenReturn(bruker);
        when(brukerService.fetchBruker(any())).thenReturn(bruker);

//        var testgruppe1 = super.createTestgruppe("Gruppen er ikke en favoritt", bruker);
//        var testgruppe2 = super.createTestgruppe("Gruppen er en favoritt", bruker);
//        bruker.setFavoritter(Set.of(testgruppe2));
//        bruker = super.saveBruker(bruker);

//        mockMvc
//                .perform(get("/api/v1/gruppe?brukerId={brukerId}", bruker.getBrukerId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.antallElementer").value(2))
//                .andExpect(jsonPath("$.contents.length()").value(2))
//                .andExpect(jsonPath("$.favoritter.length()").value(1))
//                .andExpect(jsonPath("$.favoritter..navn").value(testgruppe2.getNavn()));

    }

    @Test
    @DisplayName("Returnerer HTTP 404 med korrekt feilmelding i body")
    void shouldFail404NotFound()
            throws Exception {

        var id = new Random().nextLong();
        mockMvc
                .perform(get("/api/v1/gruppe/{gruppeId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Gruppe med id " + id + " ble ikke funnet."));

    }

    @Test
    @DisplayName("Returnerer Testgruppe")
    void shouldReturnTestgruppe()
            throws Exception {

//        var testgruppe = super.createTestgruppe("Testgruppe", super.createBruker());
//        for (var i = 1; i < 11; i++) {
//            super.createTestident("Ident " + i, testgruppe);
//        }
//
//        mockMvc
//                .perform(get("/api/v1/gruppe/{gruppeId}", testgruppe.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.navn").value("Testgruppe"))
//                .andExpect(jsonPath("$.antallIdenter").value(10));

    }

}

