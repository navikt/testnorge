package no.nav.testnav.apps.tenorsearchservice.provider;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.apps.tenorsearchservice.service.TenorOrganisasjonSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tenor")
@RequiredArgsConstructor
public class TenorOrganisasjonSearchController {

    private final TenorOrganisasjonSearchService tenorOrganisasjonSearchService;

    @PostMapping(path = "/testdata/oversikt/organisasjoner", produces = "application/json", consumes = "application/json")
    public Mono<TenorOversiktOrganisasjonResponse> getTestdataOrganisasjon(@RequestBody TenorOrganisasjonRequest searchData,
                                                                           @Schema(description = "Antall resultater per side")
                                                                           @RequestParam(required = false) Integer antall,
                                                                           @Schema(description = "Sidenummer")
                                                                           @RequestParam(required = false) Integer side,
                                                                           @Schema(description = "Seed for paginering")
                                                                           @RequestParam(required = false) Integer seed) {

        return tenorOrganisasjonSearchService.getTestdataOrganisasjon(searchData, antall, side, seed);
    }
}