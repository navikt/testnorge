package no.nav.testnav.apps.templatesearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.domain.TenorRequest;
import no.nav.testnav.apps.templatesearchservice.domain.ValidertTenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalValidationException;
import no.nav.testnav.libs.securitycore.validation.IdentValidCheck;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class TenorPersonMalValidationService {

    private static final int MAX_PAYLOAD_BYTES = 256 * 1024;
    private static final int IDENT_LENGTH = 11;
    private static final Pattern MAL_NAVN_PATTERN =
            Pattern.compile("^[A-Za-zÆØÅæøå0-9 ()-]+$");

    private final JsonMapper jsonMapper;

    public ValidertTenorPersonMal validate(OpprettTenorPersonMalRequest request) {
        var malNavn = validateMalNavn(request.malNavn());
        var soekKriterier = validateSoekKriterier(request.soekKriterier());
        return new ValidertTenorPersonMal(
                malNavn,
                soekKriterier);
    }

    public String validateMalNavn(String malNavn) {
        if (isBlank(malNavn)) {
            throw new TenorMalValidationException("Malnavn må oppgis.");
        }
        var trimmedMalNavn = malNavn.trim();
        if (trimmedMalNavn.length() > 100) {
            throw new TenorMalValidationException("Malnavn kan ikke være lengre enn 100 tegn.");
        }
        if (!MAL_NAVN_PATTERN.matcher(trimmedMalNavn).matches()) {
            throw new TenorMalValidationException(
                    "Malnavn kan bare inneholde bokstaver, tall, mellomrom, bindestrek og parenteser.");
        }
        validateNoPersonidentifikator(trimmedMalNavn);
        return trimmedMalNavn;
    }

    public void validateNoPersonidentifikator(String value) {
        if (containsPersonidentifikator(value)) {
            throw new TenorMalValidationException("Malen kan ikke inneholde fødselsnummer eller d-nummer.");
        }
    }

    private String validateSoekKriterier(JsonNode soekKriterier) {
        if (soekKriterier == null || !soekKriterier.isObject()) {
            throw new TenorMalValidationException("Søkekriterier må være et JSON-objekt.");
        }

        try {
            var normalizedJson = jsonMapper.writeValueAsString(soekKriterier);
            if (normalizedJson.getBytes(StandardCharsets.UTF_8).length > MAX_PAYLOAD_BYTES) {
                throw new TenorMalValidationException("Søkekriteriene er for store.");
            }
            validateNoPersonidentifikator(soekKriterier);
            jsonMapper.treeToValue(soekKriterier, TenorRequest.class);
            return normalizedJson;
        } catch (JacksonException exception) {
            throw new TenorMalValidationException("Søkekriteriene har ugyldig format.", exception);
        }
    }

    private void validateNoPersonidentifikator(JsonNode node) {
        if (node.isString() || node.isNumber()) {
            validateNoPersonidentifikator(node.asString());
            return;
        }
        if (node.isObject()) {
            node.forEachEntry((fieldName, value) -> {
                validateNoPersonidentifikator(fieldName);
                validateNoPersonidentifikator(value);
            });
            return;
        }
        node.forEach(this::validateNoPersonidentifikator);
    }

    private static boolean containsPersonidentifikator(String value) {
        if (value == null || value.length() < IDENT_LENGTH) {
            return false;
        }
        for (var index = 0; index <= value.length() - IDENT_LENGTH; index++) {
            var candidate = value.substring(index, index + IDENT_LENGTH);
            if (IdentValidCheck.isIdentValid(candidate)) {
                return true;
            }
        }
        return false;
    }
}
