package no.nav.registre.testnorge.organisasjonfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjon")
public class OrganisasjonController {
    private final OrganisasjonService service;

    @GetMapping
    public ResponseEntity<List<OrganisasjonDTO>> getOrganisasjoner(@RequestParam(required = false) Gruppe gruppe) {
        var organisasjoner = gruppe == null ? service.getOrganisasjoner() : service.getOrganisasjoner(gruppe);
        return ResponseEntity.ok(organisasjoner.stream().map(Organisasjon::toDTO).collect(Collectors.toList()));
    }

    @PutMapping
    public ResponseEntity<?> save(@RequestHeader Gruppe gruppe, @RequestBody OrganisasjonDTO dto) {

        if (dto.getOverenhet() != null && service.getOrganisasjon(dto.getOverenhet()).isEmpty()) {
            var error = String.format(
                    "Kan ikke opprette organisasjon %s fordi overenhet %s ikke finnes i databasen.",
                    dto.getOrgnummer(),
                    dto.getOverenhet()
            );
            log.error(error);
            return ResponseEntity.badRequest().body(error);
        }
        service.save(new Organisasjon(dto), gruppe);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orgnummer}")
                .buildAndExpand(dto.getOrgnummer())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{orgnummer}")
    public ResponseEntity<OrganisasjonDTO> get(@PathVariable String orgnummer) {
        var organisasjon = service.getOrganisasjon(orgnummer);
        return organisasjon
                .map(value -> ResponseEntity.ok(value.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{orgnummer}")
    public ResponseEntity<HttpStatus> delete(@PathVariable String orgnummer) {
        service.delete(orgnummer);
        return ResponseEntity.noContent().build();
    }
}
