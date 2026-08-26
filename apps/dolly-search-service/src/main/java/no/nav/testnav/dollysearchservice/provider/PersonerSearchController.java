package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.service.PersonerSearchService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchResponse;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.Base64;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonerSearchController {

    private static final String NAV_ORG_NR = "889640782";
    private final PersonerSearchService personerSearchService;
    private final JsonMapper jsonMapper;

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    @PostMapping
    @Operation(description = "Henter Dolly-personer som matcher både søk i registre og søk av persondetaljer i PDL")
    public Mono<SearchResponse> getPersoner(@RequestParam(required = false) List<ElasticTyper> registreRequest,
                                            @RequestBody SearchRequest request,
                                            @RequestHeader(required = false, value = UserConstant.USER_HEADER_JWT) String header) {

        log.info("Mottatt request header: {}", header);

        var orgnr = getBankIdOrgNr(header);

        if (isBlank(orgnr)) {
            log.info("Request med AZURE-bruker");
            request.setBrukerType("AZURE");

        } else {
            log.info("Request med BANKID-bruker, orgnr: {}", orgnr);
            request.setBrukerType("BANKID");
            request.setOrgnr(orgnr);
        }

        return Mono.just(personerSearchService.search(request, registreRequest));
    }

    @GetMapping("/typer")
    @Operation(description = "Henter alle søketyper mot registre")
    public Mono<List<Kategori>> getKategorier() {

        return Mono.just(personerSearchService.getTyper());
    }

    private String getBankIdOrgNr(String header) {

        if (isBlank(header) || !header.contains(".")) {
            return null;
        }

        var body = header.split("\\.")[1];
        var payload = new String(DECODER.decode(body));
        var tree = jsonMapper.readTree(payload);
        var orgnr = tree.get("org");

        return nonNull(orgnr) && !NAV_ORG_NR.equals(orgnr.toString()) ? orgnr.asString() : null;
    }
}