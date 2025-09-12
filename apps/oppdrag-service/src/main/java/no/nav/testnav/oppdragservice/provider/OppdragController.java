package no.nav.testnav.oppdragservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragKodeverk;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragResponse;
import no.nav.testnav.oppdragservice.service.KodeverkService;
import no.nav.testnav.oppdragservice.service.OppdragService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/oppdrag")
@RequiredArgsConstructor
public class OppdragController {

    private final OppdragService oppdragService;
    private final KodeverkService kodeverkService;

    @PostMapping("/{miljoe}")
    @Operation(summary = "Send inn oppdrag")
    public OppdragResponse sendInnOppdrag(@PathVariable String miljoe,
                                                @RequestBody OppdragRequest oppdragRequest) {

        return oppdragService.sendInnOppdrag(miljoe, oppdragRequest);
    }

    @GetMapping("/kodeverk/{kodeverk}")
    @Operation(summary = "Hent kodeverk")
    public List<String> getKodeverk(@PathVariable OppdragKodeverk kodeverk) {

        return kodeverkService.getKodeverk(kodeverk);
    }
}
