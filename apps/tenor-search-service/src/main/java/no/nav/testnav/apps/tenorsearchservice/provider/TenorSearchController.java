package no.nav.testnav.apps.tenorsearchservice.provider;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.MaskinportenClient;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.Kilde;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenor")
@RequiredArgsConstructor
public class TenorSearchController {

    private final TenorSearchService tenorSearchService;
    private final MaskinportenClient maskinportenClient;
    private final LookupService lookupService;

    @PostMapping(path = "/testdata/oversikt", produces = "application/json", consumes = "application/json")
    public Mono<TenorOversiktResponse> getTestdata(@RequestBody TenorRequest searchData,
                                                   @Schema(description = "Antall resultater per side")
                                                   @RequestParam(required = false) Integer antall,
                                                   @Schema(description = "Sidenummer")
                                                   @RequestParam(required = false) Integer side,
                                                   @Schema(description = "Seed for paginering")
                                                   @RequestParam(required = false) Integer seed) {

        return tenorSearchService.getTestdata(searchData, antall, side, seed);
    }

    @GetMapping("/testdata/raw")
    public Mono<TenorResponse> getTestdata(@Schema(description = "Søkekriterier")
                                           @RequestParam(required = false) String searchData,
                                           @Parameter(description = "Kilde, hvor data skal hentes fra")
                                           @RequestParam(required = false) Kilde kilde,
                                           @Parameter(description = "InfoType, kategori av felter som skal returneres")
                                           @RequestParam(required = false) InfoType type,
                                           @Schema(description = "Felter (kommaseparert liste) som skal returneres, når InfoType er 'Spesifikt'")
                                           @RequestParam(required = false) String fields,
                                           @Schema(description = "Seed for paginering")
                                           @RequestParam(required = false) Integer seed) {

        return tenorSearchService
                .getTestdata(searchData, kilde, type, fields, seed);
    }

    @PostMapping(path ="/testdata", produces = "application/json", consumes = "application/json")
    public Mono<TenorResponse> getTestdata(@RequestBody TenorRequest searchData,
                                           @Parameter(description = "Kilde, hvor data skal hentes fra")
                                           @RequestParam(required = false) Kilde kilde,
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
                .getTestdata(searchData, kilde, type, fields, antall, side, seed);
    }

    @GetMapping("/testdata/domain")
    public List<String> getTestdataDomain(@Parameter(description = "Velg liste av verdier for oppslag")
                                          @RequestParam Lookups lookup) {

        return lookupService.getLookup(lookup);
    }

    @GetMapping("/testdata/token")
    public Mono<String> getToken() {

        return maskinportenClient.getAccessToken()
                .map(AccessToken::value);
    }
}