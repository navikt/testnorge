package no.nav.registre.testnorge.avhengighetsanalyseservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/avhengigheter")
@RequiredArgsConstructor
public class AvhengighetsanalyseController {

    @GetMapping
    public String helloWorld() {
        return "<h1>Hello world</h1>";
    }

}
