package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonSearch;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class DollySearchServiceConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenExchange;

    @Autowired
    private DollySearchServiceConsumer dollySearchServiceConsumer;

    private static final PersonSearch REQUEST = PersonSearch.builder()
            .kunLevende(true)
            .randomSeed(121323)
            .page(0)
            .pageSize(10)
            .alder(PersonSearch.AlderSearch.builder()
                    .fra(17)
                    .til(66)
                    .build())
            .build();


    @BeforeEach
    void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }


    @Test
    void shouldGetSearchResult() {
        stubPostPersonSearch();

        var response = dollySearchServiceConsumer.search(REQUEST);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getNumberOfItems()).isEqualTo(1);
        assertThat(response.getItems().get(0).getIdent()).isEqualTo("11866800000");
    }

    private void stubPostPersonSearch() {
        stubFor(post(urlPathMatching("(.*)/api/v1/legacy"))
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

        var response = dollySearchServiceConsumer.search(REQUEST);
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getNumberOfItems()).isZero();
    }

    private void stubPostEmptyPersonSearch() {
        stubFor(post(urlPathMatching("(.*)/api/v1/legacy"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withHeader("NUMBER_OF_ITEMS", "0")
                        .withBody("[]")
                )
        );
    }

    @Test
    void shouldHandleErrorResponse() {
        stubPdlPersonErrorResponse();
        var response = dollySearchServiceConsumer.search(REQUEST);
        assertThat(response).isNull();
    }

    private void stubPdlPersonErrorResponse() {
        stubFor(post(urlPathMatching("(.*)/api/v1/legacy"))
                .willReturn(aResponse().withStatus(500))
        );
    }

}
