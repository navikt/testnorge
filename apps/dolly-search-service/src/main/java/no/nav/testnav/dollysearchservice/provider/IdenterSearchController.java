package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.service.IdenterSearchService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.IdentdataDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RestController
@RequestMapping("/api/v1/identer")
public class IdenterSearchController extends AbstractJwtOrgnrExtractor {

    private final IdenterSearchService identerSearchService;

    public IdenterSearchController(JsonMapper jsonMapper, IdenterSearchService identerSearchService) {
        super(jsonMapper);
        this.identerSearchService = identerSearchService;
    }

    @GetMapping
    @Operation(description = "Henter testnorge-identer som matcher søk i request")
    public Flux<IdentdataDTO> getIdenter(@RequestHeader Map<String, String> headers,
            @RequestParam(name = "fragment", required = false) String fragment,
            @RequestParam(defaultValue = "0") int side,
            @RequestParam(defaultValue = "10") int antall,
            @RequestParam(required = false) Integer seed) {

        return isNotBlank(fragment) ?
                identerSearchService.getIdenter(fragment, side, antall, seed, getBankIdOrgNr(headers)) :
                Flux.empty();
    }
}