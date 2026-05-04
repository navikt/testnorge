package no.nav.brregstub.endpoint.rs.v1;

import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.UnderstatusKode;
import no.nav.dolly.libs.test.DollyServletSpringBootTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollyServletSpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET kode/roller returnerer 200 og rollene")
    void skalHenteRollelist() throws Exception {
        mockMvc.perform(get("/api/v1/kode/roller")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$." + RolleKode.SAM.name()).value(RolleKode.SAM.getBeskrivelse()));
    }

    @Test
    @DisplayName("GET kode/understatus returnerer 200 og tilgjenglige understatuser")
    void skalHenteUnderstatuser() throws Exception {
        mockMvc.perform(get("/api/v1/kode/understatus")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['1']").value(UnderstatusKode.understatusKoder.get(1)));
    }
}
