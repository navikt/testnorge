package no.nav.registre.arena.core.provider.rs;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final SyntetiseringService syntetiseringService;

    @GetMapping("/ident")
    @ApiOperation(value = "Legg til spesifikk ident i Arena", notes = "Legger til den spesifiserte identen i Arena")
    public ResponseEntity<String> registrerBrukerIArenaForvalter(@RequestParam String ident,
                                                         @RequestParam String miljoe,
                                                         @RequestParam String avspillergruppeId) {

        return ResponseEntity.ok("OK");
    }

}
