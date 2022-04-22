package no.nav.identpool.providers.v1;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.providers.v1.support.IdentRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("POST /api/v1/finneshosskatt")
class FinnesHosSkattComponentTest extends ComponentTestbase {

    private static final String DNR = "50108000381";
    private static final String NYTT_DNR = "50058000393";
    private static final String FNR = "10108000398";

    @BeforeEach
    void populerDatabaseMedTestidenter() throws URISyntaxException {

        identRepository.deleteAll();
        identRepository.save(
                createIdentEntity(Identtype.FNR, DNR, Rekvireringsstatus.LEDIG, 10)
        );
    }

    @AfterEach
    void clearDatabase() {
        identRepository.deleteAll();
    }

    @Test
    void registrerFnrErIkkeEnDnr() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(FINNESHOSSKATT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IdentRequest(FNR))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void registrerFinnesISkdOgIdentpool() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(FINNESHOSSKATT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IdentRequest(DNR))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertTrue(identRepository.findByPersonidentifikator(DNR).isFinnesHosSkatt());
        assertThat(identRepository.findByPersonidentifikator(DNR).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
    }

    @Test
    void registrerFinnesISkdMenIkkeIIdentpool() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(FINNESHOSSKATT_V1_BASEURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IdentRequest(NYTT_DNR))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertTrue(identRepository.findByPersonidentifikator(NYTT_DNR).isFinnesHosSkatt());
        assertThat(identRepository.findByPersonidentifikator(NYTT_DNR).getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
    }
}