package no.nav.identpool.ident.rest.v1;

import static no.nav.identpool.util.PersonidentifikatorValidatorUtil.valider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.ident.service.IdentpoolService;

@RestController
@RequestMapping("/api/v1/finneshosskatt")
@RequiredArgsConstructor
public class FinnesHosSkattController {

    private final IdentpoolService identpoolService;

    @PostMapping
    public void finnesHosSkatt(
            @RequestParam(value = "personidentifikator") String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        identpoolService.registrerFinnesHosSkatt(personidentifikator);
    }
}
