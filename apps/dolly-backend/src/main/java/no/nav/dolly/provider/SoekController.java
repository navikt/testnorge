package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Soek;
import no.nav.dolly.domain.resultset.RsSoek;
import no.nav.dolly.service.SoekService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/soek")
public class SoekController {

    private final SoekService soekService;
    private final MapperFacade mapperFacade;

    @GetMapping
    @Operation(description = "Hent lagrede søk for bruker")
    public Flux<RsSoek> getSoek(@RequestParam Soek.SoekType soekType) {

        return soekService.getSoek(soekType)
                .map(soek -> mapperFacade.map(soek, RsSoek.class));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Lagre søk for bruker")
    public Mono<RsSoek> lagreSoek(@RequestParam Soek.SoekType soekType,
                                 @RequestBody String soekVerdi) {

        return soekService.lagreSoek(soekType, soekVerdi)
        .map(soek -> mapperFacade.map(soek, RsSoek.class));
    }
}
