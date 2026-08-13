package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.service.IdenterSearchService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.IdentdataDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdenterSearchController {

    private final IdenterSearchService identerSearchService;

    @GetMapping
    @Operation(description = "Henter testnorge-identer som matcher søk i request")
    public Flux<IdentdataDTO> getIdenter(
            @RequestParam(name = "fragment", required = false) String fragment,
            @RequestParam(defaultValue = "0") int side,
            @RequestParam(defaultValue = "10") int antall,
            @RequestParam(required = false) Integer seed) {

        return isNotBlank(fragment) ?
                Flux.fromIterable(identerSearchService.getIdenter(fragment, side, antall, seed)) :
                Flux.empty();
    }
}