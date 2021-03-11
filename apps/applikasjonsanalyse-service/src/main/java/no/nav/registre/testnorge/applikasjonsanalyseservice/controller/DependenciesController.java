package no.nav.registre.testnorge.applikasjonsanalyseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.Properties;
import no.nav.registre.testnorge.applikasjonsanalyseservice.service.ApplikasjonsanalyseService;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@RestController
@RequestMapping("/api/v1/dependencies")
@RequiredArgsConstructor
public class DependenciesController {
    private final ApplikasjonsanalyseService service;

    @GetMapping
    public ResponseEntity<Set<ApplicationDependenciesDTO>> getDependencies() {
        var cacheControl
                = CacheControl.maxAge(10, TimeUnit.MINUTES)
                .noTransform()
                .mustRevalidate();
        var applikasjonsanalyseList = service.getApplikasjonsanalyseList(new Properties("navikt", "testnorge"));

        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(applikasjonsanalyseList.toDTO());
    }
}
