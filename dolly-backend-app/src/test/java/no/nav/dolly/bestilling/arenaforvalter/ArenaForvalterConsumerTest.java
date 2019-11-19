package no.nav.dolly.bestilling.arenaforvalter;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(SpringRunner.class)
@RestClientTest(ArenaForvalterConsumer.class)
public class ArenaForvalterConsumerTest {

    private static final String STANDARD_PRINCIPAL = "brukernavn";
    private static final String STANDARD_IDTOKEN = "idtoken";

    private static final String IDENT = "12423353";
    private static final String ENV = "u2";

    private MockRestServiceServer server;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private ProvidersProps providersProps;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @BeforeClass
    public static void beforeClass() {

        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(STANDARD_PRINCIPAL, null, STANDARD_IDTOKEN, null)
        );
    }

    @Before
    public void setup() {

        when(providersProps.getArenaForvalter()).thenReturn(ProvidersProps.ArenaForvalter.builder().url("baseUrl").build());

        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void deleteIdent() {

        server.expect(requestTo("baseUrl/api/v1/bruker?miljoe=u2&personident=12423353"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));

        arenaForvalterConsumer.deleteIdent(IDENT, ENV);
        verify(providersProps).getArenaForvalter();
    }

    @Test
    public void postArenadata() {

        server.expect(requestTo("baseUrl/api/v1/bruker"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        arenaForvalterConsumer.postArenadata(ArenaNyeBrukere.builder()
                .nyeBrukere(singletonList(ArenaNyBruker.builder().personident(IDENT).build()))
                .build());

        verify(providersProps).getArenaForvalter();
    }

    @Test
    public void getIdent_OK() {

        server.expect(requestTo("baseUrl/api/v1/bruker?filter-personident=12423353"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        arenaForvalterConsumer.getIdent(IDENT);

        verify(providersProps).getArenaForvalter();
    }

    @Test
    public void getEnvironments() {

        server.expect(requestTo("baseUrl/api/v1/miljoe"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        arenaForvalterConsumer.getEnvironments();

        verify(providersProps).getArenaForvalter();
    }
}