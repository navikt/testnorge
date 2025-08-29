package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.service.DokumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dokument")
public class DokumentController {

    private final DokumentService dokumentService;

    @Operation(description = "Henter dokumenter basert på bestillingId")
    @GetMapping("/bestilling/{bestillingId}")
    public Flux<Dokument> getDokumenterRelatertTilBestilling(@Parameter(description = "bestillingId fra bestilling")
                                        @PathVariable("bestillingId") Long bestilllingId) {

        return dokumentService.getDokumenterByBestilling(bestilllingId);
    }

    @Operation(description = "Henter dokumenter basert på mal-Id")
    @GetMapping("/mal/{malId}")
    public Flux<Dokument> getDokumenterRelatertTilMal(@Parameter(description = "mal-Id fra mal")
                                        @PathVariable("malId") Long malId) {

        return dokumentService.getDokumenterByMal(malId);
    }

    @Operation(description = "Henter dokumenter basert på liste av dokumentId")
    @GetMapping("/dokument/{dokumentId}")
    public Flux<Dokument> getDokumenter(@Parameter(description = "Liste av dokumentId")
                                        @PathVariable("dokumentId") List<Long> dokumentIdListe) {

        return dokumentService.getDokumenter(dokumentIdListe);
    }
}