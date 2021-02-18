package no.nav.identpool.rs.v1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.identpool.domain.Navn;
import no.nav.identpool.service.NavnepoolService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/fiktive-navn", produces = MediaType.APPLICATION_JSON_VALUE)
public class FiktiveNavnController {

    private final NavnepoolService service;

    @Operation(description = "Returnerer en liste med fiktive navn du kan bruke for testpersoner. "
            + "Dette er adjektiv og substantiv som det ikke er lov å navngi sine barn med, i Norge anno 2018.")
    @GetMapping("/tilfeldig")
    public List<Navn> getRandomNames(@RequestParam(defaultValue = "1") Integer antall) {
        return service.hentTilfeldigeNavn(antall);
    }

    @Operation(description = "Validerer navn mot listen fra skatteetaten med fiktive navn. "
            + "Dette er adjektiv og substantiv som det ikke er lov å navngi sine barn med, i Norge anno 2018.")
    @GetMapping("/valider")
    public Boolean validate(Navn navn) {
        return service.isValid(navn);
    }
}
