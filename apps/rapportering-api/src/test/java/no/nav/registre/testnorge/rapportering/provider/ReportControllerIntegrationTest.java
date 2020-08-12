package no.nav.registre.testnorge.rapportering.provider;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import no.nav.registre.testnorge.dto.rapprtering.v1.EntryStatus;
import no.nav.registre.testnorge.rapportering.consumer.dto.SlackResponse;
import no.nav.registre.testnorge.rapportering.domain.Entry;
import no.nav.registre.testnorge.rapportering.domain.Report;
import no.nav.registre.testnorge.rapportering.repository.ReportRepository;
import no.nav.registre.testnorge.test.JsonWiremockHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportRepository reportRepository;

    @Value("${consumer.slack.channel}")
    private String channel;

    @Test
    @SneakyThrows
    void should_publish_report() {

        Entry entry = new Entry(EntryStatus.INFO, "some description", LocalDateTime.now());
        Report report = new Report(
                null,
                "app_name",
                "test report",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(25),
                Collections.singletonList(entry)
        );
        reportRepository.save(report.toModel());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/chat.postMessage")
                .withResponseBody(SlackResponse.builder().ok(true).build())
                .stubPost();

        mvc.perform(post("/api/v1/report/publish"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/api/chat.postMessage")
                .withRequestBody(report.toSlackMessage(channel))
                .verifyPost();

    }

    @AfterEach
    void after(){
        reset();
        reportRepository.deleteAll();
    }
}