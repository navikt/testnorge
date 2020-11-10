package no.nav.registre.testnorge.identservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.service.IdentAppService;
import no.nav.registre.testnorge.identservice.service.SjekkIdenterService;
import no.nav.registre.testnorge.identservice.testdata.response.IdentMedStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentServiceController {

    private final IdentAppService identAppService;
    private final SjekkIdenterService sjekkIdenterService;

    @GetMapping(value = "/checkIdentInProd/{ident}")
    public IdentMedStatus checkIdent(@PathVariable String ident) {
        return sjekkIdenterService.finnLedigeIdenter(ident);
    }


    @GetMapping(value = "/helloworld")
    public ResponseEntity<String> helloWorld() {
        return identAppService.helloWorld();
    }
}