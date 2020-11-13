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
import java.util.Set;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentController {

    private final IdentAppService identAppService;

    @LogExceptions
    @GetMapping(value = "/id")
    public ResponseEntity<Set<String>> checkIdentExistsInProd(@RequestParam List<String> ideer) {

        return identAppService.finnLedigeIdenter(ideer);
    }
}