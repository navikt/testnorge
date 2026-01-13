package no.nav.organisasjonforvalter.dto;

import no.nav.organisasjonforvalter.dto.responses.RsAdresse;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static no.nav.organisasjonforvalter.dto.responses.RsAdresse.AdresseType.FADR;
import static no.nav.organisasjonforvalter.dto.responses.RsAdresse.AdresseType.PADR;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RsOrganisasjonJackson3SerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeRsOrganisasjonWithAllFields() throws Exception {
        RsOrganisasjon organisasjon = RsOrganisasjon.builder()
                .organisasjonsnummer("896929119")
                .juridiskEnhet("963743254")
                .enhetstype("BEDR")
                .organisasjonsnavn("SAUEFABRIKK")
                .adresser(List.of(
                        RsAdresse.builder()
                                .adressetype(PADR)
                                .adresselinjer(List.of("OPALV 75"))
                                .postnr("5252")
                                .poststed("SØREIDGREND")
                                .kommunenr("4601")
                                .landkode("NO")
                                .build(),
                        RsAdresse.builder()
                                .adressetype(FADR)
                                .adresselinjer(List.of("OPALV 75"))
                                .postnr("5252")
                                .poststed("SØREIDGREND")
                                .kommunenr("4601")
                                .landkode("NO")
                                .build()
                ))
                .build();

        String json = objectMapper.writeValueAsString(organisasjon);

        assertThat(json).isNotEmpty();
        assertThat(json).contains("896929119");
        assertThat(json).contains("SAUEFABRIKK");
        assertThat(json).contains("BEDR");
        assertThat(json).contains("963743254");
        assertThat(json).contains("OPALV 75");
        assertThat(json).contains("PADR");
        assertThat(json).contains("FADR");

        RsOrganisasjon deserialized = objectMapper.readValue(json, RsOrganisasjon.class);
        assertThat(deserialized.getOrganisasjonsnummer()).isEqualTo("896929119");
        assertThat(deserialized.getOrganisasjonsnavn()).isEqualTo("SAUEFABRIKK");
    }

    @Test
    void shouldSerializeEmptyRsOrganisasjon() throws Exception {
        RsOrganisasjon organisasjon = RsOrganisasjon.builder()
                .organisasjonsnummer("896929119")
                .build();

        String json = objectMapper.writeValueAsString(organisasjon);

        assertThat(json).isNotEmpty();
        assertThat(json).contains("organisasjonsnummer");
        assertThat(json).contains("896929119");
    }
}

