package no.nav.identpool.navnepool.controller;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.identpool.navnepool.NavnepoolService;
import no.nav.identpool.navnepool.domain.Navn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/fiktive-navn", produces = MediaType.APPLICATION_JSON_VALUE)
public class FiktiveNavnController {

    private final NavnepoolService service;

    @ApiOperation("Returnerer en liste med fiktive navn du kan bruke for testpersoner. "
            + "Dette er adjektiv og substantiv som det ikke er lov å navngi sine barn med, i Norge anno 2018.")
    @GetMapping("/tilfeldig")
    public List<Navn> getRandomNames(@RequestParam(defaultValue = "1") Integer antall) {
        return service.hentTilfeldigeNavn(antall);
    }

    @ApiOperation("Validerer navn mot listen fra skatteetaten med fiktive navn. "
            + "Dette er adjektiv og substantiv som det ikke er lov å navngi sine barn med, i Norge anno 2018.")
    @GetMapping("/valider")
    public Boolean validate(@RequestBody Navn navn) {
        return service.isValid(navn);
    }
}
