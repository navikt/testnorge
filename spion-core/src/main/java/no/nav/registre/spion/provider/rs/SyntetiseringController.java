package no.nav.registre.spion.provider.rs;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {


    @GetMapping(value = "/generer")
    public String generer() {
        return "Work in progress.";
    }
}
