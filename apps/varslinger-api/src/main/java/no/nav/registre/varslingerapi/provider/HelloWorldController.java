package no.nav.registre.varslingerapi.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloWorldController {

    @GetMapping
    public String helloWorld(){
        return "<h1>Hello World!</h1>";
    }

}
