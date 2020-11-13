package no.nav.registre.testnorge.identservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.logging.LogExceptions;
import no.nav.registre.testnorge.identservice.service.IdentAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentController {

    private final IdentAppService identAppService;

    @LogExceptions
    @GetMapping(value = "/identer")
    public ResponseEntity<List<String>> checkIdentExistsInProd(@RequestParam List<String> identer) {

        return identAppService.finnLedigeIdenter(identer);
    }
}