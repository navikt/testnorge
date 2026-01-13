package no.nav.organisasjonforvalter.controller;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@ActiveProfiles("test")
class OrganisasjonControllerJackson3SerializationTest {

    @LocalServerPort
    private int port;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeMapWithRsOrganisasjon() throws Exception {
        RsOrganisasjon org1 = RsOrganisasjon.builder()
                .organisasjonsnummer("896929119")
                .juridiskEnhet("963743254")
                .enhetstype("BEDR")
                .organisasjonsnavn("SAUEFABRIKK")
                .build();

        RsOrganisasjon org2 = RsOrganisasjon.builder()
                .organisasjonsnummer("123456789")
                .juridiskEnhet("987654321")
                .enhetstype("AAFY")
                .organisasjonsnavn("TEST ORG")
                .build();

        Map<String, RsOrganisasjon> orgMap = Map.of("q1", org1, "q2", org2);

        String json = objectMapper.writeValueAsString(orgMap);

        assertThat(json).isNotEmpty();
        assertThat(json).contains("q1");
        assertThat(json).contains("q2");
        assertThat(json).contains("896929119");
        assertThat(json).contains("SAUEFABRIKK");
        assertThat(json).contains("123456789");
        assertThat(json).contains("TEST ORG");
        
        Map<String, RsOrganisasjon> deserialized = objectMapper.readValue(
                json,
                objectMapper.getTypeFactory().constructMapType(
                        Map.class, 
                        String.class, 
                        RsOrganisasjon.class
                )
        );
        
        assertThat(deserialized).hasSize(2);
        assertThat(deserialized.get("q1").getOrganisasjonsnummer()).isEqualTo("896929119");
        assertThat(deserialized.get("q2").getOrganisasjonsnummer()).isEqualTo("123456789");
    }
}

