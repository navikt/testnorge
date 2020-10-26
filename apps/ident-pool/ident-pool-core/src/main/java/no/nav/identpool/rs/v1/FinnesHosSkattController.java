package no.nav.identpool.rs.v1;

import static no.nav.identpool.util.PersonidentUtil.validate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.identpool.rs.v1.support.IdentRequest;
import no.nav.identpool.service.IdentpoolService;

@RestController
@RequestMapping("/api/v1/finneshosskatt")
@RequiredArgsConstructor
public class FinnesHosSkattController {

    private final IdentpoolService identpoolService;

    @PostMapping
    @Operation(description = "tjeneste som DREK bruker for å markere at DNR er i bruk og at det eksisterer hos SKD")
    public void finnesHosSkatt(@RequestBody IdentRequest identRequest) {
        validate(identRequest.getPersonidentifikator());
        identpoolService.registrerFinnesHosSkatt(identRequest.getPersonidentifikator());
    }
}
