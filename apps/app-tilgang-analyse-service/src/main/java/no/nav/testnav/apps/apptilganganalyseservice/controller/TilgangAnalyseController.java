package no.nav.testnav.apps.apptilganganalyseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.apptilganganalyseservice.domain.Access;
import no.nav.testnav.apps.apptilganganalyseservice.dto.AccessDTO;
import no.nav.testnav.apps.apptilganganalyseservice.service.TilgangService;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class TilgangAnalyseController {

    private final TilgangService tilgangService;

    @GetMapping("/{name}/access")
    public Mono<AccessDTO> app(
            @PathVariable String name,
            @RequestParam String owner,
            @RequestParam String repo
    ) {
        return tilgangService
                .fetchAccessBy(name, owner, repo)
                .map(Access::toDTO);
    }

}
