package no.nav.registre.testnorge.organisasjonservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonservice.service.OrganisasjonService;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjoner")
public class OrganisasjonController {
    private final OrganisasjonService organisasjonService;

    @GetMapping("/{orgnummer}")
    public Mono<OrganisasjonDTO> getOrgnaisasjon(
            @PathVariable("orgnummer") String orgnummer,
            @RequestHeader("miljo") String miljo
    ) {
        log.info("Henter organisasjon {} fra {}.", orgnummer, miljo);
        return organisasjonService.getOrganisasjon(orgnummer, miljo)
                .map(organisasjon -> organisasjon.toDTO())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
}
