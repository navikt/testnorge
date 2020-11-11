package no.nav.registre.testnorge.identservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.service.IdentAppService;
import no.nav.registre.testnorge.identservice.testdata.response.IdentMedStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identservice")
@RequiredArgsConstructor
public class IdentServiceController {

    private final IdentAppService identAppService;

    @GetMapping(value = "/checkIdentInProd/{ident}")
    public ResponseEntity<IdentMedStatus> checkIdent(@PathVariable String ident) {

        return identAppService.finnLedigeIdenter(ident);
    }
}