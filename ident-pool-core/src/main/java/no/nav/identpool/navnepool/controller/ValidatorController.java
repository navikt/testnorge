package no.nav.identpool.navnepool.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.navnepool.NavnepoolService;
import no.nav.identpool.navnepool.domain.Navn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/navnepool")
public class ValidatorController {

    private final NavnepoolService service;

    @GetMapping("/valider")
    public Boolean validate(@RequestBody Navn navn) {
        return service.isValid(navn);
    }
}
