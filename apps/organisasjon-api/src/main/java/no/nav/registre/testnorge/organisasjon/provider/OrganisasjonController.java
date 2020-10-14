package no.nav.registre.testnorge.organisasjon.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.organisasjon.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjon.service.OrganisasjonService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjoner")
public class OrganisasjonController {
    private final OrganisasjonService organisasjonService;

    @GetMapping("/{orgnummer}")
    public ResponseEntity<OrganisasjonDTO> getOrgnaisasjon(
            @PathVariable("orgnummer") String orgnummer,
            @RequestHeader("miljo") String miljo
    ) {
        var organisasjon = organisasjonService.getOrganisasjon(orgnummer, miljo);
        if (organisasjon == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(organisasjon.toDTO());
    }

    @PutMapping
    public ResponseEntity<HttpStatus> putOrganisasjon(
            @RequestHeader("miljo") String miljo,
            @RequestBody OrganisasjonDTO organisasjonDTO
    ) {
        organisasjonService.putOrganisasjon(new Organisasjon(organisasjonDTO), miljo);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orgnummer}")
                .buildAndExpand(organisasjonDTO.getOrgnummer())
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
