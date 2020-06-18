package no.nav.registre.testnorge.synt.sykemelding.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/synt-sykemelding")
public class SyntSykemeldingController {

    @GetMapping
    public String hello() {
        return "<h2>hello world</h2>";
    }
}
