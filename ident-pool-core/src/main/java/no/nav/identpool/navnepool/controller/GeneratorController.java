package no.nav.identpool.navnepool.controller;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.navnepool.NavnepoolService;
import no.nav.identpool.navnepool.domain.Navn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/navnepool", produces = MediaType.APPLICATION_JSON_VALUE)
public class GeneratorController {

    private final NavnepoolService service;

    @GetMapping("/tilfeldig")
    public List<Navn> get(@RequestParam(required = false) Integer antall) {
        if (antall == null) {
            antall = 1;
        }
        return service.finnTilfeldigeNavn(antall);
    }
}
