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
    void shouldDeserializeFromUppercaseEnumName() throws Exception {
        String json = "\"SKATTEKORTOPPLYSNINGER_OK\"";
        Resultatstatus result = objectMapper.readValue(json, Resultatstatus.class);
        assertThat(result).isEqualTo(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
    }

    @Test
    void shouldDeserializeFromCamelCase() throws Exception {
        String json = "\"skattekortopplysningerOK\"";
        Resultatstatus result = objectMapper.readValue(json, Resultatstatus.class);
        assertThat(result).isEqualTo(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
    }

    @Test
    void shouldDeserializeAllValuesFromCamelCase() throws Exception {
        assertThat(objectMapper.readValue("\"ikkeSkattekort\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.IKKE_SKATTEKORT);
        assertThat(objectMapper.readValue("\"vurderArbeidstillatelse\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.VURDER_ARBEIDSTILLATELSE);
        assertThat(objectMapper.readValue("\"ikkeTrekkplikt\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.IKKE_TREKKPLIKT);
        assertThat(objectMapper.readValue("\"ugyldigOrganisasjonsnummer\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UGYLDIG_ORGANISASJONSNUMMER);
        assertThat(objectMapper.readValue("\"ugyldigFoedselsEllerDnummer\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UGYLDIG_FOEDSELS_ELLER_DNUMMER);
        assertThat(objectMapper.readValue("\"utgaattDnummerSkattekortForFoedselsnummerErLevert\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT);
    }

    @Test
    void shouldDeserializeAllValuesFromUppercase() throws Exception {
        assertThat(objectMapper.readValue("\"IKKE_SKATTEKORT\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.IKKE_SKATTEKORT);
        assertThat(objectMapper.readValue("\"VURDER_ARBEIDSTILLATELSE\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.VURDER_ARBEIDSTILLATELSE);
        assertThat(objectMapper.readValue("\"IKKE_TREKKPLIKT\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.IKKE_TREKKPLIKT);
        assertThat(objectMapper.readValue("\"UGYLDIG_ORGANISASJONSNUMMER\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UGYLDIG_ORGANISASJONSNUMMER);
        assertThat(objectMapper.readValue("\"UGYLDIG_FOEDSELS_ELLER_DNUMMER\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UGYLDIG_FOEDSELS_ELLER_DNUMMER);
        assertThat(objectMapper.readValue("\"UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT\"", Resultatstatus.class))
                .isEqualTo(Resultatstatus.UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT);
    }

    @Test
    void shouldSerializeToCamelCase() throws Exception {
        String json = objectMapper.writeValueAsString(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK);
        assertThat(json).isEqualTo("\"skattekortopplysningerOK\"");
    }
}
