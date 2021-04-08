package no.nav.registre.testnorge.organisasjonfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonOrdreService;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjon/{orgnummer}/ordre")
public class OrganisasjonOrdreController {
    private final OrganisasjonService service;
    private final OrganisasjonOrdreService ordreService;

    @PostMapping
    public ResponseEntity<HttpStatus> create(@PathVariable String orgnummer, @RequestHeader String miljo) {
        var organisasjon = service.getOrganisasjon(orgnummer);
        if (organisasjon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var ordreId = ordreService.create(organisasjon.get(), miljo);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ordreId}")
                .buildAndExpand(ordreId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping
    public ResponseEntity<HttpStatus> change(@PathVariable String orgnummer, @RequestHeader String miljo) {
        var organisasjon = service.getOrganisasjon(orgnummer);
        if (organisasjon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var ordreId = ordreService.change(organisasjon.get(), miljo);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ordreId}")
                .buildAndExpand(ordreId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{ordreId}")
    public ResponseEntity<List<ItemDTO>> getStatus(@PathVariable String orgnummer, @PathVariable String ordreId) {
        var status = ordreService.getStatus(ordreId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
