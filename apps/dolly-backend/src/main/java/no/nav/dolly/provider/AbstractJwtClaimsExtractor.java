package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.Base64;
import java.util.Map;

import static java.util.Objects.isNull;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_HEADER_JWT;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJwtClaimsExtractor {

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final JsonMapper jsonMapper;

    public String getBankIdOrgNr(Map<String, String> headers) {

        var claims = getClaims(headers);
        if (isNull(claims)) {
            return null;
        }

        var orgnr = claims.path("org").asString(null);
        return StringUtils.isBlank(orgnr) || UserConstant.NAV_ORGANIZATION_NUMBER.equals(orgnr)
                ? null
                : orgnr;
    }

    public String getUserId(Map<String, String> headers) {

        var claims = getClaims(headers);
        if (isNull(claims)) {
            return null;
        }

        var oid = claims.path("oid").asString(null);
        return StringUtils.isBlank(oid)
                ? claims.path("pid").asString(null)
                : oid;
    }

    private JsonNode getClaims(Map<String, String> headers) {

        var userJwt = headers.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(USER_HEADER_JWT))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (StringUtils.isBlank(userJwt)) {
            return null;
        }

        var tokenParts = userJwt.split("\\.", -1);
        if (tokenParts.length != 3 || StringUtils.isBlank(tokenParts[1])) {
            return null;
        }

        try {
            var payload = DECODER.decode(tokenParts[1]);
            return jsonMapper.readTree(payload);
        } catch (IllegalArgumentException | JacksonException exception) {
            log.warn("Kunne ikke lese claims fra User-Jwt", exception);
            return null;
        }
    }
}