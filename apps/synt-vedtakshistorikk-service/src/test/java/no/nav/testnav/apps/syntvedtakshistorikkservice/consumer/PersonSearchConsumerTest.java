package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.libs.DollySpringBootTest;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.AlderSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
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

import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class PersonSearchConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenExchange;

    @Autowired
    private PersonSearchConsumer personSearchConsumer;

    private static final PersonSearch REQUEST = PersonSearch.builder()
            .tag("TESTNORGE")
            .excludeTags(Arrays.asList("DOLLY", "ARENASYNT"))
            .kunLevende(true)
            .randomSeed("seed")
            .page(1)
            .pageSize(10)
            .alder(AlderSearch.builder()
                    .fra((short) 17)
                    .til((short) 66)
                    .build())
            .build();


    @BeforeEach
    void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
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
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getNumberOfItems()).isZero();
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
    void shouldHandleErrorResponse() {
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
