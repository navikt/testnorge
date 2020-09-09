package no.nav.registre.testnorge.avhengighetsanalysefrontend.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;
import no.nav.registre.testnorge.avhengighetsanalysefrontend.consumer.DependencyConsumer;

@RestController
@RequestMapping("/api/v1/dependencies")
@RequiredArgsConstructor
public class DependenciesController {

    private final DependencyConsumer consumer;

    @GetMapping
    public ResponseEntity<Set<ApplicationDependenciesDTO>> getDependencies() {
        return ResponseEntity.ok(consumer.fetchDependencies());
    }
}
