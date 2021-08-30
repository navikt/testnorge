package no.nav.registre.testnav.geografiskekodeverkservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class KodeverkController {

    @GetMapping(value = "/hello")
    public String helloWorld() {
        return "Hello world!";
    }
}
