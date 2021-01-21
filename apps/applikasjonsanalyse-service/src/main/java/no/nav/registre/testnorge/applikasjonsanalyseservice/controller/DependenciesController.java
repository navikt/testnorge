package no.nav.registre.testnorge.applikasjonsanalyseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.applikasjonsanalyseservice.adapter.ApplicationAdapter;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.ApplicationInfo;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@RestController
@RequestMapping("/api/v1/dependencies")
@RequiredArgsConstructor
public class DependenciesController {
    private final ApplicationAdapter adapter;

    @GetMapping
    public ResponseEntity<Set<ApplicationDependenciesDTO>> getDependencies() {
        return ResponseEntity.ok(adapter.getAll().stream().map(ApplicationInfo::toDTO).collect(Collectors.toSet()));
    }
}
