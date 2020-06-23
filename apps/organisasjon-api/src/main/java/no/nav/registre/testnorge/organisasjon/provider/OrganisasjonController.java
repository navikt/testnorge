package no.nav.registre.testnorge.organisasjon.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.organisasjon.consumer.EregConsumer;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjoner")
public class OrganisasjonController {

    private final EregConsumer consumer;

    @GetMapping("/{orgnummer}")
    public ResponseEntity<OrganisasjonDTO> getOrgnaisasjon(
            @PathVariable("orgnummer") String orgnummer,
            @RequestHeader("miljo") String miljo
    ) {
        var organisasjon = consumer.getOrganisasjon(orgnummer, miljo);
        return ResponseEntity.ok(organisasjon.toDTO());
    }
}
