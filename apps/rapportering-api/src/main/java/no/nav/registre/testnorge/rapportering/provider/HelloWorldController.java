package no.nav.registre.testnorge.rapportering.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
@RequiredArgsConstructor
public class HelloWorldController {

    @GetMapping
    public String wold(){
        return "<h1>Hello world</h1>";
    }
}
