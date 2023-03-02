package no.nav.dolly.provider.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.testnav.libs.securitycore.domain.UserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /api/v1/gruppe")
class TestgruppeControllerPostTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserInfo getUserInfo;

    @Test
    @DisplayName("Returnerer opprettet Testgruppe med innlogget bruker som eier")
    void createTestgruppeAndSetCurrentUserAsOwner()
            throws Exception {

        var bruker = super.createBruker();
        when(getUserInfo.call())
                .thenReturn(Optional.of(new UserInfo(bruker.getBrukerId(), "", "", bruker.getBrukernavn())));

        var request = RsOpprettEndreTestgruppe
                .builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();
        mockMvc
                .perform(
                        post("/api/v1/gruppe")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    var response = objectMapper.readValue(result.getResponse().getContentAsString(), RsTestgruppeMedBestillingId.class);
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getNavn()).isEqualTo("mingruppe");
                    assertThat(response.getHensikt()).isEqualTo("hensikt");
                    assertThat(response.getOpprettetAv().getBrukerId()).isEqualTo(bruker.getBrukerId());
                    assertThat(response.getSistEndretAv().getBrukerId()).isEqualTo(bruker.getBrukerId());
                });

    }
}
