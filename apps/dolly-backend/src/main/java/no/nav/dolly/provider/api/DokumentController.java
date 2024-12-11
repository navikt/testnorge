package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.service.DokumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dokument")
public class DokumentController {

    private final DokumentService dokumentService;

    @Operation(description = "Henter dokumenter basert på bestillingId")
    @GetMapping("/bestilling/{bestillingId}")
    public List<Dokument> getDokumenter(@Parameter(description = "bestillingId kan være fra bestilling eller malbestilling")
                                        @PathVariable("bestillingId") Long bestilllingId) {

        return dokumentService.getDokumenter(bestilllingId);
    }
}
