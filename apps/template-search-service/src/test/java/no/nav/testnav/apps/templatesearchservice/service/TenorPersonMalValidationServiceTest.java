package no.nav.testnav.apps.templatesearchservice.service;

import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenorPersonMalValidationServiceTest {

    private JsonMapper jsonMapper;
    private TenorPersonMalValidationService validationService;

    @BeforeEach
    void setUp() {
        jsonMapper = JsonMapper.builder().build();
        validationService = new TenorPersonMalValidationService(jsonMapper);
    }

    @Test
    void shouldNormalizeValidPersonTemplate() {
        var request = new OpprettTenorPersonMalRequest(
                "  Min Mal  ",
                jsonMapper.readTree("""
                        {"personstatus":"Bosatt"}
                        """));

        var result = validationService.validate(request);

        assertThat(result.malNavn()).isEqualTo("Min Mal");
        assertThat(result.soekKriterier()).isEqualTo("{\"personstatus\":\"Bosatt\"}");
    }

    @Test
    void shouldRejectPersonIdentifierAnywhereInTemplate() {
        var request = new OpprettTenorPersonMalRequest(
                "Min mal",
                jsonMapper.readTree("""
                        {"avansert":{"fornavn":"verdi-41010100044-verdi"}}
                        """));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malen kan ikke inneholde fødselsnummer eller d-nummer.");
    }

    @Test
    void shouldRejectPersonIdentifierInObjectFieldName() {
        var request = new OpprettTenorPersonMalRequest(
                "Min mal",
                jsonMapper.readTree("""
                        {"avansert":{"41010100044":"verdi"}}
                        """));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malen kan ikke inneholde fødselsnummer eller d-nummer.");
    }

    @Test
    void shouldRejectUnsupportedCharactersInTemplateName() {
        var request = new OpprettTenorPersonMalRequest(
                "Min mal!",
                jsonMapper.readTree("{}"));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malnavn kan bare inneholde bokstaver, tall, mellomrom, bindestrek og parenteser.");
    }

    @Test
    void shouldRejectNonObjectCriteria() {
        var request = new OpprettTenorPersonMalRequest(
                "Min mal",
                jsonMapper.readTree("\"tekst\""));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Søkekriterier må være et JSON-objekt.");
    }
}
