package no.nav.identpool.navnepool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.identpool.navnepool.NavnepoolService;
import no.nav.identpool.navnepool.domain.Navn;

@RestController
@RequestMapping("/navnepool")
public class ValidatorController {
    @Autowired
    private NavnepoolService service;

    @PostMapping("/valider")
    public ResponseEntity<Boolean> validate(@RequestBody Navn navn) {
        return ResponseEntity.ok(service.isValid(navn));
    }
}
