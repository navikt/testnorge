package no.nav.testnav.identpool.providers.v1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.providers.v1.support.IdentRequest;
import no.nav.testnav.identpool.service.IdentpoolService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.testnav.identpool.util.PersonidentUtil.validate;

@Slf4j
@RestController
@RequestMapping("/api/v1/finneshosskatt")
@RequiredArgsConstructor
public class FinnesHosSkattController {

    private final IdentpoolService identpoolService;

    @PostMapping
    @Operation(description = "tjeneste som DREK bruker for å markere at DNR er i bruk og at det eksisterer hos SKD")
    public void finnesHosSkatt(@RequestBody IdentRequest identRequest) {

        log.info("FinnesHosSkatt har blitt kalt");
        validate(identRequest.getPersonidentifikator());
        identpoolService.registrerFinnesHosSkatt(identRequest.getPersonidentifikator());
    }
}
