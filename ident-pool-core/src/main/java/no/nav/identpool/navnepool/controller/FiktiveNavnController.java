package no.nav.identpool.navnepool.controller;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.navnepool.NavnepoolService;
import no.nav.identpool.navnepool.domain.Navn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/fiktive-navn", produces = MediaType.APPLICATION_JSON_VALUE)
public class FiktiveNavnController {

    private final NavnepoolService service;

    @GetMapping("/tilfeldig")
    public List<Navn> get(@RequestParam(required = false, defaultValue = "1") Integer antall) {
        return service.finnTilfeldigeNavn(antall);
    }

    @GetMapping("/valider")
    public Boolean validate(@RequestBody Navn navn) {
        return service.isValid(navn);
    }
}
