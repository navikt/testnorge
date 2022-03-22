package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PersonSearchProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search.AlderSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search.PersonSearchRequest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class PersonSearchConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private PersonSearchConsumer personSearchConsumer;

    private static final PersonSearchRequest REQUEST = PersonSearchRequest.builder()
            .randomSeed("seed")
            .page(1)
            .pageSize(10)
            .alder(AlderSearch.builder()
                    .fra((short) 17)
                    .til((short) 66)
                    .build())
            .build();


    @BeforeEach
    public void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(PersonSearchProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }


    @Test
    void shouldGetSearchResult() {
        stubPostPersonSearch();

        var response = personSearchConsumer.search(REQUEST);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getNumberOfItems()).isEqualTo(1);
        assertThat(response.getItems().get(0).getIdent()).isEqualTo("11866800000");
    }

    private void stubPostPersonSearch() {
        stubFor(post(urlPathMatching("(.*)/search/api/v1/person"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withHeader("NUMBER_OF_ITEMS", "1")
                        .withBody(getResourceFileContent("files/search/single_search_response.json"))
                )
        );
    }

    @Test
    void shouldGetEmptySearchResult() {
        stubPostEmptyPersonSearch();

        var response = personSearchConsumer.search(REQUEST);
        assertThat(response.getItems()).hasSize(0);
        assertThat(response.getNumberOfItems()).isEqualTo(0);
    }

    private void stubPostEmptyPersonSearch() {
        stubFor(post(urlPathMatching("(.*)/search/api/v1/person"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withHeader("NUMBER_OF_ITEMS", "0")
                        .withBody("[]")
                )
        );
    }

    @Test
    void shouldHandleErrorResponse(){
        stubPdlPersonErrorResponse();
        var response = personSearchConsumer.search(REQUEST);
        assertThat(response).isNull();
    }

    private void stubPdlPersonErrorResponse() {
        stubFor(post(urlPathMatching("(.*)/search/api/v1/person"))
                .willReturn(aResponse().withStatus(500))
        );
    }

}
