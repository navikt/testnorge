package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Soek;
import no.nav.dolly.domain.resultset.RsSoek;
import no.nav.dolly.service.SoekService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/soek")
public class SoekController {

    private final SoekService soekService;
    private final MapperFacade mapperFacade;

    @GetMapping
    @Operation(description = "Hent lagrede søk for bruker")
    public List<RsSoek> getSoek(@RequestParam Soek.SoekType soekType) {

        var soek = soekService.getSoek(soekType);
        return mapperFacade.mapAsList(soek, RsSoek.class);
    }

    @PostMapping
    @Operation(description = "Lagre søk for bruker")
    public RsSoek lagreSoek(@RequestParam Soek.SoekType soekType,
                           @RequestBody String soekVerdi) {

        var soek = soekService.lagreSoek(soekType, soekVerdi);
        return mapperFacade.map(soek, RsSoek.class);
    }
}