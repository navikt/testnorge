package no.nav.registre.testnorge.profil.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profil")
public class ProfilController {

    @GetMapping
    public String helloWorld() {
        return "<h1>hello world!</h1>";
    }
}
