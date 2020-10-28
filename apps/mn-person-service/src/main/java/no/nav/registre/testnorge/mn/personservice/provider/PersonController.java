package no.nav.registre.testnorge.mn.personservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/person")
public class PersonController {

    @GetMapping
    public String getPerson() {
        return "<h1>Hello world</h1>";
    }
}

