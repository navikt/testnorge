package no.nav.identpool.navnepool.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.navnepool.NavnepoolService;
import no.nav.identpool.navnepool.domain.Navn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/navnepool", produces = MediaType.APPLICATION_JSON_VALUE)
public class GeneratorController {

    private final NavnepoolService service;

    @GetMapping("/generer")
    public ResponseEntity<Navn> get() {
        return ResponseEntity.ok(service.finnTilfeldigNavn());
    }
}
