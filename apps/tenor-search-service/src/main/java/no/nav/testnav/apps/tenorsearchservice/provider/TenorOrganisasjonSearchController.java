package no.nav.testnav.apps.tenorsearchservice.provider;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.domain.OrganisasjonLookups;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.apps.tenorsearchservice.service.OrganisasjonLookupService;
import no.nav.testnav.apps.tenorsearchservice.service.TenorOrganisasjonSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tenor")
@RequiredArgsConstructor
public class TenorOrganisasjonSearchController {

    private final TenorOrganisasjonSearchService tenorOrganisasjonSearchService;
    private final OrganisasjonLookupService lookupService;

    @PostMapping(path = "/testdata/organisasjoner/oversikt", produces = "application/json", consumes = "application/json")
    public Mono<TenorOversiktOrganisasjonResponse> getTestdataOversiktOrganisasjoner(@RequestBody TenorOrganisasjonRequest searchData,
                                                                                     @Schema(description = "Antall resultater per side")
                                                                                     @RequestParam(required = false) Integer antall,
                                                                                     @Schema(description = "Sidenummer")
                                                                                     @RequestParam(required = false) Integer side,
                                                                                     @Schema(description = "Seed for paginering")
                                                                                     @RequestParam(required = false) Integer seed) {

        return tenorOrganisasjonSearchService.getTestdataOversiktOrganisasjon(searchData, antall, side, seed);
    }

    @PostMapping(path = "/testdata/organisasjoner", produces = "application/json", consumes = "application/json")
    public Mono<TenorOversiktOrganisasjonResponse> getTestdataOrganisasjoner(@RequestBody TenorOrganisasjonRequest searchData) {

        return tenorOrganisasjonSearchService.getTestdataOrganisasjon(searchData);
    }

    @GetMapping("/testdata/organisasjoner/domain")
    public Map<String, String> getTestdataOrganisasjonDomain(@Parameter(description = "Velg liste av verdier for oppslag")
                                                             @RequestParam OrganisasjonLookups lookup) {

        return lookupService.getLookup(lookup);
    }
}