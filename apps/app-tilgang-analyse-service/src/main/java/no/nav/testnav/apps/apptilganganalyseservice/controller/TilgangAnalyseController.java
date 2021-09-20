package no.nav.testnav.apps.apptilganganalyseservice.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import no.nav.testnav.apps.apptilganganalyseservice.domain.Access;
import no.nav.testnav.apps.apptilganganalyseservice.dto.AccessDTO;
import no.nav.testnav.apps.apptilganganalyseservice.service.TilgangService;

@RestController
@RequestMapping("/api/v1/applications")
public class TilgangAnalyseController {

    private final TilgangService tilgangService;
    private final CacheControl cacheControl;

    public TilgangAnalyseController(TilgangService tilgangService) {
        this.tilgangService = tilgangService;
        this.cacheControl = CacheControl.maxAge(10, TimeUnit.MINUTES).noTransform().mustRevalidate();
    }

    @GetMapping("/{name}/access")
    public ResponseEntity<Mono<AccessDTO>> app(
            @PathVariable String name,
            @RequestParam String owner,
            @RequestParam String repo
    ) {
        var dto = tilgangService
                .fetchAccessBy(name, owner, repo)
                .map(Access::toDTO);
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(dto);
    }

}
