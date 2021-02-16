package no.nav.registre.testnorge.endringsmeldingfrontend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ForwardController {

    @GetMapping()
    public String toRoot() {
        return "forward:/";
    }
}
