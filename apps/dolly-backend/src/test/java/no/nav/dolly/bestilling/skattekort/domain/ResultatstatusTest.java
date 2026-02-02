package no.nav.dolly.bestilling.skattekort.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultatstatusTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldDeserializeFromCamelCaseValue() throws Exception {
        String json = "\"skattekortopplysningerOK\"";
        Resultatstatus result = objectMapper.readValue(json, Resultatstatus.class);
        assertThat(result).isEqualTo(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
    }

    @Test
    void shouldDeserializeFromUppercaseEnumName() throws Exception {
        String json = "\"SKATTEKORTOPPLYSNINGER_OK\"";
        Resultatstatus result = objectMapper.readValue(json, Resultatstatus.class);
        assertThat(result).isEqualTo(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
    }

    @Test
    void shouldDeserializeFromLowercaseEnumName() throws Exception {
        String json = "\"skattekortopplysninger_ok\"";
        Resultatstatus result = objectMapper.readValue(json, Resultatstatus.class);
        assertThat(result).isEqualTo(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
    }

    @Test
    void shouldSerializeToCamelCaseValue() throws Exception {
        String json = objectMapper.writeValueAsString(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
        assertThat(json).isEqualTo("\"skattekortopplysningerOK\"");
    }
}
