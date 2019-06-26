package no.nav.dolly.provider.api;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.provider.RestTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

class TeamControllerEndToEndTest extends RestTestBase {

    private static final String ENDPOINT_BASE_URI = "/api/v1/team";
    private static final ParameterizedTypeReference<List<RsTeam>> expectedResponseType = new ParameterizedTypeReference<List<RsTeam>>() {
    };

    @Test
    @DisplayName("Skal få 403 når AUTHORIZATION ikke sendes med")
    void shouldFailMissingHeader() {
        sendRequest()
                .withoutHeader(HttpHeaders.AUTHORIZATION)
                .to(HttpMethod.GET, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.FORBIDDEN, String.class);
    }

    @Test
    @DisplayName("Skal få tom liste uten navIdent")
    void shouldGetEmptyListWithoutNavIdent() {
        List<RsTeam> rsTeams = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI)
                .andExpectList(HttpStatus.OK, expectedResponseType);

        assertThat(rsTeams.size(), is(0));
    }
}
