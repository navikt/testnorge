package no.nav.registre.testnorge.organisasjon.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloWorldController {

    @GetMapping
    public String hello(){
        return "<h1>Hello world!</h1>";
    }
}
