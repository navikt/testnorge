package no.nav.dolly.integrasjonstest;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class ITestBase {

    @Autowired
    protected TestRestTemplate restTemplate;

//    @Value("${SRVDKIF_USERNAME}")
    private String serviceUsername = "temp1";

//    @Value("${SRVDKIF_PASSWORD}")
    private String servicePassword = "temp2";

    @BeforeEach
    void stubExternalServices() {
        stubTokenService();
    }

    protected void stubTokenService() {
        stubFor(get(urlEqualTo("/token?grant_type=client_credentials&scope=openid"))
                .withHeader("Accept", equalTo("application/json"))
                .withBasicAuth(serviceUsername, servicePassword)
                .willReturn(okJson("Yo, there is content here")));
    }

    protected void stubKrr(String personident, String epost, String mobil) {
        // todo stub tilsvarende der det trengs
    }

    protected String getJsonContentsAsString(String filename) throws IOException {
        File requestBodyFile = new ClassPathResource(filename).getFile();
        return new String(Files.readAllBytes(requestBodyFile.toPath()));
    }
}
