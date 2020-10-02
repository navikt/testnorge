package no.nav.registre.testnorge.synt.bidragsmeldingapi.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "<h1>Hello world</h1>";
    }
}
