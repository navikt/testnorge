package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.service.PersonerSearchService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_HEADER_JWT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
public class PersonerSearchController extends AbstractJwtOrgnrExtractor {

    private final PersonerSearchService personerSearchService;

    public PersonerSearchController(JsonMapper jsonMapper, PersonerSearchService personerSearchService) {
        super(jsonMapper);
        this.personerSearchService = personerSearchService;
    }

    @PostMapping
    @Operation(description = "Henter Dolly-personer som matcher både søk i registre og søk av persondetaljer i PDL")
    public Mono<SearchResponse> getPersoner(@RequestHeader Map<String, String> headers,
                                            @RequestParam(required = false) List<ElasticTyper> registreRequest,
                                            @RequestBody SearchRequest request) {

        var orgnr = getBankIdOrgNr(headers.get(USER_HEADER_JWT));
        var brukerType = isBlank(orgnr) ? "AZURE" : "BANKID";

        log.info("Mottatt søk med brukerType: {}, orgnr: {}", brukerType, orgnr);

        return personerSearchService.search(request, registreRequest, orgnr);
    }

    @GetMapping("/typer")
    @Operation(description = "Henter alle søketyper mot registre")
    public Mono<List<Kategori>> getKategorier() {

        return Mono.just(personerSearchService.getTyper());
    }
}