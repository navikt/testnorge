package no.nav.testnav.apps.tenorsearchservice.provider;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.MaskinportenConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.domain.AccessToken;
import no.nav.testnav.apps.tenorsearchservice.domain.Lookups;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.apps.tenorsearchservice.service.LookupService;
import no.nav.testnav.apps.tenorsearchservice.service.TenorSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static no.nav.testnav.apps.tenorsearchservice.consumers.dto.BestillingIndexSelector.DEV;
import static no.nav.testnav.apps.tenorsearchservice.consumers.dto.BestillingIndexSelector.REGULAR;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_HEADER_JWT;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenor")
@RequiredArgsConstructor
public class TenorSearchController {

    private static final String NAV_ORG_NR = "889640782";

    private final TenorSearchService tenorSearchService;
    private final MaskinportenConsumer maskinportenConsumer;
    private final LookupService lookupService;
    private final JsonMapper jsonMapper;

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    @PostMapping(path = "/testdata/oversikt", produces = "application/json", consumes = "application/json")
    public Mono<TenorOversiktResponse> getTestdata(@RequestHeader Map<String, String> headers,
                                                   @RequestBody TenorRequest searchData,
                                                   @Schema(description = "Antall resultater per side")
                                                   @RequestParam(required = false) Integer antall,
                                                   @Schema(description = "Sidenummer")
                                                   @RequestParam(required = false) Integer side,
                                                   @Schema(description = "Seed for paginering")
                                                   @RequestParam(required = false) Integer seed,
                                                   @Schema(description = "Ikke filtrer søkeresultat for eksisterende personer (default er filtrering")
                                                   @RequestParam(required = false) Boolean ikkeFiltrer) {

        var kallendeApp = headers.getOrDefault("Origin", headers.get("origin"));
        var selector = isNotBlank(kallendeApp) && kallendeApp.contains("ekstern") ? REGULAR : DEV;
        var orgnr = getBankIdOrgNr(headers.get(USER_HEADER_JWT));

        log.info("Kallende app: {}, selector: {}, orgnr: {}", kallendeApp, selector, orgnr);

        return tenorSearchService.getTestdata(searchData, antall, side, seed, selector, ikkeFiltrer, orgnr);
    }

    @GetMapping("/testdata/raw")
    public Mono<TenorResponse> getTestdata(@Schema(description = "Søkekriterier")
                                           @RequestParam(required = false) String searchData,
                                           @Parameter(description = "InfoType, kategori av felter som skal returneres")
                                           @RequestParam(required = false) InfoType type,
                                           @Schema(description = "Felter (kommaseparert liste) som skal returneres, når InfoType er 'Spesifikt'")
                                           @RequestParam(required = false) String fields,
                                           @Schema(description = "Seed for paginering")
                                           @RequestParam(required = false) Integer seed) {

        return tenorSearchService
                .getTestdata(searchData, type, fields, seed);
    }

    @PostMapping(path = "/testdata", produces = "application/json", consumes = "application/json")
    public Mono<TenorResponse> getTestdata(@RequestBody TenorRequest searchData,
                                           @Parameter(description = "InfoType, kategori felter som skal returneres")
                                           @RequestParam(required = false) InfoType type,
                                           @Schema(description = "Felter (kommaseparert liste) som skal returneres, når InfoType er 'Spesifikt'")
                                           @RequestParam(required = false) String fields,
                                           @Schema(description = "Antall resultater per side")
                                           @RequestParam(required = false) Integer antall,
                                           @Schema(description = "Sidenummer")
                                           @RequestParam(required = false) Integer side,
                                           @Schema(description = "Seed for paginering")
                                           @RequestParam(required = false) Integer seed) {

        return tenorSearchService
                .getTestdata(searchData, type, fields, antall, side, seed);
    }

    @GetMapping("/testdata/domain")
    public List<String> getTestdataDomain(@Parameter(description = "Velg liste av verdier for oppslag")
                                          @RequestParam Lookups lookup) {

        return lookupService.getLookup(lookup);
    }

    @GetMapping("/testdata/token")
    public Mono<String> getToken() {

        return maskinportenConsumer.getAccessToken()
                .map(AccessToken::value);
    }

    private String getBankIdOrgNr(String userJwt) {

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