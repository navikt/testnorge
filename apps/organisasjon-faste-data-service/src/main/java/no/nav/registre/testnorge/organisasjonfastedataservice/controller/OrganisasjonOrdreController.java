package no.nav.registre.testnorge.organisasjonfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonOrdreService;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonService;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<HttpStatus> create(
            @PathVariable String orgnummer,
            @RequestHeader String miljo,
            @RequestHeader Boolean update
    ) {
        var organisasjon = service.getOrganisasjon(orgnummer);
        if (organisasjon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var org = organisasjon.get();

        if (org.getEnhetstype().equals("BEDR") && isNull(org.getOverenhet())) {
            log.error("Organisasjon er av type BEDR men mangler overenhet!");
            return ResponseEntity.badRequest().build();
        }

        var ordreId = update != null && update
                ? ordreService.change(org, miljo)
                : ordreService.create(org, miljo);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/api/v1/ordre/{ordreId}")
                .buildAndExpand(ordreId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/organisasjon")
    public ResponseEntity<HttpStatus> create(
            @RequestHeader Gruppe gruppe,
            @RequestHeader String miljo,
            @RequestHeader Boolean update
    ) {

        if (gruppe.equals(Gruppe.KUN_TIL_Q1) && !miljo.equals("q1")) {
            log.error("Gruppe kan kun sendes til Q1 da den inneholder reelle orgnummer");
            return ResponseEntity.badRequest().build();
        }

        if (gruppe.equals(Gruppe.OTP)) {
            log.error("Gruppe kan kun sendes ved bruk av subsets. Den er på over en million linjer og skaper tidvis problemer med tusenvis av duplikate Jenkins jobber");
            return ResponseEntity.badRequest().build();
        }

        var ordreId = update != null && update
                ? ordreService.change(gruppe, miljo)
                : ordreService.create(gruppe, miljo);


        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ordreId}")
                .buildAndExpand(ordreId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{ordreId}")
    public ResponseEntity<List<ItemDTO>> getStatus(@PathVariable String ordreId) {
        var status = ordreService.getStatus(ordreId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
