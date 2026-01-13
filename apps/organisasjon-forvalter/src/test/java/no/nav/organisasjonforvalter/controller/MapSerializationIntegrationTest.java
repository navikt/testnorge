package no.nav.organisasjonforvalter.controller;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class MapSerializationIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeMapWithRsOrganisasjonToJsonInController() throws Exception {
        Map<String, RsOrganisasjon> testData = new HashMap<>();
        testData.put("q1", RsOrganisasjon.builder()
                .organisasjonsnummer("896929119")
                .juridiskEnhet("963743254")
                .enhetstype("BEDR")
                .organisasjonsnavn("SAUEFABRIKK")
                .build());

        String json = objectMapper.writeValueAsString(testData);

        assertThat(json)
                .contains("896929119")
                .contains("SAUEFABRIKK")
                .contains("BEDR")
                .contains("q1");

        System.out.println("Serialized JSON: " + json);
    }
}
