package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.domain.OpprettTenorMalRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.ValidertTenorMal;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalValidationException;
import no.nav.testnav.libs.securitycore.validation.IdentValidCheck;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class TenorMalValidationService {

    private static final int MAX_PAYLOAD_BYTES = 256 * 1024;
    private static final int IDENT_LENGTH = 11;

    private final JsonMapper jsonMapper;

    public ValidertTenorMal validate(OpprettTenorMalRequest request) {
        var malNavn = validateMalNavn(request.malNavn());
        var soekKriterier = validateSoekKriterier(request.soekKriterier(), request.malType());
        return new ValidertTenorMal(
                malNavn,
                normalizeMalNavn(malNavn),
                request.malType(),
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
        validateNoPersonidentifikator(trimmedMalNavn);
        return trimmedMalNavn;
    }

    public String normalizeMalNavn(String malNavn) {
        return malNavn.toLowerCase(Locale.ROOT);
    }

    public void validateNoPersonidentifikator(String value) {
        if (containsPersonidentifikator(value)) {
            throw new TenorMalValidationException("Malen kan ikke inneholde fødselsnummer eller d-nummer.");
        }
    }

    private String validateSoekKriterier(JsonNode soekKriterier, TenorMalType malType) {
        if (malType == null) {
            throw new TenorMalValidationException("Maltype må oppgis.");
        }
        if (soekKriterier == null || !soekKriterier.isObject()) {
            throw new TenorMalValidationException("Søkekriterier må være et JSON-objekt.");
        }

        try {
            var normalizedJson = jsonMapper.writeValueAsString(soekKriterier);
            if (normalizedJson.getBytes(StandardCharsets.UTF_8).length > MAX_PAYLOAD_BYTES) {
                throw new TenorMalValidationException("Søkekriteriene er for store.");
            }
            validateNoPersonidentifikator(soekKriterier);
            switch (malType) {
                case PERSON -> jsonMapper.treeToValue(soekKriterier, TenorRequest.class);
                case ORGANISASJON -> jsonMapper.treeToValue(soekKriterier, TenorOrganisasjonRequest.class);
            }
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
