package no.nav.registre.testnorge.personexportapi.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/person-export")
public class PersonExportController {

    @GetMapping
    public String helloWorld() {
        return "<h1>Hello world!</h1>";
    }
}
