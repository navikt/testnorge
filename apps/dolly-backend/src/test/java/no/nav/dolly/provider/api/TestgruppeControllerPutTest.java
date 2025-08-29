package no.nav.dolly.provider.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.testnav.libs.securitycore.domain.UserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("PUT /api/v1/gruppe")
class TestgruppeControllerPutTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetUserInfo getUserInfo;

    @Test
    @DisplayName("Returnerer HTTP 404 med korrekt feilmelding hvis Testgruppe ikke finnes")
    void shouldFail404WhenTestgruppeDontExist()
            throws Exception {

        var request = RsOpprettEndreTestgruppe
                .builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();
        var id = new Random().nextLong();
        mockMvc
                .perform(
                        put("/api/v1/gruppe/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Gruppe med id " + id + " ble ikke funnet."));

    }

    @Test
    @DisplayName("Oppdaterer informasjon om Testgruppe")
    void updateTestgruppe()
            throws Exception {

        var bruker = super.createBruker();
        var testgruppe = super.createTestgruppe("Opprinnelig gruppe", bruker);
        var request = RsOpprettEndreTestgruppe
                .builder()
                .navn("Endret gruppe")
                .hensikt("Endret hensikt")
                .build();
        when(getUserInfo.call())
                .thenReturn(Optional.of(new UserInfo(bruker.getBrukerId(), "", "", bruker.getBrukernavn())));
        mockMvc
                .perform(
                        put("/api/v1/gruppe/{id}", testgruppe.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    var response = objectMapper.readValue(result.getResponse().getContentAsString(), RsTestgruppeMedBestillingId.class);
                    assertThat(response.getId()).isEqualTo(testgruppe.getId());
                    assertThat(response.getNavn()).isEqualTo("Endret gruppe");
                    assertThat(response.getHensikt()).isEqualTo("Endret hensikt");
                });

    }

}
