package no.nav.testnav.apps.templatesearchservice.service;

import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @ValueSource(strings = {
            "Min mal, med komma!",
            "Én mal: A/B & C + 50 %? [2026_1]",
            "Ola's \"testmal\".",
            "Alder < 18 og inntekt > 0"
    })
    void shouldPreservePunctuationInTemplateName(String malNavn) {
        var request = new OpprettTenorPersonMalRequest(
                "  " + malNavn + "  ",
                jsonMapper.readTree("{}"));

        var result = validationService.validate(request);

        assertThat(result.malNavn()).isEqualTo(malNavn);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 9, 10, 13, 31, 127, 133, 159})
    void shouldRejectControlCharactersInTemplateName(int controlCharacter) {
        var malNavn = "Mal" + Character.toString(controlCharacter) + "navn";

        assertThatThrownBy(() -> validationService.validateMalNavn(malNavn))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malnavn kan ikke inneholde kontrolltegn.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " \t\n "})
    void shouldRejectBlankTemplateName(String malNavn) {
        assertThatThrownBy(() -> validationService.validateMalNavn(malNavn))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malnavn må oppgis.");
    }

    @Test
    void shouldAllowTemplateNameOfOneHundredCharacters() {
        var malNavn = "A".repeat(99) + "!";

        assertThat(validationService.validateMalNavn(malNavn)).isEqualTo(malNavn);
    }

    @Test
    void shouldRejectTemplateNameLongerThanOneHundredCharacters() {
        var malNavn = "A".repeat(100) + "!";

        assertThatThrownBy(() -> validationService.validateMalNavn(malNavn))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malnavn kan ikke være lengre enn 100 tegn.");
    }

    @Test
    void shouldRejectPersonIdentifierInTemplateNameWithPunctuation() {
        var request = new OpprettTenorPersonMalRequest(
                "Min mal, 41010100044!",
                jsonMapper.readTree("{}"));

        assertThatThrownBy(() -> validationService.validate(request))
                .isInstanceOf(TenorMalValidationException.class)
                .hasMessage("Malen kan ikke inneholde fødselsnummer eller d-nummer.");
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
