package no.nav.registre.bisys.consumer;

import no.nav.registre.bisys.consumer.credential.PersonSearchProperties;
import no.nav.registre.bisys.consumer.request.FoedselSearch;
import no.nav.registre.bisys.consumer.request.PersonSearchRequest;
import no.nav.registre.bisys.consumer.request.RelasjonSearch;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureNavClientCredential;
import no.nav.testnav.libs.servletsecurity.exchange.AzureAdTokenService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

import static no.nav.registre.bisys.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;

@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class PersonSearchConsumerTest {

//    private PersonSearchConsumer personSearchConsumer;
//    private MockWebServer mockWebServer;
//
//
//    @Before
//    public void setUp() throws IOException {
//        this.mockWebServer = new MockWebServer();
//        this.mockWebServer.start();
//        Dispatcher dispatcher = getDispatcher();
//        mockWebServer.setDispatcher(dispatcher);
//
//        var personSearchProperties = PersonSearchProperties.builder()
//                .cluster("dummy")
//                .namespace("dummy")
//                .name("person-search-service")
//                .url(mockWebServer.url("/person-search").toString())
//                .build();
//        var tokenExchange = new TokenExchange(new AzureAdTokenService(
//                null,
//                mockWebServer.url("/token").toString(),
//                new AzureNavClientCredential("dummy", "dummy")
//        ));
//
//        this.personSearchConsumer = new PersonSearchConsumer(
//                (PersonSearchProperties) personSearchProperties,
//                tokenExchange
//                );
//    }
//
//    @Test
//    public void shouldGetPersonResponse(){
//        var searchRequest = PersonSearchRequest.builder()
//                .page(1)
//                .pageSize(10)
//                .randomSeed("test")
//                .foedsel(FoedselSearch.builder()
//                        .fom(LocalDate.now().minusYears(20))
//                        .tom(LocalDate.now())
//                        .build())
//                .relasjoner(RelasjonSearch.builder()
//                        .mor(true)
//                        .far(true)
//                        .build())
//                .build();
//        var response = personSearchConsumer.search(searchRequest);
//
//        assertThat(response.getNumberOfItems()).isEqualTo(1);
//    }
//
//    private Dispatcher getDispatcher() {
//        return new Dispatcher() {
//            @Override
//            public MockResponse dispatch(RecordedRequest request) {
//                switch (request.getPath()) {
//                    case "/token/oauth2/v2.0/token":
//                        return new MockResponse().setResponseCode(200)
//                                .addHeader("Content-Type", "application/json; charset=utf-8")
//                                .setBody(new AccessToken("dummy").toString());
//                    case "/person-search/api/v1/person":
//                        return new MockResponse().setResponseCode(200)
//                                .addHeader("Content-Type", "application/json; charset=utf-8")
//                                .addHeader("NUMBER_OF_ITEMS", "1")
//                                .setBody(getResourceFileContent("files/searchResultOnePerson.json"));
//                }
//                return new MockResponse().setResponseCode(404);
//            }
//        };
//    }
//
//
//    @After
//    public void tearDown() throws IOException {
//        mockWebServer.shutdown();
//    }

}
