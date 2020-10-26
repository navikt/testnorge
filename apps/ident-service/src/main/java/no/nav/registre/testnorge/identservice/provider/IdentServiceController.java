package no.nav.registre.testnorge.identservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.service.IdentServiceAppService;
import no.nav.registre.testnorge.identservice.service.SjekkIdenterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ident")
public class IdentServiceController {
    private final IdentServiceAppService identServiceAppService;
    private final SjekkIdenterService sjekkIdenterService;

    @PostMapping(value = "/checkpersoner")
    public Set<String> checkIdentList(@RequestBody List<String> identer) {
        return sjekkIdenterService.finnLedigeIdenter(identer);
    }


    @GetMapping("/helloworld")
    public ResponseEntity<String> helloWorld() {
        return identServiceAppService.helloWorld();
    }
}