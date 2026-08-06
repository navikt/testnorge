package no.nav.dolly.bestilling.skattekort.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ResultatstatusTest {

    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        jsonMapper = new JsonMapper();
    }

    @Test
    void shouldDeserializeFromUppercaseEnumName() {
        String json = "\"SKATTEKORTOPPLYSNINGER_OK\"";
        Resultatstatus result = jsonMapper.readValue(json, Resultatstatus.class);
        assertThat(result).isEqualTo(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
    }

    @Test
    void shouldDeserializeAllValuesFromUppercase() {
        assertThat(jsonMapper.readValue("\"IKKE_SKATTEKORT\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.IKKE_SKATTEKORT);
        assertThat(jsonMapper.readValue("\"VURDER_ARBEIDSTILLATELSE\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.VURDER_ARBEIDSTILLATELSE);
        assertThat(jsonMapper.readValue("\"IKKE_TREKKPLIKT\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.IKKE_TREKKPLIKT);
        assertThat(jsonMapper.readValue("\"UGYLDIG_ORGANISASJONSNUMMER\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UGYLDIG_ORGANISASJONSNUMMER);
        assertThat(jsonMapper.readValue("\"UGYLDIG_FOEDSELS_ELLER_DNUMMER\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UGYLDIG_FOEDSELS_ELLER_DNUMMER);
        assertThat(jsonMapper.readValue("\"UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT);
    }
}
