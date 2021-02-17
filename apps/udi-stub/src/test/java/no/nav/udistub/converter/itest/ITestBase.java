package no.nav.udistub.converter.itest;

import no.nav.udistub.service.dto.UdiPerson;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static no.nav.udistub.converter.DefaultTestData.createPersonTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ActiveProfiles("itest")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc(addFilters = false)
public class ITestBase {

    protected static final UdiPerson TESTPERSON_UDI = createPersonTo();

    @BeforeEach
    public void setUp() throws IOException {
        String tpsfResponseBody = getJsonContentsAsString("tpsfResponse-happy.json");
        stubFor(get(urlMatching("/tpsfstuburl/.*"))
                .withHeader(AUTHORIZATION, matching("test"))
                .willReturn(aResponse().withBody(tpsfResponseBody).withStatus(200)));
    }

    protected String getJsonContentsAsString(String filename) throws IOException {
        File requestBodyFile = new ClassPathResource(filename).getFile();
        return new String(Files.readAllBytes(requestBodyFile.toPath()));
    }
}
