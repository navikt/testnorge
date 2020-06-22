package no.nav.registre.testnorge.arbeidsforhold.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("api/v1/hello")
    public String hello(){
        return "<h1>Hello world</h1>";
    }
}
