package no.nav.testnav.oppdragservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.oppdragservice.service.OppdragService;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oppdrag")
@RequiredArgsConstructor
public class OppdragController {

    private final OppdragService oppdragService;

    @PostMapping
    public SendInnOppdragResponse sendInnOppdrag(OppdragRequest oppdragRequest) {

        return oppdragService.sendInnOppdrag(oppdragRequest);
    }
}
