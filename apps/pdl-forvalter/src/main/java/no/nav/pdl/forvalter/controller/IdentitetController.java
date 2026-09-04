package no.nav.pdl.forvalter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.opensearch.BestillingQueryService;
import no.nav.pdl.forvalter.service.IdentitetService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static java.util.Collections.emptySet;
import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_HEADER_JWT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RestController
@RequestMapping("/api/v1/identiteter")
@RequiredArgsConstructor
public class IdentitetController {

    private static final String NAV_ORG_NR = "889640782";

    private final JsonMapper jsonMapper;
    private final IdentitetService identitetService;

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private final BestillingQueryService bestillingQueryService;

    @GetMapping(produces = "application/json; charset=utf-8")
    @Operation(description = "Søk etter identitet i databasen basert på fragment av ident og/eller en eller flere navn")
    public Flux<PersonIDDTO> getPerson(@RequestHeader Map<String, String> headers,
                                       @Parameter(description = "Fragment av ident og/eller en eller flere navn")
                                       @RequestParam(required = false) String fragment,
                                       @Parameter(description = "Sidenummer ved sortering på 'sistOppdatert' og nyeste først")
                                       @RequestParam(required = false, defaultValue = "0") Integer sidenummer,
                                       @Parameter(description = "Sidestørrelse ved sortering på 'sistOppdatert' og nyeste først")
                                       @RequestParam(required = false, defaultValue = "10") Integer sidestorrelse) {

        var bankIdOrgNr = getBankIdOrgNr(headers);

        var orgIdenter = isBlank(bankIdOrgNr)
                ? Mono.just(emptySet())
                : bestillingQueryService.execIdenterCacheQuery(bankIdOrgNr);

        return orgIdenter.flatMapMany(identer ->
                        identitetService.getfragment(fragment, Paginering.builder()
                                        .sidenummer(sidenummer)
                                        .sidestoerrelse(sidestorrelse)
                                        .build())
                                .filter(person -> isBlank(bankIdOrgNr) ||
                                                  identer.contains(person.getIdent())));
    }

    @PutMapping(value = "/{ident}/standalone/{standalone}")
    @Operation(description = "Oppdaterer angitt person med standalone satt (true/false).<br>" +
                             "Når satt, blir dette håndtert som en ekstern person som ikke skal inkluderes ved sletting.")
    public Mono<Void> updateStandalone(@Parameter(description = "Ident for testperson")
                                       @PathVariable String ident,
                                       @PathVariable Boolean standalone) {

        return identitetService.updateStandalone(ident, standalone);
    }

    public String getBankIdOrgNr(Map<String, String> headers) {

        var userJwt = headers.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(USER_HEADER_JWT))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

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