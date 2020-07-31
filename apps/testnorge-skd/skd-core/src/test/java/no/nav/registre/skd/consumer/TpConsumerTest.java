package no.nav.registre.skd.consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TpConsumerTest {

    @Autowired
    private TpConsumer tpConsumer;

    private final String miljoe = "t1";
    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private List<String> identer;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    }

    @Test
    public void shouldSendIdenterToTp() {
        stubTpConsumer();

        var response = tpConsumer.leggTilIdenterITp(identer, miljoe);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assert response.getBody() != null;
        assertThat(response.getBody().size(), is(1));
        assertThat(response.getBody().get(0), equalTo(fnr2));
    }

    private void stubTpConsumer() {
        stubFor(post(urlPathEqualTo("/tp/api/v1/orkestrering/opprettPersoner/" + miljoe))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("[\"" + fnr2 + "\"]")));
    }
}