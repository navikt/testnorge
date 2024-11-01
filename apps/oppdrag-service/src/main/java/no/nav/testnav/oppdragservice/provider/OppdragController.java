package no.nav.testnav.oppdragservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragResponse;
import no.nav.testnav.oppdragservice.service.OppdragService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oppdrag")
@RequiredArgsConstructor
public class OppdragController {

    private final OppdragService oppdragService;

    @PostMapping
    @Operation(summary = "Send inn oppdrag")
    public OppdragResponse sendInnOppdrag(@RequestBody OppdragRequest oppdragRequest) {

        return oppdragService.sendInnOppdrag(oppdragRequest);
    }
}
