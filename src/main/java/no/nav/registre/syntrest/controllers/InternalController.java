package no.nav.registre.syntrest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isReady")
    public String isReady() {
        return "OK";
    }

    @GetMapping("/isAlive")
    public String isAlive() {
        return "OK";
    }
}
