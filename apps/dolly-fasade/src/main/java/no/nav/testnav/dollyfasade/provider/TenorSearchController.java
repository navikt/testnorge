package no.nav.testnav.dollyfasade.provider;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollyfasade.consumers.TenorSearchConsumer;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.InfoType;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.Lookups;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorOversiktResponse;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenor")
@RequiredArgsConstructor
public class TenorSearchController {

    private final TenorSearchConsumer tenorSearchConsumer;

    @PostMapping(path = "/testdata/oversikt", produces = "application/json", consumes = "application/json")
    public Mono<TenorOversiktResponse> getTestdata(@RequestBody TenorRequest searchData,
                                                   @Schema(description = "Antall resultater per side")
                                                   @RequestParam(required = false) Integer antall,
                                                   @Schema(description = "Sidenummer")
                                                   @RequestParam(required = false) Integer side,
                                                   @Schema(description = "Seed for paginering")
                                                   @RequestParam(required = false) Integer seed,
                                                   @Schema(description = "Ikke filtrer søkeresultat for eksisterende personer (default er filtrering")
                                                   @RequestParam(required = false) Boolean ikkeFiltrer) {

        return tenorSearchConsumer.getTestdataOversikt(searchData, antall, side, seed, ikkeFiltrer);
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

        return tenorSearchConsumer.getTestdata(searchData, type, fields, antall, side, seed);
    }

    @GetMapping("/testdata/domain")
    public Mono<List<String>> getTestdataDomain(@Parameter(description = "Velg liste av verdier for oppslag")
                                          @RequestParam Lookups lookup) {

        return tenorSearchConsumer.getLookup(lookup);
    }
}
