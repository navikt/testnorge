package no.nav.testnav.dollysearchservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJwtOrgnrExtractor {

    private static final String NAV_ORG_NR = "889640782";
    private final JsonMapper jsonMapper;

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    public String getBankIdOrgNr(String userJwt) {

        if (isBlank(userJwt) || !userJwt.contains(".")) {
            return null;
        }

        try {
            var body = userJwt.split("\\.")[1];
            var payload = new String(DECODER.decode(body), StandardCharsets.UTF_8);
            var orgnr = jsonMapper.readTree(payload)
                    .path("org")
                    .asString(null);

            return isBlank(orgnr) || NAV_ORG_NR.equals(orgnr) ? null : orgnr;
        } catch (Exception e) {
            log.warn("Kunne ikke hente BankID orgnr fra User-Jwt", e);
            return null;
        }
    }
}
