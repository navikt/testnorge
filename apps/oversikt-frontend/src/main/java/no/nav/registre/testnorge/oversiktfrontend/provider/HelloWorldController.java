package no.nav.registre.testnorge.oversiktfrontend.provider;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class HelloWorldController {

    @GetMapping
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("<h1>Hello World</h1>");
    }
}