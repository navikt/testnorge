package no.nav.registre.testnorge.person.provider;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/person")
public class PersonController {

    @GetMapping
    public ResponseEntity<String> getPerson() {
        return ResponseEntity.ok("<h2>Hello world!</h2>");
    }
}
