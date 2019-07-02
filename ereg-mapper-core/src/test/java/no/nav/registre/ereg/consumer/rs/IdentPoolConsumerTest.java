package no.nav.registre.ereg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {IdentPoolConsumer.class, RestTemplate.class})
public class IdentPoolConsumerTest {

    private Integer count = 1;

    @Autowired
    private IdentPoolConsumer identPoolConsumer;

    @Test
    public void getFakeNames() {
        stubIdentPool_OK();
        List<String> fakeNames = identPoolConsumer.getFakeNames(count);
        assertEquals(count, (Integer) fakeNames.size());
        assertEquals("SNILL GUVERNANTE", fakeNames.get(0));
    }

    @Test(expected = HttpServerErrorException.class)
    public void getFakeNames_BadRequest() {
        stubIdentPool_BadRequest();
        identPoolConsumer.getFakeNames(count);
        verify(getRequestedFor(urlEqualTo("/v1/fiktive-navn/tilfeldig?antall=" + count.toString())));
    }

    public void stubIdentPool_BadRequest() {
        stubFor(get(urlEqualTo("/v1/fiktive-navn/tilfeldig?antall=" + count.toString())).willReturn(badRequest()));
    }

    public void stubIdentPool_OK() {
        stubFor(get(urlEqualTo("/v1/fiktive-navn/tilfeldig?antall=" + count.toString())).willReturn(okJson(
                "[\n" +
                        "  {\n" +
                        "    \"fornavn\": \"SNILL\",\n" +
                        "    \"etternavn\": \"GUVERNANTE\"\n" +
                        "  }\n" +
                        "]"))
        );
    }
}