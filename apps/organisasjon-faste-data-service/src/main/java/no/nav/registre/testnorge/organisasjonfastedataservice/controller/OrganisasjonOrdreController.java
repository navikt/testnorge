package no.nav.registre.testnorge.organisasjonfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonOrdreService;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonService;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/ordre")
public class OrganisasjonOrdreController {
    private final OrganisasjonService service;
    private final OrganisasjonOrdreService ordreService;

    @PostMapping("/organisasjon/{orgnummer}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> create(
            @PathVariable String orgnummer,
            @RequestHeader String miljo,
            @RequestHeader Boolean update
    ) {
        return service.getOrganisasjon(orgnummer)
                .flatMap(optOrg -> {
                    if (optOrg.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
                    }

                    var org = optOrg.get();

                    if (org.getEnhetstype().equals("BEDR") && isNull(org.getOverenhet())) {
                        log.error("Organisasjon er av type BEDR men mangler overenhet!");
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
                    }

                    Mono<String> ordreMono = update != null && update
                            ? ordreService.change(org, miljo)
                            : ordreService.create(org, miljo);

                    return ordreMono.then();
                });
    }

    @PostMapping("/organisasjon")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> create(
            @RequestHeader Gruppe gruppe,
            @RequestHeader String miljo,
            @RequestHeader Boolean update
    ) {

        if (gruppe.equals(Gruppe.KUN_TIL_Q1) && !miljo.equals("q1")) {
            log.error("Gruppe kan kun sendes til Q1 da den inneholder reelle orgnummer");
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        if (gruppe.equals(Gruppe.OTP)) {
            log.error("Gruppe kan kun sendes ved bruk av subsets. Den er på over en million linjer og skaper tidvis problemer med tusenvis av duplikate Jenkins jobber");
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        Mono<String> ordreMono = update != null && update
                ? ordreService.change(gruppe, miljo)
                : ordreService.create(gruppe, miljo);

        return ordreMono.then();
    }

    @GetMapping("/{ordreId}")
    public Mono<List<ItemDTO>> getStatus(@PathVariable String ordreId) {
        return ordreService.getStatus(ordreId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
}
