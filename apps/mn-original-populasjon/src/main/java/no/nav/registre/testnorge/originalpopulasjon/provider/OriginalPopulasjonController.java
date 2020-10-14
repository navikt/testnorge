package no.nav.registre.testnorge.originalpopulasjon.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/original-populasjon")
@RestController
public class OriginalPopulasjonController {

    @GetMapping
    public String helloWorld() {
        return "<h2>Hello World</h2>";
    }
}
