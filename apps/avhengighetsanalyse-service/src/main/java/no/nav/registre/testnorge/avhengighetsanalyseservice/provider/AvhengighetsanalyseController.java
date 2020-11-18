package no.nav.registre.testnorge.avhengighetsanalyseservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.testnorge.avhengighetsanalyseservice.adapter.DependenciesAdapter;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@RestController
@RequestMapping("/api/v1/dependencies")
@RequiredArgsConstructor
public class AvhengighetsanalyseController {
    private final DependenciesAdapter adapter;

    @GetMapping
    public ResponseEntity<Set<ApplicationDependenciesDTO>> getDependencies() {
        return ResponseEntity.ok(adapter.getDependencies());
    }
}
