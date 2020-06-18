package no.nav.registre.testnorge.dependencyanalysis.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.dependencyanalysis.DependencyAnalysis;
import no.nav.registre.testnorge.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@RestController
@RequestMapping("/dependencies")
public class DependenciesController {

    private final DependencyAnalysis dependencyAnalysis;

    public DependenciesController(
            @Value("${application.name:unknown}") String applicationName,
            @Value("${application.basepackage:no.nav.registre}") String basePackage
    ) {
        dependencyAnalysis = new DependencyAnalysis(applicationName, basePackage);
    }

    @GetMapping
    public ResponseEntity<ApplicationDependenciesDTO> get() {
        var analyze = dependencyAnalysis.analyze();
        return ResponseEntity.ok(analyze.toDTO());
    }
}
