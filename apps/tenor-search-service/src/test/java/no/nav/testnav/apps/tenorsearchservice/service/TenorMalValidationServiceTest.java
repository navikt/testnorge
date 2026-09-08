package no.nav.testnav.apps.tenorsearchservice.service;

import no.nav.testnav.apps.tenorsearchservice.domain.OpprettTenorMalRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenorMalValidationServiceTest {

    private JsonMapper jsonMapper;
    private TenorMalValidationService validationService;

    @BeforeEach
    void setUp() {
        jsonMapper = JsonMapper.builder().build();
        validationService = new TenorMalValidationService(jsonMapper);
    }

    @Test
    void shouldNormalizeValidPersonTemplate() {
        var request = new OpprettTenorMalRequest(
                "  Min Mal  ",
                TenorMalType.PERSON,
                jsonMapper.readTree("""
                        {"personstatus":"Bosatt"}
                        """));

        var result = validationService.validate(request);

        assertThat(result.malNavn()).isEqualTo("Min Mal");
        assertThat(result.malNavnNormalisert()).isEqualTo("min mal");
        assertThat(result.soekKriterier()).isEqualTo("{\"personstatus\":\"Bosatt\"}");
    }

    @Test
    void shouldRejectPersonIdentifierAnywhereInTemplate() {
        var request = new OpprettTenorMalRequest(
                "Min mal",
                TenorMalType.PERSON,
                jsonMapper.readTree("""
                        {"avansert":{"fornavn":"verdi-41010100044-verdi"}}
                        """));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malen kan ikke inneholde fødselsnummer eller d-nummer.");
    }

    @Test
    void shouldAllowOrganizationNumber() {
        var request = new OpprettTenorMalRequest(
                "Organisasjon",
                TenorMalType.ORGANISASJON,
                jsonMapper.readTree("""
                        {"organisasjonsnummer":"889640782"}
                        """));

        var result = validationService.validate(request);

        assertThat(result.soekKriterier()).contains("889640782");
    }

    @Test
    void shouldRejectNonObjectCriteria() {
        var request = new OpprettTenorMalRequest(
                "Min mal",
                TenorMalType.PERSON,
                jsonMapper.readTree("\"tekst\""));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Søkekriterier må være et JSON-objekt.");
    }
}
