package no.nav.testnav.apps.organisasjontilgangservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjontilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.apps.organisasjontilgangservice.database.repository.OrganisasjonTilgangRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@RestController
@RequestMapping("/api/v1/miljoer")
@RequiredArgsConstructor
public class OrganisasjonMiljoeConsumer {

    private final OrganisasjonTilgangRepository organisasjonTilgangRepository;

    @GetMapping("/organisasjon/{orgnummer}")
    public Mono<OrganisasjonTilgang> getOrganisasjon(@RequestParam("orgnummer") String orgnummer) {

        return organisasjonTilgangRepository.existsByOrganisasjonNummer(orgnummer)
                .flatMap(miljoe -> isTrue(miljoe) ?
                        organisasjonTilgangRepository.findByOrganisasjonNummer(orgnummer) :
                        Mono.error(new NotFoundException(
                                String.format("Organisasjonsnummer %s ble ikke funnet", orgnummer))));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNoSuchElementFoundException(
            NotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}
