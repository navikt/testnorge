package no.nav.testnav.apps.brukerservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.dto.TilgangDTO;
import no.nav.testnav.apps.brukerservice.service.TilgangService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tilgang")
@RequiredArgsConstructor
public class TilgangController {

    private final TilgangService tilgangService;

    @GetMapping
    public Mono<TilgangDTO> getBrukereISammeOrganisasjon(String brukerId) {

        return tilgangService.getBrukereISammeOrganisasjon(brukerId);
    }
}
