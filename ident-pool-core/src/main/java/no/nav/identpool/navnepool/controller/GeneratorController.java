package no.nav.identpool.navnepool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.identpool.navnepool.NavnepoolService;
import no.nav.identpool.navnepool.domain.Navn;

@RestController
@RequestMapping("/navnepool")
public class GeneratorController {
    @Autowired
    private NavnepoolService service;

    @GetMapping("/generer")
    public ResponseEntity<Navn> get() {
        return ResponseEntity.ok(service.finnTilfeldigNavn());
    }
}
